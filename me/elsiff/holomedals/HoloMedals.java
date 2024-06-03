package me.elsiff.holomedals;

import com.comphenix.protocol.ProtocolLibrary;
import java.io.IOException;
import java.util.Iterator;
import me.elsiff.holomedals.command.GeneralCommands;
import me.elsiff.holomedals.command.MedalsCommands;
import me.elsiff.holomedals.datahandler.MySQLManager;
import me.elsiff.holomedals.hooker.PlaceholderAPIHooker;
import me.elsiff.holomedals.listener.MedalsGUI;
import me.elsiff.holomedals.listener.PlayerListener;
import me.elsiff.holomedals.protocol.HologramPacketListener;
import me.elsiff.holomedals.protocol.SpigotUpdater;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloMedals extends JavaPlugin {
   public static final int VER_CONFIG = 210;
   public static final int VER_LANG = 210;
   private Locale locale;
   private MySQLManager mySQLManager;
   private MedalManager medalManager;
   private UserManager userManager;
   private HologramManager hologramManager;
   private boolean hasNewVersion = false;
   private String newVersion;
   private MedalsGUI medalsGUI;
   private PlaceholderAPIHooker placeholderAPIHooker;

   public void onEnable() {
      System.out.println("§3Spigotunlocked.org");
      loadConfig0();
      PluginManager var1 = this.getServer().getPluginManager();
      if (var1.getPlugin("HolographicDisplays") == null) {
         this.getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
      } else if (var1.getPlugin("ProtocolLib") == null) {
         this.getLogger().severe("*** ProtocolLib is not installed or not enabled. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
      } else {
         this.saveDefaultConfig();
         this.locale = new Locale(this);
         if (this.getConfig().getInt("version") != 210) {
            String var2 = String.format(this.getLocale().getString("old-file"), "config.yml");
            this.getServer().getConsoleSender().sendMessage(var2);
         }

         this.getCommand("holomedals").setExecutor(new GeneralCommands(this));
         this.getCommand("medals").setExecutor(new MedalsCommands(this));
         boolean var7 = this.getConfig().getBoolean("mysql.enable");
         if (var7) {
            this.mySQLManager = new MySQLManager(this);
         }

         this.medalManager = new MedalManager(this, var7);
         this.userManager = new UserManager(this, var7);
         this.hologramManager = new HologramManager(this);
         SpigotUpdater var3 = new SpigotUpdater(this, 31223);
         if (!this.getDescription().getVersion().contains("SNAPSHOT")) {
            try {
               if (var3.checkForUpdates()) {
                  this.hasNewVersion = true;
                  this.newVersion = var3.getLatestVersion();
               }
            } catch (IOException var6) {
               var6.printStackTrace();
            }
         }

         this.medalsGUI = new MedalsGUI(this);
         if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderAPIHooker = new PlaceholderAPIHooker(this);
         }

         var1.registerEvents(this.medalsGUI, this);
         var1.registerEvents(new PlayerListener(this), this);
         if (!this.getConfig().getBoolean("general.display-on-staying")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new HologramPacketListener(this));
         }

         this.getLogger().info("Plugin has been enabled!");
         Iterator var4 = this.getServer().getOnlinePlayers().iterator();

         while(var4.hasNext()) {
            Player var5 = (Player)var4.next();
            this.getHologramManager().updateHologram(var5);
         }

      }
   }

   public void onDisable() {
      if (this.getHologramManager() != null) {
         this.getHologramManager().clear();
      }

      if (this.getMySQLManager() != null) {
         this.getMySQLManager().closeConnection();
      }

      ProtocolLibrary.getProtocolManager().removePacketListeners(this);
      this.getLogger().info("Plugin has been disabled!");
   }

   public Locale getLocale() {
      return this.locale;
   }

   public MySQLManager getMySQLManager() {
      return this.mySQLManager;
   }

   public MedalManager getMedalManager() {
      return this.medalManager;
   }

   public UserManager getUserManager() {
      return this.userManager;
   }

   public HologramManager getHologramManager() {
      return this.hologramManager;
   }

   public boolean hasNewVersion() {
      return this.hasNewVersion;
   }

   public String getNewVersion() {
      return this.newVersion;
   }

   public MedalsGUI getMedalsGUI() {
      return this.medalsGUI;
   }

   public PlaceholderAPIHooker getPlaceholderAPIHooker() {
      return this.placeholderAPIHooker;
   }

   public void reloadLocale() {
      this.locale = new Locale(this);
   }
}
