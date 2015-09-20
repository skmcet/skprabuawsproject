//$Id$
package com.aws.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import com.mysql.jdbc.Driver;

public class DataBaseConnection {

    private String database = null;
    private String username = null;
    private String password = null;
    private static final DataBaseConnection dataBaseConectionInstance = new DataBaseConnection();

    private DataBaseConnection() {
        Properties prop = new Properties();
        InputStream input = null;
	//input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        //if(input != null)
        //{
        try {
            //prop.load(input);
            //this.database = prop.getProperty("database");
            //this.username = prop.getProperty("username");
            //this.password = prop.getProperty("password");
            this.database = "jdbc:mysql://localhost/dbtestdb";
            this.username = "root";
            this.password = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
    }

    public static DataBaseConnection getInstance() {
        return DataBaseConnection.dataBaseConectionInstance;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(database, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
