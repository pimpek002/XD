package me.elsiff.holomedals.datahandler;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import me.elsiff.holomedals.util.BukkitSerialization;
import org.bukkit.inventory.ItemStack;

public class MySQLMedalDataHandler implements MedalDataHandler {
   private static final String TABLE_MEDAL_DATA = "hm_medal_data";
   private final HoloMedals plugin;
   private final MySQLManager mysql;

   public MySQLMedalDataHandler(HoloMedals var1) {
      this.plugin = var1;
      this.mysql = var1.getMySQLManager();
   }

   public void createTables() {
      this.mysql.executeQuery("CREATE TABLE IF NOT EXISTS `hm_medal_data` (name VARCHAR(50) NOT NULL,display_text VARCHAR(50) NOT NULL,icon TEXT NOT NULL,PRIMARY KEY (name))");
   }

   public List<Medal> loadMedals() {
      ArrayList var1 = new ArrayList();
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("SELECT * FROM hm_medal_data");
         Throwable var3 = null;

         try {
            ResultSet var4 = var2.executeQuery();

            while(var4.next()) {
               String var5 = var4.getString("name");
               String var6 = var4.getString("display_text");
               ItemStack var7 = BukkitSerialization.itemStackFromBase64(var4.getString("icon"));
               Medal var8 = new Medal(var5, var6, var7);
               var1.add(var8);
            }
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException | SQLException var19) {
         this.plugin.getLogger().severe(var19.getMessage());
      }

      return var1;
   }

   public void createMedal(Medal var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("INSERT INTO hm_medal_data (name, display_text, icon) VALUES (?, ?, ?)");
         Throwable var3 = null;

         try {
            var2.setString(1, var1.getName());
            var2.setString(2, var1.getDisplayText());
            String var4 = BukkitSerialization.itemStackToBase64(var1.getIcon());
            var2.setString(3, var4);
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

   public void updateMedal(Medal var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("UPDATE hm_medal_data SET display_text=?, icon=? WHERE id=?");
         Throwable var3 = null;

         try {
            var2.setString(1, var1.getDisplayText());
            String var4 = BukkitSerialization.itemStackToBase64(var1.getIcon());
            var2.setString(2, var4);
            var2.setString(3, var1.getName());
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

   public void removeMedal(Medal var1) {
      this.mysql.openConnection();

      try {
         PreparedStatement var2 = this.mysql.getConnection().prepareStatement("DELETE FROM hm_medal_data WHERE name = ?");
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
}
