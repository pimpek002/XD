package me.elsiff.holomedals.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import me.elsiff.holomedals.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class MedalsGUI implements Listener {
   private final HoloMedals plugin;
   private final Map<UUID, List<Medal>> userMap = new HashMap<>();

   public MedalsGUI(HoloMedals plugin) {
      this.plugin = plugin;
   }

   public void openGUI(Player player) {
      this.plugin.getMedalManager().updatePermMedals(player);
      List<Medal> medals = new ArrayList<>();
      medals.addAll(this.plugin.getMedalManager().getPermMedals(player));
      medals.addAll(this.plugin.getUserManager().getMedals(player));
      this.sort(medals);
      this.openGUI(player, medals, false, this.plugin.getLocale().getString("title-yours"));
   }

   public void openGUI(Player player, OfflinePlayer target) {
      List<Medal> medals = new ArrayList<>(this.plugin.getUserManager().getMedals(target));
      this.sort(medals);
      String title = String.format(this.plugin.getLocale().getString("title-others"), target.getName());
      this.openGUI(player, medals, true, title);
   }

   private void sort(List<Medal> medals) {
      medals.sort((medal1, medal2) -> {
         String name1 = ChatColor.stripColor(medal1.getDisplayText());
         String name2 = ChatColor.stripColor(medal2.getDisplayText());
         return name1.compareToIgnoreCase(name2);
      });
   }

   public void openListGUI(Player player) {
      List<Medal> medals = new ArrayList<>(this.plugin.getMedalManager().getMedals());
      this.sort(medals);
      this.openGUI(player, medals, true, this.plugin.getLocale().getString("title-admin"));
   }

   public void openGUI(Player player, List<Medal> medals, boolean isAdminMode, String title) {
      Inventory inventory = this.plugin.getServer().createInventory(player, 36, title);
      this.updateGUI(player, inventory, medals, 1, isAdminMode);
      player.openInventory(inventory);
      this.userMap.put(player.getUniqueId(), medals);
   }

   private void updateGUI(Player player, Inventory inventory, List<Medal> medals, int page, boolean isAdminMode) {
      inventory.clear();
      List<Medal> displayMedals = this.plugin.getUserManager().getDisplayMedals(player);

      for (int i = 0; i < 27; i++) {
         int index = i + 27 * (page - 1);
         if (index >= medals.size()) {
            break;
         }

         Medal medal = medals.get(index);
         inventory.setItem(i, this.getMedalIcon(medal, displayMedals.contains(medal), isAdminMode));
      }

      if (page > 1) {
         ItemStack previousPage = new ItemBuilder(Material.ARROW, page - 1)
                 .setDisplayName(this.plugin.getLocale().getString("icon-previous"))
                 .build();
         inventory.setItem(30, previousPage);
      }

      if (page < this.getMaxPage(medals)) {
         ItemStack nextPage = new ItemBuilder(Material.ARROW, page + 1)
                 .setDisplayName(this.plugin.getLocale().getString("icon-next"))
                 .build();
         inventory.setItem(32, nextPage);
      }

      if (!isAdminMode) {
         ItemBuilder unequipIcon = new ItemBuilder(Material.BARRIER)
                 .setDisplayName(this.plugin.getLocale().getString("icon-unequip-name"))
                 .hideAll();
         if (!displayMedals.isEmpty()) {
            unequipIcon.addLore(this.plugin.getLocale().getStringList("icon-unequip-lore"));
         } else {
            unequipIcon.addLore(this.plugin.getLocale().getStringList("icon-selected"))
                    .addEnchantment(Enchantment.DURABILITY, 1);
         }
         inventory.setItem(27, unequipIcon.build());

         if (!displayMedals.isEmpty()) {
            List<String> lore = displayMedals.stream()
                    .map(medal -> "§7" + medal.getDisplayText())
                    .collect(Collectors.toList());
            ItemStack displayMedalsIcon = new ItemBuilder(Material.PAPER)
                    .setDisplayName(this.plugin.getLocale().getString("icon-display-medals"))
                    .addLore(lore)
                    .build();
            inventory.setItem(35, displayMedalsIcon);
         }
      }

      ItemStack pageIcon = new ItemBuilder(Material.BOOK)
              .setDisplayName(String.format(this.plugin.getLocale().getString("icon-page"), page))
              .build();
      inventory.setItem(31, pageIcon);

      this.setMetadata(player, isAdminMode, page);
   }

   private ItemStack getMedalIcon(Medal medal, boolean isSelected, boolean isAdminMode) {
      ItemBuilder iconBuilder = new ItemBuilder(medal.getIcon().clone()).hideAll();
      if (isAdminMode) {
         iconBuilder.setDisplayName("§r" + medal.getDisplayText());
         iconBuilder.addLore(String.format(this.plugin.getLocale().getString("icon-admin-name"), medal.getName()));
      } else {
         String[] lines = medal.getDisplayText().split("\\|");
         for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("%.*%", "~");
         }
         iconBuilder.setDisplayName(lines[0]);
         if (lines.length > 1) {
            iconBuilder.addLore(Arrays.copyOfRange(lines, 1, lines.length));
         }
         if (!isSelected) {
            iconBuilder.addLore(this.plugin.getLocale().getStringList("icon-equip-lore"));
         } else {
            iconBuilder.addLore(this.plugin.getLocale().getStringList("icon-selected"))
                    .addEnchantment(Enchantment.DURABILITY, 1);
         }
      }
      return iconBuilder.build();
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      if (this.userMap.containsKey(event.getWhoClicked().getUniqueId())) {
         event.setCancelled(true);
         if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            Player player = (Player) event.getWhoClicked();
            List<Medal> medals = this.userMap.get(player.getUniqueId());
            switch (event.getRawSlot()) {
               case 27:
                  this.plugin.getUserManager().resetDisplayMedal(player);
                  this.updateGUI(player, event.getInventory(), medals, this.getPage(player), this.isAdminMode(player));
                  break;
               case 28:
               case 29:
               case 33:
               case 34:
               default:
                  if (this.isAdminMode(player)) {
                     return;
                  }
                  int page = this.getPage(player);
                  int index = event.getRawSlot() + 27 * (page - 1);
                  Medal medal = medals.get(index);
                  if (!this.plugin.getUserManager().getDisplayMedals(player).contains(medal)) {
                     this.plugin.getUserManager().addDisplayMedal(player, medal);
                  } else {
                     this.plugin.getUserManager().removeDisplayMedal(player, medal);
                  }
                  this.updateGUI(player, event.getInventory(), medals, page, this.isAdminMode(player));
                  break;
               case 30:
               case 32:
                  int newPage = this.getPage(player) + (event.getRawSlot() == 30 ? -1 : 1);
                  this.updateGUI(player, event.getInventory(), medals, newPage, this.isAdminMode(player));
               case 31:
               case 35:
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      if (this.userMap.containsKey(event.getPlayer().getUniqueId())) {
         this.userMap.remove(event.getPlayer().getUniqueId());
         this.removeMetadata((Player) event.getPlayer());
      }
   }

   private int getMaxPage(List<Medal> medals) {
      return medals.size() / 27 + (medals.size() % 27 == 0 ? 0 : 1);
   }

   private void setMetadata(Player player, boolean isAdminMode, int page) {
      player.setMetadata("medals_gui", new FixedMetadataValue(this.plugin, isAdminMode));
      player.setMetadata("medals_gui_page", new FixedMetadataValue(this.plugin, page));
   }

   private void removeMetadata(Player player) {
      player.removeMetadata("medals_gui", this.plugin);
      player.removeMetadata("medals_gui_page", this.plugin);
   }

   private boolean isAdminMode(Player player) {
      return player.getMetadata("medals_gui").get(0).asBoolean();
   }

   private int getPage(Player player) {
      return player.getMetadata("medals_gui_page").get(0).asInt();
   }
}