package me.elsiff.holomedals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.elsiff.holomedals.datahandler.MedalDataHandler;
import me.elsiff.holomedals.datahandler.MySQLMedalDataHandler;
import me.elsiff.holomedals.datahandler.YamlMedalDataHandler;
import org.bukkit.entity.Player;

public class MedalManager {
   private final HoloMedals plugin;
   private final MedalDataHandler handler;
   private final List<Medal> medals;
   private final Map<String, Medal> medalMap;
   private final Map<UUID, List<Medal>> permMedalMap = new HashMap<>();

   public MedalManager(HoloMedals plugin, boolean useMySQL) {
      this.plugin = plugin;
      if (useMySQL) {
         MySQLMedalDataHandler mySQLHandler = new MySQLMedalDataHandler(plugin);
         mySQLHandler.createTables();
         this.handler = mySQLHandler;
      } else {
         this.handler = new YamlMedalDataHandler(plugin);
      }

      this.medals = this.handler.loadMedals();
      this.medalMap = new HashMap<>();
      for (Medal medal : this.medals) {
         this.medalMap.put(medal.getName(), medal);
      }
   }

   public boolean migrate() {
      if (!(this.handler instanceof MySQLMedalDataHandler)) {
         return false;
      }

      for (Medal medal : this.getMedals()) {
         this.removeMedal(medal);
      }

      YamlMedalDataHandler yamlHandler = new YamlMedalDataHandler(this.plugin);
      for (Medal medal : yamlHandler.loadMedals()) {
         this.createMedal(medal);
      }

      return true;
   }

   public void createMedal(Medal medal) {
      this.medals.add(medal);
      this.medalMap.put(medal.getName(), medal);
      this.handler.createMedal(medal);
   }

   public void updateMedal(Medal medal) {
      this.plugin.getHologramManager().updateHolograms(medal);
      this.handler.updateMedal(medal);
   }

   public void removeMedal(Medal medal) {
      this.plugin.getUserManager().takeAllMedals(medal);
      this.medals.remove(medal);
      this.medalMap.remove(medal.getName());
      this.handler.removeMedal(medal);
   }

   public Medal getMedal(String name) {
      return this.medalMap.getOrDefault(name, null);
   }

   public List<Medal> getMedals() {
      return this.medals;
   }

   public void updatePermMedals(Player player) {
      boolean opGetsAll = this.plugin.getConfig().getBoolean("general.op-gets-all");
      if (opGetsAll || !player.isOp()) {
         List<Medal> permMedals = new ArrayList<>();
         for (Medal medal : this.getMedals()) {
            if (player.hasPermission("holomedals.medal." + medal.getName()) && !this.plugin.getUserManager().hasMedal(player, medal)) {
               permMedals.add(medal);
            }
         }

         this.permMedalMap.put(player.getUniqueId(), permMedals);

         List<Medal> displayMedals = new ArrayList<>(this.plugin.getUserManager().getDisplayMedals(player));
         for (Medal medal : displayMedals) {
            if (!this.plugin.getUserManager().hasMedal(player, medal) && !permMedals.contains(medal)) {
               this.plugin.getUserManager().takeMedal(player, medal);
            }
         }
      }
   }

   public List<Medal> getPermMedals(Player player) {
      this.permMedalMap.putIfAbsent(player.getUniqueId(), new ArrayList<>());
      return this.permMedalMap.get(player.getUniqueId());
   }
}