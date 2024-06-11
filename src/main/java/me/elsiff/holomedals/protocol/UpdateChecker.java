package me.elsiff.holomedals.protocol;

import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {
   private static final String KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
   private static final String ID = "31223";
   private String oldVersion;
   private String newVersion;

   public UpdateChecker(JavaPlugin var1) {
      this.oldVersion = var1.getDescription().getVersion();
      this.newVersion = this.loadNewVersion();
   }

   public boolean isUpToDate() {
      return this.oldVersion.contains("SNAPSHOT") || this.oldVersion.equals(this.newVersion);
   }

   public String getOldVersion() {
      return this.oldVersion;
   }

   public String getNewVersion() {
      return this.newVersion;
   }

   private String loadNewVersion() {
      return null;
   }
}
