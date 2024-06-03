package me.elsiff.holomedals.protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUpdater {
   private int project;
   private URL checkURL;
   private String newVersion;
   private JavaPlugin plugin;

   public SpigotUpdater(JavaPlugin var1, int var2) {
      this.plugin = var1;
      this.newVersion = var1.getDescription().getVersion();
      this.project = var2;

      try {
         this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + var2);
      } catch (MalformedURLException var4) {
      }

   }

   public int getProjectID() {
      return this.project;
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   public String getLatestVersion() {
      return this.newVersion;
   }

   public String getResourceURL() {
      return "https://www.spigotmc.org/resources/" + this.project;
   }

   public boolean checkForUpdates() {
      URLConnection var1 = this.checkURL.openConnection();
      this.newVersion = (new BufferedReader(new InputStreamReader(var1.getInputStream()))).readLine();
      return !this.plugin.getDescription().getVersion().equals(this.newVersion);
   }
}
