package me.elsiff.holomedals.hooker;

import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.elsiff.holomedals.HoloMedals;
import me.elsiff.holomedals.Medal;
import org.bukkit.entity.Player;

public class PlaceholderAPIHooker extends EZPlaceholderHook {
   private final HoloMedals plugin;

   public PlaceholderAPIHooker(HoloMedals var1) {
      super(var1, "holomedals");
      this.plugin = var1;
      this.hook();
   }

   public String onPlaceholderRequest(Player var1, String var2) {
      if (var2.equals("display_medal") && var1 != null) {
         List var3 = this.plugin.getUserManager().getDisplayMedals(var1);
         return var3.isEmpty() ? "" : ((Medal)var3.get(0)).getDisplayText();
      } else {
         return null;
      }
   }

   public String setPlaceholders(Player var1, String var2) {
      return PlaceholderAPI.setPlaceholders(var1, var2);
   }
}
