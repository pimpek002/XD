package me.elsiff.holomedals.datahandler;

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
   private final HoloMedals plugin;
   private final MySQLManager mysql;

   public MySQLMedalDataHandler(HoloMedals plugin) {
      this.plugin = plugin;
      this.mysql = plugin.getMySQLManager();
   }

   public void createTables() {
      this.mysql.executeQuery("CREATE TABLE IF NOT EXISTS `hm_medal_data` (name VARCHAR(50) NOT NULL, display_text VARCHAR(50) NOT NULL, icon TEXT NOT NULL, PRIMARY KEY (name))");
   }

   public List<Medal> loadMedals() {
      List<Medal> medals = new ArrayList<>();
      this.mysql.openConnection();

      try (PreparedStatement statement = this.mysql.getConnection().prepareStatement("SELECT * FROM hm_medal_data")) {
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            String name = resultSet.getString("name");
            String displayText = resultSet.getString("display_text");
            ItemStack icon = BukkitSerialization.itemStackFromBase64(resultSet.getString("icon"));
            Medal medal = new Medal(name, displayText, icon);
            medals.add(medal);
         }
      } catch (SQLException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }

      return medals;
   }

   public void createMedal(Medal medal) {
      this.mysql.openConnection();

      try (PreparedStatement statement = this.mysql.getConnection().prepareStatement("INSERT INTO hm_medal_data (name, display_text, icon) VALUES (?, ?, ?)")) {
         statement.setString(1, medal.getName());
         statement.setString(2, medal.getDisplayText());
         String icon = BukkitSerialization.itemStackToBase64(medal.getIcon());
         statement.setString(3, icon);
         statement.executeUpdate();
      } catch (SQLException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }

   public void updateMedal(Medal medal) {
      this.mysql.openConnection();

      try (PreparedStatement statement = this.mysql.getConnection().prepareStatement("UPDATE hm_medal_data SET display_text=?, icon=? WHERE name=?")) {
         statement.setString(1, medal.getDisplayText());
         String icon = BukkitSerialization.itemStackToBase64(medal.getIcon());
         statement.setString(2, icon);
         statement.setString(3, medal.getName());
         statement.executeUpdate();
      } catch (SQLException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }

   public void removeMedal(Medal medal) {
      this.mysql.openConnection();

      try (PreparedStatement statement = this.mysql.getConnection().prepareStatement("DELETE FROM hm_medal_data WHERE name = ?")) {
         statement.setString(1, medal.getName());
         statement.executeUpdate();
      } catch (SQLException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }
}