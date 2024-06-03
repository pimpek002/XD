package me.elsiff.holomedals.datahandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

   public YamlMedalDataHandler(HoloMedals var1) {
      this.plugin = var1;
      this.configFile = new File(var1.getDataFolder(), "medals.yml");
      if (!this.configFile.exists()) {
         try {
            boolean var2 = this.configFile.createNewFile();
            if (!var2) {
               var1.getLogger().warning("Failed to create userdata.yml!");
            }
         } catch (IOException var3) {
            var1.getLogger().severe(var3.getMessage());
         }
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
   }

   public List<Medal> loadMedals() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.config.getKeys(false).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         String var4 = this.config.getString(var3 + ".displayText");
         ItemStack var5 = this.config.getItemStack(var3 + ".icon");
         Medal var6 = new Medal(var3, var4, var5);
         var1.add(var6);
      }

      return var1;
   }

   private void saveConfigFile() {
      try {
         this.config.save(this.configFile);
      } catch (IOException var2) {
         this.plugin.getLogger().severe(var2.getMessage());
      }

   }

   public void createMedal(Medal var1) {
      this.updateMedal(var1);
   }

   public void updateMedal(Medal var1) {
      this.config.set(var1.getName() + ".displayText", var1.getDisplayText());
      this.config.set(var1.getName() + ".icon", var1.getIcon());
      this.saveConfigFile();
   }

   public void removeMedal(Medal var1) {
      this.config.set(var1.getName(), (Object)null);
      this.saveConfigFile();
   }
}
