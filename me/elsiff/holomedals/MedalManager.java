package me.elsiff.holomedals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
   private final Map<UUID, List<Medal>> permMedalMap = new HashMap();

   public MedalManager(HoloMedals var1, boolean var2) {
      this.plugin = var1;
      if (var2) {
         MySQLMedalDataHandler var3 = new MySQLMedalDataHandler(var1);
         var3.createTables();
         this.handler = var3;
      } else {
         this.handler = new YamlMedalDataHandler(var1);
      }

      this.medals = this.handler.loadMedals();
      this.medalMap = new HashMap();
      Iterator var5 = this.medals.iterator();

      while(var5.hasNext()) {
         Medal var4 = (Medal)var5.next();
         this.medalMap.put(var4.getName(), var4);
      }

   }

   public boolean migrate() {
      if (!(this.handler instanceof MySQLMedalDataHandler)) {
         return false;
      } else {
         Iterator var1 = this.getMedals().iterator();

         while(var1.hasNext()) {
            Medal var2 = (Medal)var1.next();
            this.removeMedal(var2);
         }

         YamlMedalDataHandler var4 = new YamlMedalDataHandler(this.plugin);
         Iterator var5 = var4.loadMedals().iterator();

         while(var5.hasNext()) {
            Medal var3 = (Medal)var5.next();
            this.createMedal(var3);
         }

         return true;
      }
   }

   public void createMedal(Medal var1) {
      this.medals.add(var1);
      this.medalMap.put(var1.getName(), var1);
      this.handler.createMedal(var1);
   }

   public void updateMedal(Medal var1) {
      this.plugin.getHologramManager().updateHolograms(var1);
      this.handler.updateMedal(var1);
   }

   public void removeMedal(Medal var1) {
      this.plugin.getUserManager().takeAllMedals(var1);
      this.medals.remove(var1);
      this.medalMap.remove(var1.getName());
      this.handler.removeMedal(var1);
   }

   public Medal getMedal(String var1) {
      if (!this.existMedal(var1)) {
         throw new IllegalArgumentException("Couldn't find a medal named '" + var1 + "'");
      } else {
         return (Medal)this.medalMap.get(var1);
      }
   }

   public boolean existMedal(String var1) {
      return this.medalMap.containsKey(var1);
   }

   public List<Medal> getMedals() {
      return this.medals;
   }

   public void updatePermMedals(Player var1) {
      boolean var2 = this.plugin.getConfig().getBoolean("general.op-gets-all");
      if (var2 || !var1.isOp()) {
         ArrayList var3 = new ArrayList();
         Iterator var4 = this.getMedals().iterator();

         while(var4.hasNext()) {
            Medal var5 = (Medal)var4.next();
            if (var1.hasPermission("holomedals.medal." + var5.getName()) && !this.plugin.getUserManager().hasMedal(var1, var5)) {
               var3.add(var5);
            }
         }

         this.permMedalMap.put(var1.getUniqueId(), var3);
         ArrayList var7 = new ArrayList(this.plugin.getUserManager().getDisplayMedals(var1));
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            Medal var6 = (Medal)var8.next();
            if (!this.plugin.getUserManager().hasMedal(var1, var6) && !var3.contains(var6)) {
               this.plugin.getUserManager().takeMedal(var1, var6);
            }
         }

      }
   }

   public List<Medal> getPermMedals(Player var1) {
      if (!this.permMedalMap.containsKey(var1.getUniqueId())) {
         this.permMedalMap.put(var1.getUniqueId(), new ArrayList());
      }

      return (List)this.permMedalMap.get(var1.getUniqueId());
   }
}
