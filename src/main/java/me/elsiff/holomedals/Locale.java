package me.elsiff.holomedals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Locale {
   private final FileConfiguration lang;

   public Locale(HoloMedals plugin) {
      String localeName = plugin.getConfig().getString("general.locale");
      String langFileName = "lang_" + localeName + ".yml";
      File langFile = new File(plugin.getDataFolder(), langFileName);
      if (!langFile.exists()) {
         plugin.saveResource(langFileName, false);
      }

      this.lang = YamlConfiguration.loadConfiguration(langFile);
      if (this.lang.getInt("version") != 210) {
         plugin.getServer().getConsoleSender().sendMessage(String.format(this.lang.getString("old-file"), langFileName));
      }
   }

   public String getString(String key) {
      String value = this.lang.getString(key);
      return ChatColor.translateAlternateColorCodes('&', value);
   }

   public List<String> getStringList(String key) {
      List<String> result = new ArrayList<>();
      for (String line : this.lang.getStringList(key)) {
         result.add(ChatColor.translateAlternateColorCodes('&', line));
      }
      return result;
   }
}