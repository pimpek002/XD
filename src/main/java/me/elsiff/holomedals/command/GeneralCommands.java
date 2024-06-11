package me.elsiff.holomedals.command;

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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneralCommands implements CommandExecutor {
   private final HoloMedals plugin;

   public GeneralCommands(HoloMedals plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length >= 1 && !args[0].equalsIgnoreCase("help")) {
         if (checkAdminPermission(sender, args[0])) return true;

         switch (args[0].toLowerCase()) {
            case "list":
               handleListCommand(sender, args);
               break;
            case "create":
               handleCreateCommand(sender, args);
               break;
            case "delete":
               handleDeleteCommand(sender, args);
               break;
            case "edit":
               handleEditCommand(sender, label);
               break;
            case "setdisplaytext":
               handleSetDisplayTextCommand(sender, args);
               break;
            case "seticon":
               handleSetIconCommand(sender, args);
               break;
            case "give":
               handleGiveCommand(sender, args);
               break;
            case "take":
               handleTakeCommand(sender, args);
               break;
            case "set":
               handleSetCommand(sender, args);
               break;
            case "reload":
               plugin.reloadConfig();
               plugin.reloadLocale();
               sender.sendMessage(plugin.getLocale().getString("reloaded"));
               break;
            case "migrate":
               handleMigrateCommand(sender);
               break;
            default:
               sender.sendMessage(plugin.getLocale().getString("invalid-command"));
         }
      } else {
         displayHelp(sender, label);
      }
      return true;
   }

   private boolean checkAdminPermission(CommandSender sender, String arg) {
      if (arg.equalsIgnoreCase("list") || arg.equalsIgnoreCase("create") || arg.equalsIgnoreCase("delete") ||
              arg.equalsIgnoreCase("edit") || arg.equalsIgnoreCase("setdisplaytext") || arg.equalsIgnoreCase("seticon")) {
         if (!sender.hasPermission("holomedals.admin")) {
            sender.sendMessage(plugin.getLocale().getString("no-permission"));
            return true;
         } else if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocale().getString("in-game-command"));
            return true;
         }
      }
      return false;
   }

   private void handleListCommand(CommandSender sender, String[] args) {
      Player player = (Player) sender;
      if (args.length == 1) {
         plugin.getMedalsGUI().openListGUI(player);
      } else {
         OfflinePlayer target = getOfflinePlayer(args[1]);
         if (target == null) {
            player.sendMessage(String.format(plugin.getLocale().getString("player-not-found"), args[1]));
            return;
         }
         plugin.getMedalsGUI().openGUI(player, target);
      }
   }

   private void handleCreateCommand(CommandSender sender, String[] args) {
      Player player = (Player) sender;
      ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
      if (heldItem == null || heldItem.getType() == Material.AIR) {
         player.sendMessage(plugin.getLocale().getString("empty-hand"));
         return;
      }

      String medalName = args[1];
      if (plugin.getMedalManager().getMedal(medalName) != null) {
         player.sendMessage(plugin.getLocale().getString("already-taken-name"));
         return;
      }

      String displayText = ChatColor.translateAlternateColorCodes('&', getFinalArg(args));
      if (displayText.contains(";")) {
         player.sendMessage(String.format(plugin.getLocale().getString("not-allowed-character"), ";"));
         return;
      }

      Medal medal = new Medal(medalName, displayText, heldItem);
      plugin.getMedalManager().createMedal(medal);

      for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
         plugin.getMedalManager().updatePermMedals(onlinePlayer);
      }

      player.sendMessage(String.format(plugin.getLocale().getString("created-medal"), medal.getName()));
   }

   private void handleDeleteCommand(CommandSender sender, String[] args) {
      Medal medal = plugin.getMedalManager().getMedal(args[1]);
      if (medal == null) {
         sender.sendMessage(plugin.getLocale().getString("medal-not-found"));
         return;
      }

      plugin.getMedalManager().removeMedal(medal);
      sender.sendMessage(String.format(plugin.getLocale().getString("deleted-medal"), medal.getName()));
   }

   private void handleEditCommand(CommandSender sender, String label) {
      sender.sendMessage("§e[HoloMedals]§r §6> ===== §e§lHoloMedals §6 ===== <");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " setdisplaytext <medalName> <newDisplayText>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " seticon <medalName>");
   }

   private void handleSetDisplayTextCommand(CommandSender sender, String[] args) {
      Medal medal = plugin.getMedalManager().getMedal(args[1]);
      if (medal == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("medal-not-found"), args[1]));
         return;
      }

      String newDisplayText = ChatColor.translateAlternateColorCodes('&', getFinalArg(args));
      medal.setDisplayText(newDisplayText);
      plugin.getMedalManager().updateMedal(medal);
      sender.sendMessage(String.format(plugin.getLocale().getString("edited-medal"), medal.getName()));
   }

   private void handleSetIconCommand(CommandSender sender, String[] args) {
      Medal medal = plugin.getMedalManager().getMedal(args[1]);
      if (medal == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("medal-not-found"), args[1]));
         return;
      }

      Player player = (Player) sender;
      ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
      if (heldItem == null || heldItem.getType() == Material.AIR) {
         player.sendMessage(plugin.getLocale().getString("empty-hand"));
         return;
      }

      medal.setIcon(heldItem);
      plugin.getMedalManager().updateMedal(medal);
      sender.sendMessage(String.format(plugin.getLocale().getString("edited-medal"), medal.getName()));
   }

   private void handleGiveCommand(CommandSender sender, String[] args) {
      Medal medal = plugin.getMedalManager().getMedal(args[2]);
      if (medal == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("medal-not-found"), args[2]));
         return;
      }

      OfflinePlayer target = getOfflinePlayer(args[1]);
      if (target == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("player-not-found"), args[1]));
         return;
      }

      if (plugin.getUserManager().hasMedal(target, medal)) {
         sender.sendMessage(plugin.getLocale().getString("already-has"));
         return;
      }

      plugin.getUserManager().giveMedal(target, medal);
      sender.sendMessage(plugin.getLocale().getString("gave-medal").replace("%player%", target.getName()).replace("%medal%", medal.getName()));

      if (target.isOnline()) {
         target.getPlayer().sendMessage(String.format(plugin.getLocale().getString("gained-medal"), medal.getDisplayName()));
      }
   }

   private void handleTakeCommand(CommandSender sender, String[] args) {
      Medal medal = plugin.getMedalManager().getMedal(args[2]);
      if (medal == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("medal-not-found"), args[2]));
         return;
      }

      OfflinePlayer target = getOfflinePlayer(args[1]);
      if (target == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("player-not-found"), args[1]));
         return;
      }

      if (!plugin.getUserManager().hasMedal(target, medal)) {
         sender.sendMessage(plugin.getLocale().getString("doesnt-have"));
         return;
      }

      plugin.getUserManager().takeMedal(target, medal);
      sender.sendMessage(plugin.getLocale().getString("took-medal").replace("%player%", target.getName()).replace("%medal%", medal.getName()));

      if (target.isOnline()) {
         target.getPlayer().sendMessage(String.format(plugin.getLocale().getString("lost-medal"), medal.getDisplayName()));
      }
   }

   private void handleSetCommand(CommandSender sender, String[] args) {
      Player player = getPlayer(args[1]);
      if (player == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("player-not-found"), args[1]));
         return;
      }

      Medal medal = plugin.getMedalManager().getMedal(args[2]);
      if (medal == null) {
         sender.sendMessage(String.format(plugin.getLocale().getString("medal-not-found"), args[2]));
         return;
      }

      if (!plugin.getUserManager().hasMedal(player, medal) && !plugin.getMedalManager().getPermMedals(player).contains(medal)) {
         sender.sendMessage(plugin.getLocale().getString("doesnt-have"));
         return;
      }

      if (plugin.getUserManager().getDisplayMedals(player).contains(medal)) {
         sender.sendMessage(plugin.getLocale().getString("already-selected"));
         return;
      }

      plugin.getUserManager().addDisplayMedal(player, medal);
      sender.sendMessage(plugin.getLocale().getString("set-display-medal").replace("%player%", player.getName()).replace("%medal%", medal.getName()));

      if (player.isOnline()) {
         player.sendMessage(String.format(plugin.getLocale().getString("forced-display"), medal.getDisplayName()));
      }
   }

   private void handleMigrateCommand(CommandSender sender) {
      if (!plugin.getConfig().getBoolean("mysql.enable")) {
         return;
      }

      boolean success = plugin.getMedalManager().migrate();
      if (!success) {
         sender.sendMessage(plugin.getLocale().getString("migration-failed"));
      } else {
         sender.sendMessage(plugin.getLocale().getString("migration-succeeded"));
      }
   }

   private void displayHelp(CommandSender sender, String label) {
      sender.sendMessage("§e[HoloMedals]§r §6> ===== §e§lHoloMedals §6 ===== <");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " list [player]");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " create <name> <displayText>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " delete <name>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " edit");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " setdisplaytext <name> <newDisplayText>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " seticon <name>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " give <player> <name>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " take <player> <name>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " set <player> <name>");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " reload");
      sender.sendMessage("§e[HoloMedals]§r /" + label + " migrate");
   }

   private OfflinePlayer getOfflinePlayer(String name) {
      for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
         if (player.getName().equalsIgnoreCase(name)) {
            return player;
         }
      }
      return null;
   }

   private Player getPlayer(String name) {
      return plugin.getServer().getPlayer(name);
   }

   private String getFinalArg(String[] args) {
      return Stream.of(args).skip(2).collect(Collectors.joining(" "));
   }
}
