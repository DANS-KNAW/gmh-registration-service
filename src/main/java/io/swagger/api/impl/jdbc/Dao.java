package io.swagger.api.impl.jdbc;

import io.swagger.model.NbnLocationsObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static io.swagger.api.impl.jdbc.Dao.SqlResponse.DUPLICATE;
import static io.swagger.api.impl.jdbc.Dao.SqlResponse.FAILURE;
import static io.swagger.api.impl.jdbc.Dao.SqlResponse.OK;
import static io.swagger.api.impl.jdbc.Dao.SqlResponse.UPDATE;

public class Dao {

  public enum SqlResponse {
    OK, FAILURE, DUPLICATE, UPDATE
  }

  private static final Logger logger = LoggerFactory.getLogger(Dao.class);

  public Dao() {
  }

  public static boolean getIdentifier(String identifier) {

    boolean idExists = false;
    //    Get rid of the fragment part:
    String unfragmented = identifier;
    if (identifier != null && identifier.contains("#")) {
      unfragmented = identifier.split("#")[0];
    }

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
        //ignored
      }
    }
    return idExists;
  }

  //TODO: implement rollback: combine transactions in stored procedures
  public static SqlResponse createOrUpdateNbn(NbnLocationsObject nbnLocationsObject) {
    SqlResponse result = null;
    String identifier = nbnLocationsObject.getIdentifier();
    //    Get rid of the fragment part:
    String unfragmented = identifier;
    if (identifier != null && identifier.contains("#")) {
      unfragmented = identifier.split("#")[0];
    }

    logger.info("Inserting in database: " + nbnLocationsObject.toString());
    Connection conn = null;

    try {
      conn = PooledDataSource.getConnection();
      conn.setAutoCommit(false);
      boolean idExists = getIdentifier(identifier);

      for (String location : nbnLocationsObject.getLocations()) {
        String insertNbnStoredProcedureQuery = getIdentifier(identifier) ? "{call insertNbnLocation(?, ?)}" : "{call insertNbnObject(?, ?)}";
        CallableStatement callableStatement = conn.prepareCall(insertNbnStoredProcedureQuery);
        callableStatement.setString(1, unfragmented);
        //TODO: add support for multiple locations
        callableStatement.setString(2, location);
        int sqlresult = (callableStatement.executeUpdate());
        if (sqlresult == 0) {
          if(idExists){
            result = UPDATE;
          }else {
            result = OK;
          }
        }
        else {
          result = FAILURE;
          break;
        }
      }
    }
    catch (SQLException e) {
      if (e instanceof SQLIntegrityConstraintViolationException) {
        result = DUPLICATE;
      }
      else {
        result = FAILURE;
      }

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
    return result;
  }

  public static List<String> getLocations(String identifier) {
    List<String> locations = new ArrayList<>();

    //    Get rid of the fragment part:
    String unfragmented = identifier;
    if (identifier != null && identifier.contains("#")) {
      unfragmented = identifier.split("#")[0];
    }

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
        //ignored
      }
    }
    return nbns;
  }

}




