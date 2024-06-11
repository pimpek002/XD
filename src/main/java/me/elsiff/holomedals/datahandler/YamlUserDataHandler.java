package me.elsiff.holomedals.datahandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlUserDataHandler implements UserDataHandler {
   private final HoloMedals plugin;
   private final File configFile;
   private final FileConfiguration config;

   public YamlUserDataHandler(HoloMedals plugin) {
      this.plugin = plugin;
      this.configFile = new File(plugin.getDataFolder(), "userdata.yml");
      if (!this.configFile.exists()) {
         try {
            boolean created = this.configFile.createNewFile();
            if (!created) {
               plugin.getLogger().warning("Failed to create userdata.yml!");
            }
         } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
         }
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
   }

   private void setMedals(UUID playerId, List<Medal> medals) {
      String displayMedalNames = this.getDisplayMedalNames(playerId);
      this.config.set(playerId.toString(), null);
      this.setDisplayMedalNames(playerId, displayMedalNames);

      for (int i = 0; i < medals.size(); i++) {
         Medal medal = medals.get(i);
         this.config.set(playerId + "." + i, medal.getName());
      }

      try {
         this.config.save(this.configFile);
      } catch (IOException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }

   private String getDisplayMedalNames(UUID playerId) {
      return this.config.getString(playerId.toString() + ".display");
   }

   private void setDisplayMedalNames(UUID playerId, String displayMedalNames) {
      this.config.set(playerId.toString() + ".display", displayMedalNames);
   }

   public void giveMedal(UUID playerId, Medal medal) {
      List<Medal> medals = this.getMedals(playerId);
      medals.add(medal);
      this.setMedals(playerId, medals);
   }

   public void takeMedal(UUID playerId, Medal medal) {
      List<Medal> medals = this.getMedals(playerId);
      medals.remove(medal);
      this.setMedals(playerId, medals);
   }

   public List<Medal> getMedals(UUID playerId) {
      List<Medal> medals = new ArrayList<>();

      for (int i = 0; i < Integer.MAX_VALUE; i++) {
         String path = playerId.toString() + "." + i;
         if (!this.config.contains(path)) {
            break;
         }

         String medalName = this.config.getString(path);
         Medal medal = this.plugin.getMedalManager().getMedal(medalName);
         medals.add(medal);
      }

      return medals;
   }

   public void takeAllMedals(Medal medal) {
      for (String key : this.config.getKeys(false)) {
         UUID playerId = UUID.fromString(key);
         this.takeMedal(playerId, medal);
      }
   }

   public String getDisplayMedals(UUID playerId) {
      return this.getDisplayMedalNames(playerId);
   }

   public void setDisplayMedals(UUID playerId, String displayMedals) {
      this.setDisplayMedalNames(playerId, displayMedals);

      try {
         this.config.save(this.configFile);
      } catch (IOException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }
}
