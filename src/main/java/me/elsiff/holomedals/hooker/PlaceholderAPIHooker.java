package me.elsiff.holomedals.hooker;

import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHooker extends PlaceholderExpansion {
   private final HoloMedals plugin;

   public PlaceholderAPIHooker(HoloMedals plugin) {
      this.plugin = plugin;
   }

   @Override
   public @NotNull String getIdentifier() {
      return "holomedals";
   }

   @Override
   public @NotNull String getAuthor() {
      return plugin.getDescription().getAuthors().toString();
   }

   @Override
   public @NotNull String getVersion() {
      return plugin.getDescription().getVersion();
   }

   @Override
   public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
      if (identifier.equals("display_medal") && player != null) {
         List<Medal> medals = this.plugin.getUserManager().getDisplayMedals(player);
         return medals.isEmpty() ? "" : medals.get(0).getDisplayText();
      }
      return null;
   }

   public String setPlaceholders(Player player, String text) {
      return PlaceholderAPI.setPlaceholders(player, text);
   }
}