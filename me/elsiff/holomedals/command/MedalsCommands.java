package me.elsiff.holomedals.command;

import me.elsiff.holomedals.HoloMedals;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MedalsCommands implements CommandExecutor {
   private final HoloMedals plugin;

   public MedalsCommands(HoloMedals var1) {
      this.plugin = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length > 0) {
         var1.sendMessage(this.plugin.getLocale().getString("invalid-command"));
         return true;
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage(this.plugin.getLocale().getString("in-game-command"));
         return true;
      } else {
         Player var5 = (Player)var1;
         this.plugin.getMedalsGUI().openGUI(var5);
         return true;
      }
   }
}
