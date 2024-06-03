package me.elsiff.holomedals;

import com.comphenix.protocol.ProtocolLibrary;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.elsiff.holomedals.hooker.PlaceholderAPIHooker;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

public class HologramManager {
   private final HoloMedals plugin;
   private final Map<UUID, Hologram> holoMap = new HashMap();
   private final Map<UUID, Integer> displayIndexMap = new HashMap();
   private HologramManager.StayingCheckerTask stayingCheckerTask;
   private HologramManager.HologramSwapperTask hologramSwapperTask;
   private boolean showSelfOnStaying;
   private boolean displayOnStaying;

   public HologramManager(HoloMedals var1) {
      this.plugin = var1;
      this.showSelfOnStaying = var1.getConfig().getBoolean("general.show-self-on-staying");
      this.displayOnStaying = var1.getConfig().getBoolean("general.display-on-staying");
      if (this.showSelfOnStaying || this.displayOnStaying) {
         this.stayingCheckerTask = new HologramManager.StayingCheckerTask();
         this.stayingCheckerTask.runTaskTimer(var1, 0L, 20L);
      }

      this.hologramSwapperTask = new HologramManager.HologramSwapperTask();
      this.hologramSwapperTask.runTaskTimer(var1, 0L, var1.getConfig().getLong("general.displaying-period-tick"));
   }

   public void clear() {
      if (this.getStayingCheckerTask() != null) {
         this.getStayingCheckerTask().cancel();
      }

      if (this.getHologramSwapperTask() != null) {
         this.getHologramSwapperTask().cancel();
      }

      Iterator var1 = this.holoMap.values().iterator();

      while(var1.hasNext()) {
         Hologram var2 = (Hologram)var1.next();
         var2.delete();
      }

      this.holoMap.clear();
   }

   private String getReplacedText(Medal var1, Player var2) {
      PlaceholderAPIHooker var3 = this.plugin.getPlaceholderAPIHooker();
      String var4 = var1.getDisplayText();
      return var3 != null ? var3.setPlaceholders(var2, var4) : var4;
   }

   private void appendDisplayText(Hologram var1, Medal var2, Player var3) {
      String var4 = this.getReplacedText(var2, var3);
      String[] var5 = var4.split("\\|");
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         var1.appendTextLine(var8);
      }

   }

   public void createHologram(Player var1) {
      List var2 = this.plugin.getUserManager().getDisplayMedals(var1);
      if (!this.holoMap.containsKey(var1.getUniqueId()) && !var2.isEmpty()) {
         Hologram var3 = HologramsAPI.createHologram(this.plugin, var1.getLocation());
         var3.getVisibilityManager().setVisibleByDefault(false);
         this.displayIndexMap.put(var1.getUniqueId(), 0);
         this.appendDisplayText(var3, (Medal)var2.get(0), var1);
         var3.teleport(this.getHeadLocation(var1, var3));
         var3.getVisibilityManager().setVisibleByDefault(true);
         if (!this.showSelfOnStaying) {
            var3.getVisibilityManager().hideTo(var1);
         }

         this.updateTrackers(var1, var3);
         this.holoMap.put(var1.getUniqueId(), var3);
      }
   }

   private void updateTrackers(Player var1, Hologram var2) {
      VisibilityManager var3 = var2.getVisibilityManager();
      List var4 = ProtocolLibrary.getProtocolManager().getEntityTrackers(var1);
      Iterator var5 = var1.getWorld().getPlayers().iterator();

      while(var5.hasNext()) {
         Player var6 = (Player)var5.next();
         if (var6.equals(var1)) {
            if (!this.showSelfOnStaying) {
               var3.hideTo(var1);
            }
         } else if (var4.contains(var6)) {
            var3.showTo(var6);
         } else {
            var3.hideTo(var6);
         }
      }

   }

   public void removeHologram(Player var1) {
      this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
         if (this.holoMap.containsKey(var1.getUniqueId())) {
            ((Hologram)this.holoMap.get(var1.getUniqueId())).delete();
            this.holoMap.remove(var1.getUniqueId());
         }

      });
   }

   public void updateHolograms(Medal var1) {
      Iterator var2 = this.holoMap.keySet().iterator();

      while(var2.hasNext()) {
         UUID var3 = (UUID)var2.next();
         Player var4 = this.plugin.getServer().getPlayer(var3);
         if (this.plugin.getUserManager().getDisplayMedals(var4).contains(var1)) {
            this.updateHologram(var4, true, 0);
         }
      }

   }

   public void updateHologram(Player var1) {
      this.updateHologram(var1, false, 0);
   }

   public void updateHologram(Player var1, boolean var2, int var3) {
      List var4 = this.plugin.getUserManager().getDisplayMedals(var1);
      if (!var4.isEmpty() && !this.isHidden(var1)) {
         if (this.hasHologram(var1)) {
            Hologram var5 = (Hologram)this.holoMap.get(var1.getUniqueId());
            if (var2) {
               this.displayIndexMap.put(var1.getUniqueId(), var3);
               var5.clearLines();
               this.appendDisplayText(var5, (Medal)var4.get(var3), var1);
            }

            var5.teleport(this.getHeadLocation(var1, var5));
            this.updateTrackers(var1, var5);
            if (this.getStayingCheckerTask() != null) {
               this.getStayingCheckerTask().checkLocation(var1);
            }
         } else {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
               this.createHologram(var1);
            });
         }

      } else {
         if (this.hasHologram(var1)) {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
               this.removeHologram(var1);
            });
         }

      }
   }

   public boolean hasHologram(Player var1) {
      return this.holoMap.containsKey(var1.getUniqueId());
   }

   public HologramManager.StayingCheckerTask getStayingCheckerTask() {
      return this.stayingCheckerTask;
   }

   public HologramManager.HologramSwapperTask getHologramSwapperTask() {
      return this.hologramSwapperTask;
   }

   private Location getHeadLocation(Player var1, Hologram var2) {
      double var3 = 2.6D;
      var3 += var2.getHeight();
      if (this.plugin.getConfig().getBoolean("general.below-name")) {
         var3 -= 0.7D;
      } else if (var1.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) == null) {
         var3 -= 0.25D;
      }

      return var1.getLocation().add(0.0D, var3, 0.0D);
   }

   private boolean isHidden(Player var1) {
      return var1.isDead() || var1.isSneaking() || var1.getGameMode() == GameMode.SPECTATOR || var1.hasPotionEffect(PotionEffectType.INVISIBILITY) || var1.getPassenger() != null || var1.getVehicle() != null;
   }

   public class StayingCheckerTask extends BukkitRunnable {
      private final Map<UUID, Location> locMap = new HashMap();

      public void run() {
         this.updateAllTrackers();
         Iterator var1 = HologramManager.this.plugin.getServer().getOnlinePlayers().iterator();

         while(true) {
            while(true) {
               Player var2;
               UUID var3;
               do {
                  do {
                     do {
                        if (!var1.hasNext()) {
                           this.updateOldLocations();
                           return;
                        }

                        var2 = (Player)var1.next();
                        var3 = var2.getUniqueId();
                     } while(!HologramManager.this.holoMap.containsKey(var3));
                  } while(!this.locMap.containsKey(var3));
               } while(!((Location)this.locMap.get(var3)).getWorld().equals(var2.getLocation().getWorld()));

               List var4 = HologramManager.this.plugin.getUserManager().getDisplayMedals(var2);
               if (!HologramManager.this.isHidden(var2) && (!var4.isEmpty() || !HologramManager.this.holoMap.containsKey(var3)) && !(((Location)this.locMap.get(var3)).distance(var2.getLocation()) > 0.1D)) {
                  if (!var4.isEmpty()) {
                     this.show(var2);
                  }
               } else {
                  this.hide(var2);
               }
            }
         }
      }

      private void updateAllTrackers() {
         Iterator var1 = HologramManager.this.holoMap.keySet().iterator();

         while(var1.hasNext()) {
            UUID var2 = (UUID)var1.next();
            Player var3 = HologramManager.this.plugin.getServer().getPlayer(var2);
            if (!HologramManager.this.isHidden(var3)) {
               Hologram var4 = (Hologram)HologramManager.this.holoMap.get(var3.getUniqueId());
               HologramManager.this.updateTrackers(var3, var4);
            }
         }

      }

      private void updateOldLocations() {
         Iterator var1 = HologramManager.this.plugin.getServer().getOnlinePlayers().iterator();

         while(var1.hasNext()) {
            Player var2 = (Player)var1.next();
            UUID var3 = var2.getUniqueId();
            if (HologramManager.this.holoMap.containsKey(var3)) {
               this.locMap.put(var3, var2.getLocation());
            }
         }

      }

      public void checkLocation(Player var1) {
         UUID var2 = var1.getUniqueId();
         if (this.locMap.containsKey(var2)) {
            Location var3 = (Location)this.locMap.get(var2);
            if (var3.getWorld().equals(var1.getWorld()) && var3.distance(var1.getLocation()) > 0.1D) {
               this.hide(var1);
            }
         }

      }

      public void show(Player var1) {
         HologramManager.this.createHologram(var1);
         if (!HologramManager.this.displayOnStaying) {
            Hologram var2 = (Hologram)HologramManager.this.holoMap.get(var1.getUniqueId());
            var2.teleport(HologramManager.this.getHeadLocation(var1, var2));
            var2.getVisibilityManager().showTo(var1);
         }

      }

      public void hide(Player var1) {
         if (HologramManager.this.holoMap.containsKey(var1.getUniqueId())) {
            if (HologramManager.this.displayOnStaying) {
               HologramManager.this.removeHologram(var1);
            } else {
               ((Hologram)HologramManager.this.holoMap.get(var1.getUniqueId())).getVisibilityManager().hideTo(var1);
            }

         }
      }
   }

   public class HologramSwapperTask extends BukkitRunnable {
      public void run() {
         Iterator var1 = HologramManager.this.plugin.getServer().getOnlinePlayers().iterator();

         while(var1.hasNext()) {
            Player var2 = (Player)var1.next();
            UUID var3 = var2.getUniqueId();
            if (HologramManager.this.holoMap.containsKey(var3)) {
               List var4 = HologramManager.this.plugin.getUserManager().getDisplayMedals(var2);
               if (var4.size() > 1) {
                  int var5 = (Integer)HologramManager.this.displayIndexMap.get(var3);
                  int var6 = var5 != var4.size() - 1 ? var5 + 1 : 0;
                  HologramManager.this.updateHologram(var2, true, var6);
               }
            }
         }

      }
   }
}
