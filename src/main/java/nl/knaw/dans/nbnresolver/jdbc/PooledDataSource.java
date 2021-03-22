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

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PooledDataSource {

  private static final BasicDataSource DS = new BasicDataSource();
  private static final Logger logger = LoggerFactory.getLogger(PooledDataSource.class);
  private static final String PROPERTIES_FILE = "application.properties";
  private static final Properties PROPERTIES = new Properties();

  static {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIES_FILE);

    if (propertiesFile == null) {
      throw new RuntimeException("Properties file '" + PROPERTIES_FILE + "' is missing in classpath.");
    }

    try {
      PROPERTIES.load(propertiesFile);
    }
    catch (IOException e) {
      logger.error("Cannot load properties file '" + PROPERTIES_FILE + "'.", e);
    }

    DS.setUrl(PROPERTIES.getProperty("MYSQL_DB_URL"));
    DS.setUsername(PROPERTIES.getProperty("MYSQL_DB_USERNAME"));
    DS.setPassword(PROPERTIES.getProperty("MYSQL_DB_PASSWORD"));
    DS.setMinIdle(2);
    DS.setMaxIdle(10);
    DS.setMaxActive(15);

  }

  public static Connection getConnection() throws SQLException {
    printDbStatus();
    return DS.getConnection();
  }

  private PooledDataSource() {
  }

  // This method is used to print the Connection Pool status:
  private static void printDbStatus() {
    logger.debug("Max.: " + DS.getMaxActive() + "; Active: " + DS.getNumActive() + "; Idle: " + DS.getNumIdle());
  }

  public static void testDBConnection() {
    logger.debug("Testing DB connection");
    ResultSet rs = null;
    Connection conn = null;
    Statement stmt = null;

    try {
      conn = PooledDataSource.getConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery("SELECT 'test'");
    }
    catch (SQLException e) {
      logger.error("Testing DB connection went wrong...");
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
