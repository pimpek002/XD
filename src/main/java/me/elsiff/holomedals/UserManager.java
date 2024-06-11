package me.elsiff.holomedals;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.elsiff.holomedals.datahandler.MySQLUserDataHandler;
import me.elsiff.holomedals.datahandler.UserDataHandler;
import me.elsiff.holomedals.datahandler.YamlUserDataHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class UserManager {
   private final HoloMedals plugin;
   private final UserDataHandler handler;

   public UserManager(HoloMedals plugin, boolean useMySQL) {
      this.plugin = plugin;
      if (useMySQL) {
         MySQLUserDataHandler mySQLHandler = new MySQLUserDataHandler(plugin);
         mySQLHandler.createTables();
         this.handler = mySQLHandler;
      } else {
         this.handler = new YamlUserDataHandler(plugin);
      }
   }

   public void giveMedal(OfflinePlayer player, Medal medal) {
      this.handler.giveMedal(player.getUniqueId(), medal);
   }

   public void takeMedal(OfflinePlayer player, Medal medal) {
      if (this.getDisplayMedals(player).contains(medal)) {
         this.resetDisplayMedal(player);
      }
      this.handler.takeMedal(player.getUniqueId(), medal);
   }

   public boolean hasMedal(OfflinePlayer player, Medal medal) {
      return this.handler.getMedals(player.getUniqueId()).contains(medal);
   }

   public List<Medal> getMedals(OfflinePlayer player) {
      return this.handler.getMedals(player.getUniqueId());
   }

   public void takeAllMedals(Medal medal) {
      this.handler.takeAllMedals(medal);
   }

   public int getMaxDisplayMedalsAmount(Player player) {
      int maxAmount = 1;
      for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
         if (permissionInfo.getPermission().startsWith("holomedals.display.")) {
            int count = StringUtils.countMatches(permissionInfo.getPermission(), ".");
            String[] parts = permissionInfo.getPermission().split("\\.");
            String number = parts[count];
            if (StringUtils.isNumeric(number)) {
               int value = Integer.parseInt(number);
               maxAmount = Math.max(value, maxAmount);
            }
         }
      }
      return maxAmount;
   }

   public List<Medal> getDisplayMedals(OfflinePlayer player) {
      String displayMedals = this.handler.getDisplayMedals(player.getUniqueId());
      displayMedals = displayMedals != null ? displayMedals : "";
      return Stream.of(displayMedals.split(";"))
              .map(this.plugin.getMedalManager()::getMedal)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
   }

   public void addDisplayMedal(Player player, Medal medal) {
      List<Medal> displayMedals = this.getDisplayMedals(player);
      displayMedals.add(medal);
      if (displayMedals.size() > this.getMaxDisplayMedalsAmount(player)) {
         displayMedals.remove(0);
      }
      this.setDisplayMedals(player, displayMedals);
   }

   public void removeDisplayMedal(Player player, Medal medal) {
      List<Medal> displayMedals = this.getDisplayMedals(player);
      displayMedals.remove(medal);
      this.setDisplayMedals(player, displayMedals);
   }

   private void setDisplayMedals(OfflinePlayer player, List<Medal> medals) {
      String displayMedals = medals.stream()
              .map(Medal::getName)
              .collect(Collectors.joining(";"));
      this.handler.setDisplayMedals(player.getUniqueId(), displayMedals);
      if (player.isOnline()) {
         this.plugin.getHologramManager().updateHologram(player.getPlayer(), true, 0);
      }
   }

   public void resetDisplayMedal(OfflinePlayer player) {
      this.handler.setDisplayMedals(player.getUniqueId(), "");
   }
}