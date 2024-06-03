package me.elsiff.holomedals.listener;

import java.util.Iterator;
import java.util.List;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.ChatColor;
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

   public PlayerListener(HoloMedals var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent var1) {
      if (this.plugin.getConfig().getBoolean("general.display-on-chat")) {
         List var2 = this.plugin.getUserManager().getDisplayMedals(var1.getPlayer());
         if (var2.isEmpty()) {
            return;
         }

         Medal var3 = (Medal)var2.get(0);
         String var4 = var3.getDisplayText().replaceAll("\\|", " ");
         var1.setFormat(var4 + ChatColor.RESET + var1.getFormat());
      }

   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      if (var1.getPlayer().isOp() && this.plugin.getConfig().getBoolean("general.check-update") && this.plugin.hasNewVersion()) {
         Iterator var2 = this.plugin.getLocale().getStringList("new-version").iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.getPlayer().sendMessage(String.format(var3, this.plugin.getNewVersion()));
         }
      }

      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         this.plugin.getMedalManager().updatePermMedals(var1.getPlayer());
         this.plugin.getHologramManager().updateHologram(var1.getPlayer());
      }, 1L);
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.plugin.getHologramManager().removeHologram(var1.getPlayer());
   }

   @EventHandler
   public void onChangedWorld(PlayerChangedWorldEvent var1) {
      if (!this.plugin.getUserManager().getDisplayMedals(var1.getPlayer()).isEmpty()) {
         this.plugin.getHologramManager().removeHologram(var1.getPlayer());
         this.plugin.getHologramManager().createHologram(var1.getPlayer());
      }

   }

   @EventHandler
   public void onMove(PlayerMoveEvent var1) {
      if ((this.plugin.getConfig().getBoolean("general.display-on-staying") || this.plugin.getConfig().getBoolean("general.show-self-on-staying")) && this.plugin.getHologramManager().hasHologram(var1.getPlayer()) && var1.getFrom().distance(var1.getTo()) > 0.1D) {
         this.plugin.getHologramManager().getStayingCheckerTask().hide(var1.getPlayer());
      }

   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent var1) {
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         this.plugin.getHologramManager().updateHologram(var1.getPlayer());
      }, 1L);
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent var1) {
      this.plugin.getHologramManager().removeHologram(var1.getEntity());
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent var1) {
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         this.plugin.getHologramManager().createHologram(var1.getPlayer());
      }, 1L);
   }

   @EventHandler
   public void onToggleSneak(PlayerToggleSneakEvent var1) {
      if (!var1.isAsynchronous() && !this.plugin.getUserManager().getDisplayMedals(var1.getPlayer()).isEmpty()) {
         if (var1.isSneaking()) {
            this.plugin.getHologramManager().removeHologram(var1.getPlayer());
         } else {
            this.plugin.getHologramManager().createHologram(var1.getPlayer());
         }
      }

   }

   @EventHandler
   public void onGameModeChange(PlayerGameModeChangeEvent var1) {
      if (!this.plugin.getUserManager().getDisplayMedals(var1.getPlayer()).isEmpty()) {
         if (var1.getNewGameMode() == GameMode.SPECTATOR) {
            this.plugin.getHologramManager().removeHologram(var1.getPlayer());
         } else {
            this.plugin.getHologramManager().createHologram(var1.getPlayer());
         }
      }

   }
}
