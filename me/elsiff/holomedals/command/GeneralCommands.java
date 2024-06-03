package me.elsiff.holomedals.command;

import java.util.Iterator;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneralCommands implements CommandExecutor {
   private final HoloMedals plugin;
   private final String prefix;

   public GeneralCommands(HoloMedals var1) {
      this.prefix = ChatColor.YELLOW + "[HoloMedals]" + ChatColor.RESET + " ";
      this.plugin = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length >= 1 && !var4[0].equalsIgnoreCase("help")) {
         Player var18;
         OfflinePlayer var20;
         if (var4[0].equalsIgnoreCase("list") && var4.length < 3) {
            if (!var1.hasPermission("holomedals.admin")) {
               var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
               return true;
            } else if (!(var1 instanceof Player)) {
               var1.sendMessage(this.plugin.getLocale().getString("in-game-command"));
               return true;
            } else {
               var18 = (Player)var1;
               if (var4.length == 1) {
                  this.plugin.getMedalsGUI().openListGUI(var18);
               } else {
                  var20 = this.getOfflinePlayer(var4[1]);
                  if (var20 == null) {
                     var18.sendMessage(String.format(this.plugin.getLocale().getString("player-not-found"), var4[1]));
                     return true;
                  }

                  this.plugin.getMedalsGUI().openGUI(var18, var20);
               }

               return true;
            }
         } else if (var4[0].equalsIgnoreCase("create") && var4.length >= 3) {
            if (!var1.hasPermission("holomedals.admin")) {
               var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
               return true;
            } else if (!(var1 instanceof Player)) {
               var1.sendMessage(this.plugin.getLocale().getString("in-game-command"));
               return true;
            } else {
               var18 = (Player)var1;
               ItemStack var24 = var18.getInventory().getItem(var18.getInventory().getHeldItemSlot());
               if (var24 != null && var24.getType() != Material.AIR) {
                  String var22 = var4[1];
                  if (this.plugin.getMedalManager().existMedal(var22)) {
                     var18.sendMessage(this.plugin.getLocale().getString("already-taken-name"));
                     return true;
                  } else {
                     String var8 = ChatColor.translateAlternateColorCodes('&', this.getFinalArg(var4, 2));
                     if (var8.contains(";")) {
                        var18.sendMessage(String.format(this.plugin.getLocale().getString("not-allowed-character"), ";"));
                        return true;
                     } else {
                        Medal var9 = new Medal(var22, var8, var24);
                        this.plugin.getMedalManager().createMedal(var9);
                        Iterator var10 = this.plugin.getServer().getOnlinePlayers().iterator();

                        while(var10.hasNext()) {
                           Player var11 = (Player)var10.next();
                           this.plugin.getMedalManager().updatePermMedals(var11);
                        }

                        var18.sendMessage(String.format(this.plugin.getLocale().getString("created-medal"), var9.getName()));
                        return true;
                     }
                  }
               } else {
                  var18.sendMessage(this.plugin.getLocale().getString("empty-hand"));
                  return true;
               }
            }
         } else {
            Medal var19;
            if (var4[0].equalsIgnoreCase("delete") && var4.length == 2) {
               if (!var1.hasPermission("holomedals.admin")) {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               } else {
                  try {
                     var19 = this.plugin.getMedalManager().getMedal(var4[1]);
                  } catch (IllegalArgumentException var12) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[1]));
                     return true;
                  }

                  this.plugin.getMedalManager().removeMedal(var19);
                  var1.sendMessage(String.format(this.plugin.getLocale().getString("deleted-medal"), var19.getName()));
                  return true;
               }
            } else if (var4[0].equalsIgnoreCase("edit")) {
               if (!var1.hasPermission("holomedals.admin")) {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               } else {
                  var1.sendMessage(this.prefix + ChatColor.GOLD + "> ===== " + ChatColor.YELLOW + ChatColor.BOLD + this.plugin.getDescription().getFullName() + ChatColor.GOLD + " ===== <");
                  var1.sendMessage(this.prefix + "/" + var3 + " setdisplaytext <medalName> <newDisplayText>");
                  var1.sendMessage(this.prefix + "/" + var3 + " seticon <medalName>");
                  return true;
               }
            } else if (var4[0].equalsIgnoreCase("setdisplaytext") && var4.length >= 3) {
               if (!var1.hasPermission("holomedals.admin")) {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               } else {
                  try {
                     var19 = this.plugin.getMedalManager().getMedal(var4[1]);
                  } catch (IllegalArgumentException var13) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[1]));
                     return true;
                  }

                  String var23 = ChatColor.translateAlternateColorCodes('&', this.getFinalArg(var4, 2));
                  var19.setDisplayText(var23);
                  this.plugin.getMedalManager().updateMedal(var19);
                  var1.sendMessage(String.format(this.plugin.getLocale().getString("edited-medal"), var19.getName()));
                  return true;
               }
            } else if (var4[0].equalsIgnoreCase("seticon") && var4.length == 2) {
               if (!var1.hasPermission("holomedals.admin")) {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               } else {
                  try {
                     var19 = this.plugin.getMedalManager().getMedal(var4[1]);
                  } catch (IllegalArgumentException var14) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[1]));
                     return true;
                  }

                  if (!(var1 instanceof Player)) {
                     var1.sendMessage(this.plugin.getLocale().getString("in-game-command"));
                     return true;
                  } else {
                     Player var21 = (Player)var1;
                     ItemStack var7 = var21.getInventory().getItem(var21.getInventory().getHeldItemSlot());
                     if (var7 != null && var7.getType() != Material.AIR) {
                        var19.setIcon(var7);
                        this.plugin.getMedalManager().updateMedal(var19);
                        var1.sendMessage(String.format(this.plugin.getLocale().getString("edited-medal"), var19.getName()));
                        return true;
                     } else {
                        var21.sendMessage(this.plugin.getLocale().getString("empty-hand"));
                        return true;
                     }
                  }
               }
            } else if (var4[0].equalsIgnoreCase("give") && var4.length == 3) {
               try {
                  var19 = this.plugin.getMedalManager().getMedal(var4[2]);
               } catch (IllegalArgumentException var15) {
                  var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[2]));
                  return true;
               }

               if (var1.hasPermission("holomedals.admin") && var1.hasPermission("holomedals.give." + var19.getName())) {
                  var20 = this.getOfflinePlayer(var4[1]);
                  if (var20 == null) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("player-not-found"), var4[1]));
                     return true;
                  } else if (this.plugin.getUserManager().hasMedal(var20, var19)) {
                     var1.sendMessage(this.plugin.getLocale().getString("already-has"));
                     return true;
                  } else {
                     this.plugin.getUserManager().giveMedal(var20, var19);
                     var1.sendMessage(this.plugin.getLocale().getString("gave-medal").replaceAll("%player%", var20.getName()).replaceAll("%medal%", var19.getName()));
                     if (var20.isOnline()) {
                        var20.getPlayer().sendMessage(String.format(this.plugin.getLocale().getString("gained-medal"), var19.getDisplayName()));
                     }

                     return true;
                  }
               } else {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               }
            } else if (var4[0].equalsIgnoreCase("take") && var4.length == 3) {
               try {
                  var19 = this.plugin.getMedalManager().getMedal(var4[2]);
               } catch (IllegalArgumentException var16) {
                  var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[2]));
                  return true;
               }

               if (var1.hasPermission("holomedals.admin") && var1.hasPermission("holomedals.take." + var19.getName())) {
                  var20 = this.getOfflinePlayer(var4[1]);
                  if (var20 == null) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("player-not-found"), var4[1]));
                     return true;
                  } else if (!this.plugin.getUserManager().hasMedal(var20, var19)) {
                     var1.sendMessage(this.plugin.getLocale().getString("doesnt-have"));
                     return true;
                  } else {
                     this.plugin.getUserManager().takeMedal(var20, var19);
                     var1.sendMessage(this.plugin.getLocale().getString("took-medal").replaceAll("%player%", var20.getName()).replaceAll("%medal%", var19.getName()));
                     if (var20.isOnline()) {
                        var20.getPlayer().sendMessage(String.format(this.plugin.getLocale().getString("lost-medal"), var19.getDisplayName()));
                     }

                     return true;
                  }
               } else {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               }
            } else if (var4[0].equalsIgnoreCase("set") && var4.length == 3) {
               if (!var1.hasPermission("holomedals.admin")) {
                  var1.sendMessage(this.plugin.getLocale().getString("no-permission"));
                  return true;
               } else {
                  var18 = this.getPlayer(var4[1]);
                  if (var18 == null) {
                     var1.sendMessage(String.format(this.plugin.getLocale().getString("player-not-found"), var4[1]));
                     return true;
                  } else {
                     Medal var6;
                     try {
                        var6 = this.plugin.getMedalManager().getMedal(var4[2]);
                     } catch (IllegalArgumentException var17) {
                        var1.sendMessage(String.format(this.plugin.getLocale().getString("medal-not-found"), var4[2]));
                        return true;
                     }

                     if (!this.plugin.getUserManager().hasMedal(var18, var6) && !this.plugin.getMedalManager().getPermMedals(var18).contains(var6)) {
                        var1.sendMessage(this.plugin.getLocale().getString("doesnt-have"));
                        return true;
                     } else if (this.plugin.getUserManager().getDisplayMedals(var18).contains(var6)) {
                        var1.sendMessage(this.plugin.getLocale().getString("already-selected"));
                        return true;
                     } else {
                        this.plugin.getUserManager().addDisplayMedal(var18, var6);
                        var1.sendMessage(this.plugin.getLocale().getString("set-display-medal").replaceAll("%player%", var18.getName()).replaceAll("%medal%", var6.getName()));
                        if (var18.isOnline()) {
                           var18.getPlayer().sendMessage(String.format(this.plugin.getLocale().getString("forced-display"), var6.getDisplayName()));
                        }

                        return true;
                     }
                  }
               }
            } else if (var4[0].equalsIgnoreCase("reload") && var4.length == 1) {
               this.plugin.reloadConfig();
               this.plugin.reloadLocale();
               var1.sendMessage(this.plugin.getLocale().getString("reloaded"));
               return true;
            } else if (var4[0].equalsIgnoreCase("migrate") && var4.length == 1) {
               if (!this.plugin.getConfig().getBoolean("mysql.enable")) {
                  return true;
               } else {
                  boolean var5 = this.plugin.getMedalManager().migrate();
                  if (!var5) {
                     var1.sendMessage(this.plugin.getLocale().getString("migration-failed"));
                  }

                  var1.sendMessage(this.plugin.getLocale().getString("migration-succeeded"));
                  return true;
               }
            } else {
               var1.sendMessage(this.plugin.getLocale().getString("invalid-command"));
               return true;
            }
         }
      } else {
         var1.sendMessage(this.prefix + ChatColor.GOLD + "> ===== " + ChatColor.YELLOW + ChatColor.BOLD + this.plugin.getDescription().getFullName() + ChatColor.GOLD + " ===== <");
         var1.sendMessage(this.prefix + "/medals");
         var1.sendMessage(this.prefix + "/" + var3 + " help");
         var1.sendMessage(this.prefix + "/" + var3 + " list [player]");
         var1.sendMessage(this.prefix + "/" + var3 + " create <medalName> <displayText>");
         var1.sendMessage(this.prefix + "/" + var3 + " delete <medalName>");
         var1.sendMessage(this.prefix + "/" + var3 + " edit");
         var1.sendMessage(this.prefix + "/" + var3 + " <give|take|set> <player> <medalName>");
         var1.sendMessage(this.prefix + "/" + var3 + " reload");
         return true;
      }
   }

   private OfflinePlayer getOfflinePlayer(String var1) {
      OfflinePlayer[] var2 = this.plugin.getServer().getOfflinePlayers();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         OfflinePlayer var5 = var2[var4];
         if (var5 != null && var5.getName().equals(var1)) {
            return var5;
         }
      }

      return null;
   }

   private Player getPlayer(String var1) {
      Iterator var2 = this.plugin.getServer().getOnlinePlayers().iterator();

      Player var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Player)var2.next();
      } while(var3 == null || !var3.getName().equals(var1));

      return var3;
   }

   private String getFinalArg(String[] var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      for(int var4 = var2; var4 < var1.length; ++var4) {
         var3.append(var1[var4]);
         if (var4 < var1.length - 1) {
            var3.append(" ");
         }
      }

      return var3.toString();
   }
}
