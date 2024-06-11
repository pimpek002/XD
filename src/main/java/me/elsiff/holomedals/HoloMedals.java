package me.elsiff.holomedals;

import com.comphenix.protocol.ProtocolLibrary;
import me.elsiff.holomedals.command.GeneralCommands;
import me.elsiff.holomedals.command.MedalsCommands;
import me.elsiff.holomedals.datahandler.MySQLManager;
import me.elsiff.holomedals.hooker.PlaceholderAPIHooker;
import me.elsiff.holomedals.listener.MedalsGUI;
import me.elsiff.holomedals.listener.PlayerListener;
import me.elsiff.holomedals.protocol.HologramPacketListener;
import me.elsiff.holomedals.protocol.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloMedals extends JavaPlugin {
   private Locale locale;
   private MySQLManager mySQLManager;
   private MedalManager medalManager;
   private UserManager userManager;
   private HologramManager hologramManager;
   private UpdateChecker updateChecker;
   private MedalsGUI medalsGUI;
   private PlaceholderAPIHooker placeholderAPIHooker;

   public void onEnable() {
      Bukkit.getConsoleSender().sendMessage("%%__ENCRYPTME__%%[HoloMedals] Cracked by T0R&CM");
      PluginManager pluginManager = this.getServer().getPluginManager();
      if (pluginManager.getPlugin("HolographicDisplays") == null) {
         this.getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
      } else if (pluginManager.getPlugin("ProtocolLib") == null) {
         this.getLogger().severe("*** ProtocolLib is not installed or not enabled. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
      } else {
         this.saveDefaultConfig();
         this.locale = new Locale(this);
         if (this.getConfig().getInt("version") != 210) {
            String message = String.format(this.getLocale().getString("old-file"), "config.yml");
            this.getServer().getConsoleSender().sendMessage(message);
         }

         this.getCommand("holomedals").setExecutor(new GeneralCommands(this));
         this.getCommand("medals").setExecutor(new MedalsCommands(this));
         boolean mysqlEnabled = this.getConfig().getBoolean("mysql.enable");
         if (mysqlEnabled) {
            this.mySQLManager = new MySQLManager(this);
         }

         this.medalManager = new MedalManager(this, mysqlEnabled);
         this.userManager = new UserManager(this, mysqlEnabled);
         this.hologramManager = new HologramManager(this);
         this.updateChecker = new UpdateChecker(this);
         this.medalsGUI = new MedalsGUI(this);
         if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderAPIHooker = new PlaceholderAPIHooker(this);
         }

         pluginManager.registerEvents(this.medalsGUI, this);
         pluginManager.registerEvents(new PlayerListener(this), this);
         if (!this.getConfig().getBoolean("general.display-on-staying")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new HologramPacketListener(this));
         }

         this.getLogger().info("Plugin has been enabled!");
         for (Player player : this.getServer().getOnlinePlayers()) {
            this.getHologramManager().updateHologram(player);
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

   public UpdateChecker getUpdateChecker() {
      return this.updateChecker;
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
