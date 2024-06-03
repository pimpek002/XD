package me.elsiff.holomedals.datahandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

   public YamlUserDataHandler(HoloMedals var1) {
      this.plugin = var1;
      this.configFile = new File(var1.getDataFolder(), "userdata.yml");
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

   private void setMedals(UUID var1, List<Medal> var2) {
      String var3 = this.getDisplayMedalNames(var1);
      this.config.set(var1.toString(), (Object)null);
      this.setDisplayMedalNames(var1, var3);

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         Medal var5 = (Medal)var2.get(var4);
         this.config.set(var1.toString() + "." + var4, var5.getName());
      }

      try {
         this.config.save(this.configFile);
      } catch (IOException var6) {
         this.plugin.getLogger().severe(var6.getMessage());
      }

   }

   private String getDisplayMedalNames(UUID var1) {
      return this.config.getString(var1.toString() + ".display");
   }

   private void setDisplayMedalNames(UUID var1, String var2) {
      this.config.set(var1.toString() + ".display", var2);
   }

   public void giveMedal(UUID var1, Medal var2) {
      List var3 = this.getMedals(var1);
      var3.add(var2);
      this.setMedals(var1, var3);
   }

   public void takeMedal(UUID var1, Medal var2) {
      List var3 = this.getMedals(var1);
      var3.remove(var2);
      this.setMedals(var1, var3);
   }

   public List<Medal> getMedals(UUID var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < Integer.MAX_VALUE; ++var3) {
         String var4 = var1.toString() + "." + var3;
         if (!this.config.contains(var4)) {
            break;
         }

         String var5 = this.config.getString(var4);
         Medal var6 = this.plugin.getMedalManager().getMedal(var5);
         var2.add(var6);
      }

      return var2;
   }

   public void takeAllMedals(Medal var1) {
      Iterator var2 = this.config.getKeys(false).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         UUID var4 = UUID.fromString(var3);
         this.takeMedal(var4, var1);
      }

   }

   public String getDisplayMedals(UUID var1) {
      return this.getDisplayMedalNames(var1);
   }

   public void setDisplayMedals(UUID var1, String var2) {
      this.setDisplayMedalNames(var1, var2);

      try {
         this.config.save(this.configFile);
      } catch (IOException var4) {
         this.plugin.getLogger().severe(var4.getMessage());
      }

   }
}
