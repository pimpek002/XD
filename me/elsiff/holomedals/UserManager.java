package me.elsiff.holomedals;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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

   public UserManager(HoloMedals var1, boolean var2) {
      this.plugin = var1;
      if (var2) {
         MySQLUserDataHandler var3 = new MySQLUserDataHandler(var1);
         var3.createTables();
         this.handler = var3;
      } else {
         this.handler = new YamlUserDataHandler(var1);
      }

   }

   public void giveMedal(OfflinePlayer var1, Medal var2) {
      this.handler.giveMedal(var1.getUniqueId(), var2);
   }

   public void takeMedal(OfflinePlayer var1, Medal var2) {
      if (this.getDisplayMedals(var1).contains(var2)) {
         this.resetDisplayMedal(var1);
      }

      this.handler.takeMedal(var1.getUniqueId(), var2);
   }

   public boolean hasMedal(OfflinePlayer var1, Medal var2) {
      return this.handler.getMedals(var1.getUniqueId()).contains(var2);
   }

   public List<Medal> getMedals(OfflinePlayer var1) {
      return this.handler.getMedals(var1.getUniqueId());
   }

   public void takeAllMedals(Medal var1) {
      this.handler.takeAllMedals(var1);
   }

   public int getMaxDisplayMedalsAmount(Player var1) {
      int var2 = 1;
      Iterator var3 = var1.getEffectivePermissions().iterator();

      while(var3.hasNext()) {
         PermissionAttachmentInfo var4 = (PermissionAttachmentInfo)var3.next();
         if (var4.getPermission().startsWith("holomedals.display.")) {
            int var5 = StringUtils.countMatches(var4.getPermission(), ".");
            String var6 = var4.getPermission().split("\\.")[var5];
            if (var6 != null && StringUtils.isNumeric(var6)) {
               int var7 = Integer.parseInt(var6);
               var2 = Math.max(var7, var2);
            }
         }
      }

      return var2;
   }

   public List<Medal> getDisplayMedals(OfflinePlayer var1) {
      ArrayList var2 = new ArrayList();
      String var3 = this.handler.getDisplayMedals(var1.getUniqueId());
      if (var3 != null && !var3.isEmpty()) {
         ArrayList var4 = Lists.newArrayList(var3.split(";"));
         boolean var5 = false;
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

            Medal var8;
            try {
               var8 = this.plugin.getMedalManager().getMedal(var7);
            } catch (IllegalArgumentException var10) {
               this.plugin.getLogger().warning("Removed a medal named '" + var7 + "' from display medals of '" + var1.getName() + "' because it doesn't exist");
               var5 = true;
               continue;
            }

            var2.add(var8);
         }

         if (var5) {
            this.setDisplayMedals(var1, var2);
         }

         return var2;
      } else {
         return var2;
      }
   }

   public void addDisplayMedal(OfflinePlayer var1, Medal var2) {
      List var3 = this.getDisplayMedals(var1);
      var3.add(var2);
      if (var1.isOnline() && var3.size() > this.getMaxDisplayMedalsAmount((Player)var1)) {
         var3.remove(0);
      }

      this.setDisplayMedals(var1, var3);
   }

   public void removeDisplayMedal(OfflinePlayer var1, Medal var2) {
      List var3 = this.getDisplayMedals(var1);
      var3.remove(var2);
      this.setDisplayMedals(var1, var3);
   }

   private void setDisplayMedals(OfflinePlayer var1, List<Medal> var2) {
      String var3 = (String)var2.stream().map(Medal::getName).collect(Collectors.joining(";"));
      this.handler.setDisplayMedals(var1.getUniqueId(), var3);
      if (var1.isOnline()) {
         this.plugin.getHologramManager().updateHologram(var1.getPlayer(), true, 0);
      }

   }

   public void resetDisplayMedal(OfflinePlayer var1) {
      this.handler.setDisplayMedals(var1.getUniqueId(), "");
   }
}
