package me.elsiff.holomedals.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class BukkitSerialization {
   private BukkitSerialization() {
      throw new IllegalStateException("Utility class");
   }

   public static String itemStackToBase64(ItemStack var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         BukkitObjectOutputStream var2 = new BukkitObjectOutputStream(var1);
         Throwable var3 = null;

         String var4;
         try {
            var2.writeObject(var0);
            var4 = Base64Coder.encodeLines(var1.toByteArray());
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         throw new IllegalStateException("Unable to save item stacks.", var16);
      }
   }

   public static ItemStack itemStackFromBase64(String var0) {
      ByteArrayInputStream var1 = new ByteArrayInputStream(Base64Coder.decodeLines(var0));

      try {
         BukkitObjectInputStream var2 = new BukkitObjectInputStream(var1);
         Throwable var3 = null;

         ItemStack var4;
         try {
            var4 = (ItemStack)var2.readObject();
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (ClassNotFoundException var16) {
         throw new IOException("Unable to decode class type.", var16);
      }
   }
}
