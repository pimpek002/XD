package me.elsiff.holomedals.datahandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;

public class MySQLUserDataHandler implements UserDataHandler {
   private static final String TABLE_USERDATA = "hm_userdata";
   private static final String TABLE_MEDALS = "hm_medals";
   private final HoloMedals plugin;
   private final MySQLManager mysql;

   public MySQLUserDataHandler(HoloMedals var1) {
      this.plugin = var1;
      this.mysql = var1.getMySQLManager();
   }

   public void createTables() {
      if (this.isOutdated()) {
         this.mysql.executeQuery("ALTER TABLE hm_userdata MODIFY display TEXT");
      }

      this.mysql.executeQuery("CREATE TABLE IF NOT EXISTS `hm_userdata` (id VARCHAR(36) NOT NULL,display TEXT,PRIMARY KEY (id))");
      this.mysql.executeQuery("CREATE TABLE IF NOT EXISTS `hm_medals` (id VARCHAR(36) NOT NULL,medal VARCHAR(50) NOT NULL)");
   }

   private boolean isOutdated() {
      this.mysql.openConnection();

      try {
         PreparedStatement var1 = this.mysql.getConnection().prepareStatement("SELECT * FROM hm_userdata");
         Throwable var2 = null;

         boolean var6;
         try {
            ResultSet var3 = var1.executeQuery();
            ResultSetMetaData var4 = var3.getMetaData();
            String var5 = var4.getColumnTypeName(1);
            var6 = var5.equals("VARCHAR");
         } catch (Throwable var16) {
            var2 = var16;
            throw var16;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var15) {
                     var2.addSuppressed(var15);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var6;
      } catch (SQLException var18) {
         this.plugin.getLogger().severe(var18.getMessage());
         return false;
      }
   }

   public void giveMedal(UUID var1, Medal var2) {
      this.mysql.openConnection();

      try {
         PreparedStatement var3 = this.mysql.getConnection().prepareStatement("INSERT INTO hm_medals (id, medal) VALUES (?, ?)");
         Throwable var4 = null;

         try {
            var3.setString(1, var1.toString());
            var3.setString(2, var2.getName());
            var3.executeUpdate();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (SQLException var16) {
         this.plugin.getLogger().severe(var16.getMessage());
      }

   }

   public void takeMedal(UUID var1, Medal var2) {
      this.mysql.openConnection();

      try {
         PreparedStatement var3 = this.mysql.getConnection().prepareStatement("DELETE FROM hm_medals WHERE id = ? AND medal = ?");
         Throwable var4 = null;

         try {
            var3.setString(1, var1.toString());
            var3.setString(2, var2.getName());
            var3.executeUpdate();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (SQLException var16) {
         this.plugin.getLogger().severe(var16.getMessage());
      }

   }

   public List<Medal> getMedals(UUID var1) {
      ArrayList var2 = new ArrayList();
      this.mysql.openConnection();

      try {
         PreparedStatement var3 = this.mysql.getConnection().prepareStatement("SELECT * FROM hm_medals WHERE id = ?");
         Throwable var4 = null;

         try {
            var3.setString(1, var1.toString());
            ResultSet var5 = var3.executeQuery();

            while(var5.next()) {
               String var6 = var5.getString("medal");
               Medal var7 = this.plugin.getMedalManager().getMedal(var6);
               var2.add(var7);
            }
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (SQLException var18) {
         this.plugin.getLogger().severe(var18.getMessage());
      }

      return var2;
   }

   public void takeAllMedals(Medal var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("DELETE FROM hm_medals WHERE medal = ?");
         Throwable var3 = null;

         try {
            var2.setString(1, var1.getName());
            var2.executeUpdate();
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

   public String getDisplayMedals(UUID var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("SELECT * FROM hm_userdata WHERE id = ?");
         Throwable var3 = null;

         String var5;
         try {
            var2.setString(1, var1.toString());
            ResultSet var4 = var2.executeQuery();
            if (!var4.next()) {
               this.addUserData(var1);
               return null;
            }

            var5 = var4.getString("display");
         } catch (Throwable var16) {
            var3 = var16;
            throw var16;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var15) {
                     var3.addSuppressed(var15);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var5;
      } catch (SQLException var18) {
         this.plugin.getLogger().severe(var18.getMessage());
         return null;
      }
   }

   private void addUserData(UUID var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("INSERT INTO hm_userdata (id, display) VALUES (?, ?)");
         Throwable var3 = null;

         try {
            var2.setString(1, var1.toString());
            var2.setString(2, (String)null);
            var2.executeUpdate();
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

   public void setDisplayMedals(UUID var1, String var2) {
      this.mysql.openConnection();

      try {
         PreparedStatement var3 = this.mysql.getConnection().prepareStatement("UPDATE hm_userdata SET display=? WHERE id=?");
         Throwable var4 = null;

         try {
            var3.setString(1, var2);
            var3.setString(2, var1.toString());
            var3.executeUpdate();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (SQLException var16) {
         this.plugin.getLogger().severe(var16.getMessage());
      }

   }
}
