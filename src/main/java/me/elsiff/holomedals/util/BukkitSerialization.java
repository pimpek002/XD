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

   public static String itemStackToBase64(ItemStack itemStack) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {
         bukkitObjectOutputStream.writeObject(itemStack);
         return Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
      } catch (IOException e) {
         throw new IllegalStateException("Unable to save item stacks.", e);
      }
   }

   public static ItemStack itemStackFromBase64(String data) {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      try (BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream)) {
         return (ItemStack) bukkitObjectInputStream.readObject();
      } catch (IOException | ClassNotFoundException e) {
         throw new IllegalStateException("Unable to decode class type.", e);
      }
   }
}