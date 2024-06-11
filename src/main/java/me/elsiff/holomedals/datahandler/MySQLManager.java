package me.elsiff.holomedals.datahandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import me.elsiff.holomedals.HoloMedals;

public class MySQLManager {
   private final HoloMedals plugin;
   private final String host;
   private final String database;
   private final String username;
   private final String password;
   private final int port;
   private Connection connection;

   public MySQLManager(HoloMedals var1) {
      this.plugin = var1;
      this.host = var1.getConfig().getString("mysql.host");
      this.database = var1.getConfig().getString("mysql.database");
      this.username = var1.getConfig().getString("mysql.username");
      this.password = var1.getConfig().getString("mysql.password");
      this.port = var1.getConfig().getInt("mysql.port");
   }

   public void openConnection() {
      try {
         if (this.connection != null && !this.connection.isClosed()) {
            return;
         }

         synchronized(this) {
            if (this.connection != null && !this.connection.isClosed()) {
               return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            Properties var2 = new Properties();
            var2.setProperty("user", this.username);
            var2.setProperty("password", this.password);
            var2.setProperty("characterEncoding", "utf-8");
            var2.setProperty("useUnicode", "true");
            var2.setProperty("autoReconnect", "true");
            var2.setProperty("useSSL", "false");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, var2);
         }
      } catch (ClassNotFoundException | SQLException var5) {
         this.plugin.getLogger().severe(var5.getMessage());
      }

   }

   public void closeConnection() {
      try {
         if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
         }
      } catch (SQLException var2) {
         this.plugin.getLogger().severe(var2.getMessage());
      }

   }

   public void executeQuery(String var1) {
      this.openConnection();

      try {
         Statement var2 = this.connection.createStatement();
         Throwable var3 = null;

         try {
            var2.executeUpdate(var1);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (SQLException var15) {
         this.plugin.getLogger().severe(var15.getMessage());
      }

   }

   public Connection getConnection() {
      return this.connection;
   }
}
