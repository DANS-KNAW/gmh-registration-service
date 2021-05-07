/**
 * Copyright (C) 2018 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.nbnresolver.jdbc;

import io.swagger.model.NbnLocationsObject;
import io.swagger.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dao {

  private static final Logger logger = LoggerFactory.getLogger(Dao.class);

  public Dao() {
  }

  public static boolean getIdentifier(String identifier) throws SQLException {
    boolean idExists = false;
    String unfragmented = getUnfragmentedString(identifier);

    Connection conn = PooledDataSource.getConnection();
    PreparedStatement pstmt = conn.prepareStatement("SELECT identifier_id FROM identifier WHERE identifier.identifier_value = ?;");
    pstmt.setString(1, unfragmented);
    ResultSet rs = pstmt.executeQuery();

    if (rs.next()) {
      idExists = true;
    }

    try {
      rs.close();
      pstmt.close();
      conn.close();
    }
    catch (Exception ignored) {
    }
    return idExists;
  }

  public static void createNbn(NbnLocationsObject nbnLocationsObject, int registantId) throws SQLException {
    String identifier = nbnLocationsObject.getIdentifier();
    String unfragmented = getUnfragmentedString(identifier);

    logger.info("Inserting in database: " + nbnLocationsObject.toString());

    Connection conn = PooledDataSource.getConnection();
    conn.setAutoCommit(false);

    for (String location : nbnLocationsObject.getLocations()) {
      String insertNbnStoredProcedureQuery = "{call insertNbnObject(?, ?, ?, ?)}";
      CallableStatement callableStatement = conn.prepareCall(insertNbnStoredProcedureQuery);
      callableStatement.setString(1, unfragmented);
      callableStatement.setString(2, location);
      callableStatement.setInt(3, registantId);
      //TODO: set isFailover to false for first location
      callableStatement.setBoolean(4, true);
      callableStatement.executeUpdate();
    }

    try {
      conn.close();
    }
    catch (Exception ignored) {
    }
  }

  public static void deleteNbn(String identifier) throws SQLException {

    Connection conn = PooledDataSource.getConnection();
    conn.setAutoCommit(false);
    String deleteNbnStoredProcedureQuery = "{call deleteNbnObject(?)}";
    CallableStatement callableStatement = conn.prepareCall(deleteNbnStoredProcedureQuery);
    callableStatement.setString(1, identifier);
    callableStatement.executeUpdate();

    try {
      conn.close();
    }
    catch (Exception ignored) {
    }
  }

  public static int getRegistrantIdByOrgPrefix(String org_prefix) throws SQLException {
    int registrantId = 0;

    Connection conn = PooledDataSource.getConnection();
    PreparedStatement pstmt = conn.prepareStatement("SELECT C.registrant_id FROM nbnresolver.credentials C INNER JOIN registrant R ON R.registrant_id = C.registrant_id WHERE C.org_prefix = ?;");
    pstmt.setString(1, org_prefix);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next())
      registrantId = rs.getInt("registrant_id");

    try {
      rs.close();
      pstmt.close();
      conn.close();
    }
    catch (Exception ex) {
      logger.error(ex.getMessage());
    }
    return registrantId;
  }

  public static List<String> getLocations(String identifier) {
    List<String> locations = new ArrayList<>();

    String unfragmented = getUnfragmentedString(identifier);

    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT L.location_url, IL.isFailover FROM identifier I JOIN identifier_location IL ON I.identifier_id = IL.identifier_id JOIN location L ON L.location_id = IL.location_id WHERE I.identifier_value=? ORDER BY IL.isFailover, IL.last_modified DESC");
      pstmt.setString(1, unfragmented);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        locations.add(rs.getString(1));
      }
    }
    catch (SQLException e) {
      logger.error("Locations could not be retrieved from database for Nbn: " + identifier);
      logger.debug(e.getMessage());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ignored) {
      }
    }
    return locations;
  }

  public static List<String> getNbnByLocation(String location) {
    List<String> nbns = new ArrayList<>();

    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT I.identifier_value FROM identifier I JOIN identifier_location IL ON I.identifier_id = IL.identifier_id JOIN location L ON L.location_id = IL.location_id WHERE L.location_url = ? ;");
      pstmt.setString(1, location);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        nbns.add(rs.getString(1));
      }
    }
    catch (SQLException e) {
      logger.error("Nbn could not be retrieved from database for location: " + location);
      logger.debug(e.getMessage());

    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ignored) {
      }
    }
    return nbns;
  }

  public static User getUserByCredentials(String username, String password) throws InvalidCredentialsException, SQLException {
    User user = null;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT C.org_prefix FROM nbnresolver.credentials C WHERE C.username = ? AND C.password = ?;");
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        throw new InvalidCredentialsException("Provided credentials were invalid");
      }
      else {
        user = new User();
        user.setOrgPrefix(rs.getString(1));
      }
    }
    
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ignored) {
      }
    }
    return user;
  }

  public static User getUserByToken(String token) throws InvalidTokenException, SQLException {
    User user;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT C.org_prefix FROM nbnresolver.credentials C WHERE C.token = ?;");
      pstmt.setString(1, token);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        throw new InvalidTokenException("Invalid Token");
      }
      else {
        user = new User();
        user.setOrgPrefix(rs.getString(1));
      }
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ignored) {
      }
    }
    return user;
  }

  public static void registerToken(String token, String username, String password) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("UPDATE credentials C SET C.token = ? WHERE C.username = ? AND C.password = ?;");
      pstmt.setString(1, token);
      pstmt.setString(2, username);
      pstmt.setString(3, password);
      int resultCode = pstmt.executeUpdate();
      if (resultCode != 1) {
        throw new SQLException("Token could not be persisted");
      }
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException ignored) {
      }
    }
  }

  private static String getUnfragmentedString(String identifier) {
    String unfragmented = identifier;
    if (identifier != null && identifier.contains("#")) {
      unfragmented = identifier.split("#")[0];
    }
    return unfragmented;
  }

}







