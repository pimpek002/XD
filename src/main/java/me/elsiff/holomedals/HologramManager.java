package me.elsiff.holomedals;

import com.comphenix.protocol.ProtocolLibrary;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import java.util.HashMap;
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
   private final Map<UUID, Hologram> holoMap = new HashMap<>();
   private final Map<UUID, Integer> displayIndexMap = new HashMap<>();
   private final StayingCheckerTask stayingCheckerTask;
   private final HologramSwapperTask hologramSwapperTask;
   private final boolean showSelfOnStaying;
   private final boolean displayOnStaying;

   public HologramManager(HoloMedals plugin) {
      this.plugin = plugin;
      this.showSelfOnStaying = plugin.getConfig().getBoolean("general.show-self-on-staying");
      this.displayOnStaying = plugin.getConfig().getBoolean("general.display-on-staying");

      if (this.showSelfOnStaying || this.displayOnStaying) {
         this.stayingCheckerTask = new StayingCheckerTask();
         this.stayingCheckerTask.runTaskTimer(plugin, 0L, 20L);
      } else {
         this.stayingCheckerTask = null;
      }

      this.hologramSwapperTask = new HologramSwapperTask();
      this.hologramSwapperTask.runTaskTimer(plugin, 0L, plugin.getConfig().getLong("general.displaying-period-tick"));
   }

   public void clear() {
      if (this.getStayingCheckerTask() != null) {
         this.getStayingCheckerTask().cancel();
      }

      if (this.getHologramSwapperTask() != null) {
         this.getHologramSwapperTask().cancel();
      }

      for (Hologram hologram : this.holoMap.values()) {
         hologram.delete();
      }

      this.holoMap.clear();
   }

   private String getReplacedText(Medal medal, Player player) {
      PlaceholderAPIHooker placeholderAPIHooker = this.plugin.getPlaceholderAPIHooker();
      String displayText = medal.getDisplayText();
      return placeholderAPIHooker != null ? placeholderAPIHooker.setPlaceholders(player, displayText) : displayText;
   }

   private void appendDisplayText(Hologram hologram, Medal medal, Player player) {
      String replacedText = this.getReplacedText(medal, player);
      String[] lines = replacedText.split("\\|");
      for (String line : lines) {
         hologram.appendTextLine(line);
      }
   }

   public void createHologram(Player player) {
      List<Medal> displayMedals = this.plugin.getUserManager().getDisplayMedals(player);
      if (!this.holoMap.containsKey(player.getUniqueId()) && !displayMedals.isEmpty()) {
         Hologram hologram = HologramsAPI.createHologram(this.plugin, player.getLocation());
         hologram.getVisibilityManager().setVisibleByDefault(false);
         this.displayIndexMap.put(player.getUniqueId(), 0);
         this.appendDisplayText(hologram, displayMedals.get(0), player);
         hologram.teleport(this.getHeadLocation(player, hologram));
         hologram.getVisibilityManager().setVisibleByDefault(true);
         if (!this.showSelfOnStaying) {
            hologram.getVisibilityManager().hideTo(player);
         }
         this.updateTrackers(player, hologram);
         this.holoMap.put(player.getUniqueId(), hologram);
      }
   }

   private void updateTrackers(Player player, Hologram hologram) {
      VisibilityManager visibilityManager = hologram.getVisibilityManager();
      List<Player> entityTrackers = ProtocolLibrary.getProtocolManager().getEntityTrackers(player);
      for (Player otherPlayer : player.getWorld().getPlayers()) {
         if (otherPlayer.equals(player)) {
            if (!this.showSelfOnStaying) {
               visibilityManager.hideTo(player);
            }
         } else if (entityTrackers.contains(otherPlayer)) {
            visibilityManager.showTo(otherPlayer);
         } else {
            visibilityManager.hideTo(otherPlayer);
         }
      }
   }

   public void removeHologram(Player player) {
      this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
         if (this.holoMap.containsKey(player.getUniqueId())) {
            this.holoMap.get(player.getUniqueId()).delete();
            this.holoMap.remove(player.getUniqueId());
         }
      });
   }

   public void updateHolograms(Medal medal) {
      for (UUID uuid : this.holoMap.keySet()) {
         Player player = this.plugin.getServer().getPlayer(uuid);
         if (this.plugin.getUserManager().getDisplayMedals(player).contains(medal)) {
            this.updateHologram(player, true, 0);
         }
      }
   }

   public void updateHologram(Player player) {
      this.updateHologram(player, false, 0);
   }

   public void updateHologram(Player player, boolean forceUpdate, int index) {
      List<Medal> displayMedals = this.plugin.getUserManager().getDisplayMedals(player);
      if (!displayMedals.isEmpty() && this.isVisible(player)) {
         if (this.hasHologram(player)) {
            Hologram hologram = this.holoMap.get(player.getUniqueId());
            if (forceUpdate) {
               this.displayIndexMap.put(player.getUniqueId(), index);
               hologram.clearLines();
               this.appendDisplayText(hologram, displayMedals.get(index), player);
            }
            hologram.teleport(this.getHeadLocation(player, hologram));
            this.updateTrackers(player, hologram);
            if (this.getStayingCheckerTask() != null) {
               this.getStayingCheckerTask().checkLocation(player);
            }
         } else {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.createHologram(player));
         }
      } else {
         if (this.hasHologram(player)) {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.removeHologram(player));
         }
      }
   }

   public boolean hasHologram(Player player) {
      return this.holoMap.containsKey(player.getUniqueId());
   }

   public StayingCheckerTask getStayingCheckerTask() {
      return this.stayingCheckerTask;
   }

   public HologramSwapperTask getHologramSwapperTask() {
      return this.hologramSwapperTask;
   }

   private Location getHeadLocation(Player player, Hologram hologram) {
      double height = 2.6D;
      height += hologram.getHeight();
      if (this.plugin.getConfig().getBoolean("general.below-name")) {
         height -= 0.7D;
      } else if (player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) == null) {
         height -= 0.25D;
      }
      return player.getLocation().add(0.0D, height, 0.0D);
   }

   private boolean isVisible(Player player) {
      return !player.isDead() && !player.isSneaking() && player.getGameMode() != GameMode.SPECTATOR
              && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)
              && player.getPassengers().isEmpty() && player.getVehicle() == null;
   }

   public class StayingCheckerTask extends BukkitRunnable {
      private final Map<UUID, Location> locMap = new HashMap<>();

      @Override
      public void run() {
         this.updateAllTrackers();
         for (Player player : HologramManager.this.plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (HologramManager.this.holoMap.containsKey(uuid)) {
               if (this.locMap.containsKey(uuid)) {
                  Location lastLocation = this.locMap.get(uuid);
                  if (lastLocation.getWorld().equals(player.getLocation().getWorld())) {
                     List<Medal> displayMedals = HologramManager.this.plugin.getUserManager().getDisplayMedals(player);
                     if (HologramManager.this.isVisible(player)
                             && (!displayMedals.isEmpty() || !HologramManager.this.holoMap.containsKey(uuid))
                             && lastLocation.distance(player.getLocation()) <= 0.1D) {
                        if (!displayMedals.isEmpty()) {
                           this.show(player);
                        }
                     } else {
                        this.hide(player);
                     }
                  }
               }
            }
         }
         this.updateOldLocations();
      }

      private void updateAllTrackers() {
         for (UUID uuid : HologramManager.this.holoMap.keySet()) {
            Player player = HologramManager.this.plugin.getServer().getPlayer(uuid);
            if (!HologramManager.this.isVisible(player)) {
               Hologram hologram = HologramManager.this.holoMap.get(player.getUniqueId());
               HologramManager.this.updateTrackers(player, hologram);
            }
         }
      }

      private void updateOldLocations() {
         for (Player player : HologramManager.this.plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (HologramManager.this.holoMap.containsKey(uuid)) {
               this.locMap.put(uuid, player.getLocation());
            }
         }
      }

      public void checkLocation(Player player) {
         UUID uuid = player.getUniqueId();
         if (this.locMap.containsKey(uuid)) {
            Location lastLocation = this.locMap.get(uuid);
            if (lastLocation.getWorld().equals(player.getWorld()) && lastLocation.distance(player.getLocation()) > 0.1D) {
               this.hide(player);
            }
         }
      }

      public void show(Player player) {
         HologramManager.this.createHologram(player);
         if (!HologramManager.this.displayOnStaying) {
            Hologram hologram = HologramManager.this.holoMap.get(player.getUniqueId());
            hologram.teleport(HologramManager.this.getHeadLocation(player, hologram));
            hologram.getVisibilityManager().showTo(player);
         }
      }

      public void hide(Player player) {
         if (HologramManager.this.holoMap.containsKey(player.getUniqueId())) {
            if (HologramManager.this.displayOnStaying) {
               HologramManager.this.removeHologram(player);
            } else {
               HologramManager.this.holoMap.get(player.getUniqueId()).getVisibilityManager().hideTo(player);
            }
         }
      }
   }

   public class HologramSwapperTask extends BukkitRunnable {
      @Override
      public void run() {
         for (Player player : HologramManager.this.plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (HologramManager.this.holoMap.containsKey(uuid)) {
               List<Medal> displayMedals = HologramManager.this.plugin.getUserManager().getDisplayMedals(player);
               if (displayMedals.size() > 1) {
                  int currentIndex = HologramManager.this.displayIndexMap.get(uuid);
                  int nextIndex = currentIndex != displayMedals.size() - 1 ? currentIndex + 1 : 0;
                  HologramManager.this.updateHologram(player, true, nextIndex);
               }
            }
         }
      }
   }
}