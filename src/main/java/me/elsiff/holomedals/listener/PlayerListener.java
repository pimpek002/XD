package me.elsiff.holomedals.listener;

import java.util.List;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerListener implements Listener {
   private final HoloMedals plugin;

   public PlayerListener(HoloMedals plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      if (this.plugin.getConfig().getBoolean("general.display-on-chat")) {
         List<Medal> medals = this.plugin.getUserManager().getDisplayMedals(event.getPlayer());
         if (medals.isEmpty()) {
            return;
         }

         Medal medal = medals.get(0);
         String displayText = medal.getDisplayText().replaceAll("\\|", " ");
         event.setFormat(displayText + "§r " + event.getFormat());
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      if (event.getPlayer().isOp() && this.plugin.getConfig().getBoolean("general.check-update") && !this.plugin.getUpdateChecker().isUpToDate()) {
         for (String message : this.plugin.getLocale().getStringList("new-version")) {
            event.getPlayer().sendMessage(String.format(message, this.plugin.getUpdateChecker().getNewVersion()));
         }
      }

      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         this.plugin.getMedalManager().updatePermMedals(event.getPlayer());
         this.plugin.getHologramManager().updateHologram(event.getPlayer());
      }, 1L);
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.plugin.getHologramManager().removeHologram(event.getPlayer());
   }

   @EventHandler
   public void onChangedWorld(PlayerChangedWorldEvent event) {
      if (!this.plugin.getUserManager().getDisplayMedals(event.getPlayer()).isEmpty()) {
         this.plugin.getHologramManager().removeHologram(event.getPlayer());
         this.plugin.getHologramManager().createHologram(event.getPlayer());
      }
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      if ((this.plugin.getConfig().getBoolean("general.display-on-staying") || this.plugin.getConfig().getBoolean("general.show-self-on-staying")) && this.plugin.getHologramManager().hasHologram(event.getPlayer()) && event.getFrom().distance(event.getTo()) > 0.1D) {
         this.plugin.getHologramManager().getStayingCheckerTask().hide(event.getPlayer());
      }
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent event) {
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getHologramManager().updateHologram(event.getPlayer()), 1L);
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      this.plugin.getHologramManager().removeHologram(event.getEntity());
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent event) {
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getHologramManager().createHologram(event.getPlayer()), 1L);
   }

   @EventHandler
   public void onToggleSneak(PlayerToggleSneakEvent event) {
      if (!event.isAsynchronous() && !this.plugin.getUserManager().getDisplayMedals(event.getPlayer()).isEmpty()) {
         if (event.isSneaking()) {
            this.plugin.getHologramManager().removeHologram(event.getPlayer());
         } else {
            this.plugin.getHologramManager().createHologram(event.getPlayer());
         }
      }
   }

   @EventHandler
   public void onGameModeChange(PlayerGameModeChangeEvent event) {
      if (!this.plugin.getUserManager().getDisplayMedals(event.getPlayer()).isEmpty()) {
         if (event.getNewGameMode() == GameMode.SPECTATOR) {
            this.plugin.getHologramManager().removeHologram(event.getPlayer());
         } else {
            this.plugin.getHologramManager().createHologram(event.getPlayer());
         }
      }
   }
}