package org.example.testfx;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    public static Connection connect_DB(String db) {
        String url = "jdbc:mysql://localhost/" + db ;
        String id = "root";
        String pass = "1234";
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,id,pass);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    public static void main(String[] args) {
        Connection con = connect_DB("cbt2");
    }
}
