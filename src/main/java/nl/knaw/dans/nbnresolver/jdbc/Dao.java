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

import io.swagger.model.LtpLocation;
import io.swagger.model.NbnLocationsObject;
import nl.knaw.dans.nbnresolver.model.User;
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

  /**
   * @param identifier  URN:NBN
   * @param registantId Local-DB Registrant Identifier
   * @return True if the (unfragmented)identifier exists for this registrant in the DB. False otherwise.
   * @throws SQLException
   */
  public static boolean isRegistrantIdentifier(String identifier, int registantId) throws SQLException {
    boolean idExists = false;
    String unfragmented = getUnfragmentedString(identifier);

    Connection conn = PooledDataSource.getConnection();
    PreparedStatement pstmt = conn.prepareStatement("SELECT I.identifier_id FROM identifier I INNER JOIN identifier_registrant IR ON I.identifier_id=IR.identifier_id WHERE I.identifier_value = ? AND IR.registrant_id = ?;");
    pstmt.setString(1, unfragmented);
    pstmt.setInt(2, registantId);
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

  /**
   * @param identifier Fragmented URN:NBN
   * @return True if the (unfragmented)identifier exists in the identifier table only, so even if no locations are associated with it. False otherwise.
   * @throws SQLException
   */
  public static boolean identifierExists(String identifier) throws SQLException {
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

  /**
   * @param identifier Fragmented URN:NBN
   * @return True if the (unfragmented)identifier has at least one location associated with it, that makes it resolvable. False if it has none, or does not exist at all.
   * @throws SQLException
   */
  public static boolean isResolvableIdentifier(String identifier) throws SQLException {
    boolean isResolvable = false;
    String unfragmented = getUnfragmentedString(identifier);
    Connection conn = PooledDataSource.getConnection();
    PreparedStatement pstmt = conn.prepareStatement("SELECT I.identifier_id FROM identifier I INNER JOIN identifier_location IL ON I.identifier_id=IL.identifier_id WHERE I.identifier_value = ?;");
    pstmt.setString(1, unfragmented);
    ResultSet rs = pstmt.executeQuery();
    if (rs.next()) {
      isResolvable = true;
    }
    try {
      rs.close();
      pstmt.close();
      conn.close();
    }
    catch (Exception ignored) {
    }
    return isResolvable;
  }

  /**
   * @param nbnLocationsObject NbnLocationsObject object
   * @param registantId        Local-DB Registrant Identifier
   * @throws SQLException If the registrant is an LTA, the given locations will be marked as 'Failover'.
   */
  public static void createNbn(NbnLocationsObject nbnLocationsObject, int registantId) throws SQLException {
    String identifier = nbnLocationsObject.getIdentifier();
    String unfragmented_id = getUnfragmentedString(identifier);
    Connection conn = PooledDataSource.getConnection();
    conn.setAutoCommit(false);

    for (String location : nbnLocationsObject.getLocations()) {
      CallableStatement callableStatement = conn.prepareCall("{call insertNbnLocation(?, ?, ?)}");
      callableStatement.setString(1, unfragmented_id);
      callableStatement.setString(2, location);
      callableStatement.setInt(3, registantId);
      callableStatement.executeUpdate();
    }
    try {
      conn.close();
      logger.info("Inserted in database: " + nbnLocationsObject.toString());
    }
    catch (Exception e) {
      logger.error("Inserting in database: " + nbnLocationsObject.toString() + " went wrong. Error: " + e.toString());
    }
  }

  /**
   * @param nbnLocationsObject NbnLocationsObject object
   * @param registantId        Local-DB Registrant Identifier
   * @param isLTP              Is the registrant(Id) an LTA or not.
   * @throws SQLException If the registrant is an LTA, the given locations will be marked as 'Failover'.
   */
  public static void addNbnLocations(NbnLocationsObject nbnLocationsObject, int registantId, boolean isLTP) throws SQLException {
    String identifier = nbnLocationsObject.getIdentifier();
    String unfragmented_id = getUnfragmentedString(identifier);
    Connection conn = PooledDataSource.getConnection();
    conn.setAutoCommit(false);

    for (String location : nbnLocationsObject.getLocations()) {
      CallableStatement callableStatement = conn.prepareCall("{call addNbnLocation(?, ?, ?, ?)}");
      callableStatement.setString(1, unfragmented_id);
      callableStatement.setString(2, location);
      callableStatement.setInt(3, registantId);
      callableStatement.setBoolean(4, isLTP);
      callableStatement.executeUpdate();
    }
    try {
      conn.close();
      logger.info("Inserted in database: " + nbnLocationsObject.toString());
    }
    catch (Exception e) {
      logger.error("Inserting in database: " + nbnLocationsObject.toString() + " went wrong. Error: " + e.toString());
    }
  }

  //  /**
  //   * @param registantId Local-DB Registrant Identifier
  //   * @param identifier  URN:NBN (fragmented)
  //   * @throws SQLException Deletes URN:NBN locations registered by this Registrant.
  //   */
  //  public static void deleteNbnLocationsByRegId(int registantId, String identifier) throws SQLException {
  //    String unfragmented_id = getUnfragmentedString(identifier);
  //    Connection conn = PooledDataSource.getConnection();
  //    conn.setAutoCommit(false);
  //    CallableStatement callableStatement = conn.prepareCall("{call deleteNbnLocationsByRegistrant(?, ?)}");
  //    callableStatement.setString(1, unfragmented_id);
  //    callableStatement.setInt(2, registantId);
  //    callableStatement.executeUpdate();
  //    try {
  //      conn.close();
  //      logger.info("Deleted locations from DB for registrant_id: " + registantId + " and NBN identifier: " + unfragmented_id);
  //    }
  //    catch (Exception e) {
  //      logger.error("Deleting locations from DB for registrant_id: " + registantId + " and NBN identifier: " + unfragmented_id + " went wrong. Error: " + e.toString());
  //    }
  //  }

  /**
   * @param registantId Local-DB Registrant Identifier
   * @param identifier  URN:NBN (fragmented)
   * @throws SQLException Deletes URN:NBN locations registered by this Registrant.
   */
  public static void deleteNbnLocationsByRegistrantId(int registantId, String identifier, boolean isLTP) throws SQLException {
    String unfragmented_id = getUnfragmentedString(identifier);
    Connection conn = PooledDataSource.getConnection();
    conn.setAutoCommit(false);
    CallableStatement callableStatement = conn.prepareCall("{call deleteNbnLocationsByRegistrantId(?, ?, ?)}");
    callableStatement.setString(1, unfragmented_id);
    callableStatement.setInt(2, registantId);
    callableStatement.setBoolean(3, isLTP);
    callableStatement.executeUpdate();
    try {
      conn.close();
      logger.info("Deleted locations from DB for registrant_id: " + registantId + " and NBN identifier: " + unfragmented_id);
    }
    catch (Exception e) {
      logger.error("Deleting locations from DB for registrant_id: " + registantId + " and NBN identifier: " + unfragmented_id + " went wrong. Error: " + e.toString());
    }
  }

  /**
   * @param nbn_org_prefix The URN:NBN prefix of the Registrant as issued by the KB.
   * @return Local-DB Registrant Identifier
   * @throws SQLException
   */
  public static int getRegistrantIdByOrgPrefix(String nbn_org_prefix) throws SQLException {
    int registrantId = 0;
    Connection conn = PooledDataSource.getConnection();
    PreparedStatement pstmt = conn.prepareStatement("SELECT registrant_id FROM registrant WHERE prefix = ?;");
    pstmt.setString(1, nbn_org_prefix);
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

  /**
   * @param identifier Fragmented URN:NBN
   * @param includeLTP Whether or not to include the failover locations for this URN:NBN.
   * @return A list of locations registered for this URN:NBN, sorted by modificationdate, newest first.
   */
  public static List<LtpLocation> getLocations(String identifier, boolean includeLTP) {
    List<LtpLocation> locations = new ArrayList<>();
    String unfragmented = getUnfragmentedString(identifier);
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      conn = PooledDataSource.getConnection();
      if (includeLTP) {
        pstmt = conn.prepareStatement("SELECT L.location_url, IL.isFailover FROM identifier I JOIN identifier_location IL ON I.identifier_id = IL.identifier_id JOIN location L ON L.location_id = IL.location_id WHERE I.identifier_value=? ORDER BY IL.isFailover, IL.last_modified DESC");
      }
      else {
        pstmt = conn.prepareStatement("SELECT L.location_url, IL.isFailover FROM identifier I JOIN identifier_location IL ON I.identifier_id = IL.identifier_id JOIN location L ON L.location_id = IL.location_id WHERE I.identifier_value=? AND IL.isFailover=0 ORDER BY IL.isFailover, IL.last_modified DESC");
      }
      pstmt.setString(1, unfragmented);
      rs = pstmt.executeQuery();
      while (rs.next()) {
        LtpLocation loc = new LtpLocation();
        loc.setUri(rs.getString(1));
        loc.setLtp(rs.getBoolean(2));
        locations.add(loc);
      }
    }
    catch (SQLException e) {
      logger.error("Locations could not be retrieved from database for Nbn: " + identifier + ". Error: " + e.toString());
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

  /**
   * @param location Location/URI string
   * @return List of URN:NBN's for the given location. Empty List if no registered URN:NBN has been found for the location.
   */
  public static List<String> getNbnsByLocation(String location) {
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
      logger.error("Nbn could not be retrieved from database for location: " + location + ". Error: " + e.toString());
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

  /**
   * @param username Username
   * @param password Password for this user
   * @return User object, that knows about URN:NBN prefix and LTPA. Null if no user is found.
   * @throws InvalidCredentialsException
   * @throws SQLException
   */
  public static User getUserByCredentials(String username, String password) throws InvalidCredentialsException, SQLException {
    User user = null;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT R.prefix, R.isLTP, R.registrant_id, R.registrant_groupid FROM registrant R inner join credentials C ON R.registrant_id = C.registrant_id WHERE C.username = ? AND C.password = ?;");
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        logger.warn("Provided credentials were invalid for username: " + username);
        throw new InvalidCredentialsException("Provided credentials were invalid");
      }
      else {
        user = new User();
        user.setOrgPrefix(rs.getString(1));
        user.setLTP(rs.getBoolean(2));
        user.setRegistrantId(rs.getInt(3));
        user.setRegistrantGroupId(rs.getString(4));
        logger.debug("Called getUserByCredentials: " + username);
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

  /**
   * @param token String that represents the JSON Web Token (JWT).
   * @return User object, that knows about URN:NBN prefix and LTPA. Null if no user is found.
   * @throws InvalidTokenException
   * @throws SQLException
   */
  public static User getUserByToken(String token) throws InvalidTokenException, SQLException {
    User user;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT R.prefix, R.isLTP, R.registrant_id, R.registrant_groupid FROM registrant R inner join credentials C ON R.registrant_id = C.registrant_id WHERE C.token = ?;");
      pstmt.setString(1, token);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        logger.warn("Unauthorized token: " + token);
        throw new InvalidTokenException("Invalid Token");
      }
      else {
        user = new User();
        user.setOrgPrefix(rs.getString(1));
        user.setLTP(rs.getBoolean(2));
        user.setRegistrantId(rs.getInt(3));
        user.setRegistrantGroupId(rs.getString(4));
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

  /**
   * @param new_jwttoken New JSON Web Token (JWT) string that needs to be registered with a given user.
   * @param username     Name of the existing user.
   * @param password     Password of the existing user.
   * @throws SQLException If the token can not be persisted.
   */
  public static void registerToken(String new_jwttoken, String username, String password) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("UPDATE credentials C SET C.token = ? WHERE C.username = ? AND C.password = ?;");
      pstmt.setString(1, new_jwttoken);
      pstmt.setString(2, username);
      pstmt.setString(3, password);
      int resultCode = pstmt.executeUpdate();
      if (resultCode != 1) {
        logger.error("Error registering new JWT token " + new_jwttoken + " for: " + username);
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

  public static boolean isRegistrantFailoverLocation(String location, String orgPrefix) {
    boolean isFailover = false;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      int registrant_id = getRegistrantIdByOrgPrefix(orgPrefix);
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT IL.isFailover FROM identifier_location IL JOIN location L ON IL.location_id = L.location_id JOIN location_registrant LR ON L.location_id = LR.location_id WHERE IL.isFailover = '1' AND L.location_url = ? AND LR.registrant_id = ?;");
      pstmt.setString(1, location);
      pstmt.setInt(2, registrant_id);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        isFailover = true;
      }
    }
    catch (SQLException e) {
      logger.error("Could not retrieve isFailover from database for location: " + location + ". Error: " + e.toString());
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
    return isFailover;
  }

  public static boolean hasLtpLocation(String identifier, String orgPrefix) {
    boolean hasFailover = false;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      int registrant_id = getRegistrantIdByOrgPrefix(orgPrefix);
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT IL.isFailover FROM identifier_location IL JOIN identifier I ON IL.identifier_id = I.identifier_id JOIN identifier_registrant IR ON I.identifier_id = IR.identifier_id WHERE IL.isFailover = '1' AND I.identifier_value = ? AND IR.registrant_id = ?;");
      pstmt.setString(1, identifier);
      pstmt.setInt(2, registrant_id);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        hasFailover = true;
      }
    }
    catch (SQLException e) {
      logger.error("Could not retrieve isFailover from database for identifier: " + identifier + ". Error: " + e.toString());
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
    return hasFailover;
  }
}
