package me.elsiff.holomedals.datahandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class YamlMedalDataHandler implements MedalDataHandler {
   private final HoloMedals plugin;
   private final File configFile;
   private final FileConfiguration config;

   public YamlMedalDataHandler(HoloMedals plugin) {
      this.plugin = plugin;
      this.configFile = new File(plugin.getDataFolder(), "medals.yml");
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

   public List<Medal> loadMedals() {
      List<Medal> medals = new ArrayList<>();
      for (String key : this.config.getKeys(false)) {
         String displayText = this.config.getString(key + ".displayText");
         ItemStack icon = this.config.getItemStack(key + ".icon");
         Medal medal = new Medal(key, displayText, icon);
         medals.add(medal);
      }
      return medals;
   }

   private void saveConfigFile() {
      try {
         this.config.save(this.configFile);
      } catch (IOException e) {
         this.plugin.getLogger().severe(e.getMessage());
      }
   }

   public void createMedal(Medal medal) {
      this.updateMedal(medal);
   }

   public void updateMedal(Medal medal) {
      this.config.set(medal.getName() + ".displayText", medal.getDisplayText());
      this.config.set(medal.getName() + ".icon", medal.getIcon());
      this.saveConfigFile();
   }

   public void removeMedal(Medal medal) {
      this.config.set(medal.getName(), null);
      this.saveConfigFile();
   }
}