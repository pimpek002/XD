package me.elsiff.holomedals;

import org.bukkit.inventory.ItemStack;

public class Medal {
   private String name;
   private String displayText;
   private ItemStack icon;

   public Medal(String var1, String var2, ItemStack var3) {
      this.name = var1;
      this.displayText = var2;
      this.icon = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getDisplayText() {
      return this.displayText;
   }

   public String getDisplayName() {
      return this.displayText.split("\\|")[0];
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public void setDisplayText(String var1) {
      this.displayText = var1;
   }

   public void setIcon(ItemStack var1) {
      this.icon = var1;
   }
}
