package me.elsiff.holomedals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Locale {
   private FileConfiguration lang;

   public Locale(HoloMedals var1) {
      String var2 = var1.getConfig().getString("general.locale");
      String var3 = "lang_" + var2 + ".yml";
      File var4 = new File(var1.getDataFolder(), var3);
      if (!var4.exists()) {
         var1.saveResource(var3, false);
      }

      this.lang = YamlConfiguration.loadConfiguration(var4);
      if (this.lang.getInt("version") != 210) {
         var1.getServer().getConsoleSender().sendMessage(String.format(this.lang.getString("old-file"), var3));
      }

   }

   public String getString(String var1) {
      String var2 = this.lang.getString(var1);
      return ChatColor.translateAlternateColorCodes('&', var2);
   }

   public List<String> getStringList(String var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.lang.getStringList(var1).iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.add(ChatColor.translateAlternateColorCodes('&', var4));
      }

      return var2;
   }
}
