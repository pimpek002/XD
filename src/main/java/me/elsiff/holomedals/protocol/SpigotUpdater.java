package me.elsiff.holomedals.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUpdater {
   private final int project;
   private final URL checkURL;
   private String newVersion;
   private final JavaPlugin plugin;

   public SpigotUpdater(JavaPlugin plugin, int project) {
      this.plugin = plugin;
      this.newVersion = plugin.getDescription().getVersion();
      this.project = project;

      URL tempURL = null;
      try {
         tempURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + project);
      } catch (MalformedURLException e) {
         e.printStackTrace(); // log the error
      }
      this.checkURL = tempURL;
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
      try {
         URLConnection connection = this.checkURL.openConnection();
         this.newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
         return !this.plugin.getDescription().getVersion().equals(this.newVersion);
      } catch (IOException e) {
         e.printStackTrace(); // handle the exception
         return false;
      }
   }
}