package me.elsiff.holomedals.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
   private ItemStack item;

   public ItemBuilder(Material var1) {
      this.item = new ItemStack(var1);
   }

   public ItemBuilder(Material var1, int var2) {
      this.item = new ItemStack(var1, var2);
   }

   public ItemBuilder(Material var1, int var2, short var3) {
      this.item = new ItemStack(var1, var2, var3);
   }

   public ItemBuilder(ItemStack var1) {
      this.item = var1;
   }

   public ItemBuilder setDisplayName(String var1) {
      ItemMeta var2 = this.item.getItemMeta();
      var2.setDisplayName(var1);
      this.item.setItemMeta(var2);
      return this;
   }

   public ItemBuilder addLore(String... var1) {
      ItemMeta var2 = this.item.getItemMeta();
      Object var3 = var2.hasLore() ? var2.getLore() : new ArrayList();
      Collections.addAll((Collection)var3, var1);
      var2.setLore((List)var3);
      this.item.setItemMeta(var2);
      return this;
   }

   public ItemBuilder addLore(Collection<? extends String> var1) {
      ItemMeta var2 = this.item.getItemMeta();
      Object var3 = var2.hasLore() ? var2.getLore() : new ArrayList();
      ((List)var3).addAll(var1);
      var2.setLore((List)var3);
      this.item.setItemMeta(var2);
      return this;
   }

   public ItemBuilder addLore(int var1, String... var2) {
      ItemMeta var3 = this.item.getItemMeta();
      Object var4 = var3.hasLore() ? var3.getLore() : new ArrayList();
      ((List)var4).addAll(var1, Lists.newArrayList(var2));
      var3.setLore((List)var4);
      this.item.setItemMeta(var3);
      return this;
   }

   public ItemBuilder setLore(List<String> var1) {
      ItemMeta var2 = this.item.getItemMeta();
      var2.setLore(var1);
      this.item.setItemMeta(var2);
      return this;
   }

   public ItemBuilder addEnchantment(Enchantment var1, int var2) {
      this.item.addUnsafeEnchantment(var1, var2);
      return this;
   }

   public ItemBuilder addItemFlags(ItemFlag... var1) {
      ItemMeta var2 = this.item.getItemMeta();
      var2.addItemFlags(var1);
      this.item.setItemMeta(var2);
      return this;
   }

   public ItemBuilder hideAll() {
      this.addItemFlags(ItemFlag.values());
      return this;
   }

   public ItemStack build() {
      return this.item;
   }
}
