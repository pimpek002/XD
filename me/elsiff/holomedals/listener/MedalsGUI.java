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
   private static final String METADATA_GUI = "medals_gui";
   private static final String METADATA_PAGE = "medals_gui_page";
   private Map<UUID, List<Medal>> userMap = new HashMap();

   public MedalsGUI(HoloMedals var1) {
      this.plugin = var1;
   }

   public void openGUI(Player var1) {
      this.plugin.getMedalManager().updatePermMedals(var1);
      ArrayList var2 = new ArrayList();
      var2.addAll(this.plugin.getMedalManager().getPermMedals(var1));
      var2.addAll(this.plugin.getUserManager().getMedals(var1));
      this.sort(var2);
      this.openGUI(var1, var2, false, this.plugin.getLocale().getString("title-yours"));
   }

   public void openGUI(Player var1, OfflinePlayer var2) {
      ArrayList var3 = new ArrayList();
      var3.addAll(this.plugin.getUserManager().getMedals(var2));
      this.sort(var3);
      String var4 = String.format(this.plugin.getLocale().getString("title-others"), var2.getName());
      this.openGUI(var1, var3, true, var4);
   }

   private void sort(List<Medal> var1) {
      var1.sort((var0, var1x) -> {
         String var2 = ChatColor.stripColor(var0.getDisplayText());
         String var3 = ChatColor.stripColor(var1x.getDisplayText());
         return var2.compareToIgnoreCase(var3);
      });
   }

   public void openListGUI(Player var1) {
      ArrayList var2 = new ArrayList();
      var2.addAll(this.plugin.getMedalManager().getMedals());
      this.sort(var2);
      this.openGUI(var1, var2, true, this.plugin.getLocale().getString("title-admin"));
   }

   public void openGUI(Player var1, List<Medal> var2, boolean var3, String var4) {
      Inventory var5 = this.plugin.getServer().createInventory(var1, 36, var4);
      this.updateGUI(var1, var5, var2, 1, var3);
      var1.openInventory(var5);
      this.userMap.put(var1.getUniqueId(), var2);
   }

   private void updateGUI(Player var1, Inventory var2, List<Medal> var3, int var4, boolean var5) {
      var2.clear();
      List var6 = this.plugin.getUserManager().getDisplayMedals(var1);

      for(int var7 = 0; var7 < 27; ++var7) {
         int var8 = var7 + 27 * (var4 - 1);
         if (var8 >= var3.size()) {
            break;
         }

         Medal var9 = (Medal)var3.get(var8);
         var2.setItem(var7, this.getMedalIcon(var9, var6.contains(var9), var5));
      }

      ItemStack var10;
      if (var4 > 1) {
         var10 = (new ItemBuilder(Material.ARROW, var4 - 1)).setDisplayName(this.plugin.getLocale().getString("icon-previous")).build();
         var2.setItem(30, var10);
      }

      if (var4 < this.getMaxPage(var3)) {
         var10 = (new ItemBuilder(Material.ARROW, var4 + 1)).setDisplayName(this.plugin.getLocale().getString("icon-next")).build();
         var2.setItem(32, var10);
      }

      if (!var5) {
         ItemBuilder var11 = (new ItemBuilder(Material.BARRIER)).setDisplayName(this.plugin.getLocale().getString("icon-unequip-name")).hideAll();
         if (!var6.isEmpty()) {
            var11.addLore((Collection)this.plugin.getLocale().getStringList("icon-unequip-lore"));
         } else {
            var11.addLore((Collection)this.plugin.getLocale().getStringList("icon-selected")).addEnchantment(Enchantment.DURABILITY, 1);
         }

         var2.setItem(27, var11.build());
         if (!var6.isEmpty()) {
            var2.setItem(35, (new ItemBuilder(Material.PAPER)).setDisplayName(this.plugin.getLocale().getString("icon-display-medals")).addLore((Collection)var6.stream().map((var0) -> {
               return ChatColor.GRAY + var0.getDisplayText();
            }).collect(Collectors.toList())).build());
         }
      }

      var2.setItem(31, (new ItemBuilder(Material.BOOK)).setDisplayName(String.format(this.plugin.getLocale().getString("icon-page"), var4)).build());
      this.setMetadata(var1, var5, var4);
   }

   private ItemStack getMedalIcon(Medal var1, boolean var2, boolean var3) {
      ItemBuilder var4 = (new ItemBuilder(var1.getIcon().clone())).hideAll();
      if (var3) {
         var4.setDisplayName(ChatColor.RESET + var1.getDisplayText());
         var4.addLore(String.format(this.plugin.getLocale().getString("icon-admin-name"), var1.getName()));
      } else {
         String[] var5 = var1.getDisplayText().split("\\|");

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = var5[var6].replaceAll("%.*%", "~");
         }

         var4.setDisplayName(var5[0]);
         if (var5.length > 1) {
            var4.addLore(0, (String[])Arrays.copyOfRange(var5, 1, var5.length));
         }

         if (!var2) {
            var4.addLore((Collection)this.plugin.getLocale().getStringList("icon-equip-lore"));
         } else {
            var4.addLore((Collection)this.plugin.getLocale().getStringList("icon-selected")).addEnchantment(Enchantment.DURABILITY, 1);
         }
      }

      return var4.build();
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      if (this.userMap.containsKey(var1.getWhoClicked().getUniqueId())) {
         var1.setCancelled(true);
         if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
            Player var2 = (Player)var1.getWhoClicked();
            List var3 = (List)this.userMap.get(var2.getUniqueId());
            switch(var1.getRawSlot()) {
            case 27:
               this.plugin.getUserManager().resetDisplayMedal(var2);
               this.updateGUI(var2, var1.getInventory(), var3, this.getPage(var2), this.isAdminMode(var2));
               break;
            case 28:
            case 29:
            case 33:
            case 34:
            default:
               if (this.isAdminMode(var2)) {
                  return;
               }

               int var5 = this.getPage(var2);
               int var6 = var1.getRawSlot() + 27 * (var5 - 1);
               Medal var7 = (Medal)var3.get(var6);
               if (!this.plugin.getUserManager().getDisplayMedals(var2).contains(var7)) {
                  this.plugin.getUserManager().addDisplayMedal(var2, var7);
               } else {
                  this.plugin.getUserManager().removeDisplayMedal(var2, var7);
               }

               this.updateGUI(var2, var1.getInventory(), var3, var5, this.isAdminMode(var2));
               break;
            case 30:
            case 32:
               int var4 = this.getPage(var2) + (var1.getRawSlot() == 30 ? -1 : 1);
               this.updateGUI(var2, var1.getInventory(), var3, var4, this.isAdminMode(var2));
            case 31:
            case 35:
            }

         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
      if (this.userMap.containsKey(var1.getPlayer().getUniqueId())) {
         this.userMap.remove(var1.getPlayer().getUniqueId());
         this.removeMetedata((Player)var1.getPlayer());
      }

   }

   private int getMaxPage(List<Medal> var1) {
      return var1.size() / 27 + (var1.size() % 27 == 0 ? 0 : 1);
   }

   private void setMetadata(Player var1, boolean var2, int var3) {
      var1.setMetadata("medals_gui", new FixedMetadataValue(this.plugin, var2));
      var1.setMetadata("medals_gui_page", new FixedMetadataValue(this.plugin, var3));
   }

   private void removeMetedata(Player var1) {
      var1.removeMetadata("medals_gui", this.plugin);
      var1.removeMetadata("medals_gui_page", this.plugin);
   }

   private boolean isAdminMode(Player var1) {
      return ((MetadataValue)var1.getMetadata("medals_gui").get(0)).asBoolean();
   }

   private int getPage(Player var1) {
      return ((MetadataValue)var1.getMetadata("medals_gui_page").get(0)).asInt();
   }
}
