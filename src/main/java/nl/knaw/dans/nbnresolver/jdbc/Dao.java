package nl.knaw.dans.nbnresolver.jdbc;

import io.swagger.model.NbnLocationsObject;
import io.swagger.model.User;
import nl.knaw.dans.nbnresolver.response.Conflict;
import nl.knaw.dans.nbnresolver.response.Created;
import nl.knaw.dans.nbnresolver.response.OperationResult;
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

  public static User getUserByCredentials(String username, String password) throws Exception {
    User user = null;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT C.username, C.password, C.org_prefix FROM nbnresolver.credentials C WHERE C.username = ? AND C.password = ?;");
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        throw new InvalidCredentialsException("Provided credentials were invalid");
      }
      else {
        user = new User();
        user.setOrgPrefix(rs.getString(3));
      }
    }

    catch (SQLException e) {
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
      catch (Exception ex) {
      }
    }
    return user;
  }

  public static boolean getIdentifier(String identifier) {
    boolean idExists = false;
    String unfragmented = getUnfragmentedString(identifier);

    logger.info("Getting location(s) for: " + unfragmented);

    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT identifier_id FROM identifier WHERE identifier.identifier_value = ?;");
      pstmt.setString(1, unfragmented);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        idExists = true;
      }
    }
    catch (SQLException e) {
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
      catch (Exception ex) {
      }
    }
    return idExists;
  }

  public static OperationResult createNbn(NbnLocationsObject nbnLocationsObject, int registantId) {
    OperationResult result = null;
    String identifier = nbnLocationsObject.getIdentifier();
    String unfragmented = getUnfragmentedString(identifier);

    logger.info("Inserting in database: " + nbnLocationsObject.toString());
    Connection conn = null;

    boolean idExists = getIdentifier(identifier);
    if (idExists) {
      result = new Conflict(identifier);
    }
    else {
      try {
        conn = PooledDataSource.getConnection();
        conn.setAutoCommit(false);

        for (String location : nbnLocationsObject.getLocations()) {
          String insertNbnStoredProcedureQuery = "{call insertNbnObject(?, ?, ?, ?)}";
          CallableStatement callableStatement = conn.prepareCall(insertNbnStoredProcedureQuery);
          callableStatement.setString(1, unfragmented);
          callableStatement.setString(2, location);
          callableStatement.setInt(3, registantId);
          callableStatement.setBoolean(4, true);
          callableStatement.executeUpdate();
          result = new Created(identifier);
        }
      }
      catch (SQLException e) {
        logger.error("Error inserting nbn object in database..");
      }
      finally {
        try {
          assert conn != null;
          conn.close();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    return result;
  }

  public static void deleteNbn(String nbn) {
    Connection conn = null;
    try {
      conn = PooledDataSource.getConnection();
      conn.setAutoCommit(false);
      String deleteNbnStoredProcedureQuery = "{call deleteNbnObject(?)}";
      CallableStatement callableStatement = conn.prepareCall(deleteNbnStoredProcedureQuery);
      callableStatement.setString(1, nbn);
      callableStatement.executeUpdate();
    }
    catch (SQLException e) {
    }
    finally {
      try {
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ex) {
      }
    }
  }

  public static int getRegistrantIdByOrgPrefix(String org_prefix) {
    int registrantId = 0;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT C.registrant_id FROM nbnresolver.credentials C INNER JOIN registrant R ON R.registrant_id = C.registrant_id WHERE C.org_prefix = ?;");
      pstmt.setString(1, org_prefix);
      rs = pstmt.executeQuery();

      while (rs.next())
        registrantId = rs.getInt("registrant_id");
    }
    catch (SQLException e) {
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
      catch (Exception ex) {
      }
    }
    return registrantId;

  }

  private static String getUnfragmentedString(String identifier) {
    String unfragmented = identifier;
    if (identifier != null && identifier.contains("#")) {
      unfragmented = identifier.split("#")[0];
    }
    return unfragmented;
  }

  public static List<String> getLocations(String identifier) {
    List<String> locations = new ArrayList<>();

    //    Get rid of the fragment part:
    String unfragmented = getUnfragmentedString(identifier);

    logger.info("Getting location(s) for: " + unfragmented);

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
      catch (Exception ex) {
        //ignored
      }
    }
    return locations;
  }

  public static List<String> getNbnByLocation(String location) {
    List<String> nbns = new ArrayList<>();

    logger.info("Getting nbn(s) for: " + location);

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
      catch (Exception ex) {
      }
    }
    return nbns;
  }

  public static User getUserByToken(String token) throws Exception {
    User user;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = PooledDataSource.getConnection();
      pstmt = conn.prepareStatement("SELECT  C.org_prefix FROM nbnresolver.credentials C WHERE C.token = ?;");
      pstmt.setString(1, token);
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        throw new Exception("Invalid Token");
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
      catch (Exception ex) {
        //ignored
      }
    }
    return user;
  }

  public static void registerToken(String token, String username, String password) throws Exception {
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
        throw new Exception("Token could not be persisted");
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
      catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

}







