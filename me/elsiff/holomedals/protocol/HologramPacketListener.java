package me.elsiff.holomedals.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.elsiff.holomedals.HoloMedals;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HologramPacketListener extends PacketAdapter {
   private final HoloMedals holoMedals;

   public HologramPacketListener(HoloMedals var1) {
      super(var1, new PacketType[]{Server.REL_ENTITY_MOVE, Server.REL_ENTITY_MOVE_LOOK, Server.ENTITY_VELOCITY, Server.ENTITY_TELEPORT, Server.ENTITY_DESTROY});
      this.holoMedals = var1;
   }

   public void onPacketSending(PacketEvent var1) {
      if (!var1.isAsync() && !var1.isAsynchronous()) {
         World var3;
         if (var1.getPacketType() == Server.ENTITY_DESTROY) {
            int[] var2 = (int[])var1.getPacket().getIntegerArrays().read(0);
            var3 = var1.getPlayer().getWorld();
            int[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               int var7 = var4[var6];
               Entity var8 = ProtocolLibrary.getProtocolManager().getEntityFromID(var3, var7);
               if (var8 instanceof Player) {
                  Player var9 = (Player)var8;
                  this.holoMedals.getHologramManager().removeHologram(var9);
               }
            }
         } else {
            int var10 = (Integer)var1.getPacket().getIntegers().read(0);
            var3 = var1.getPlayer().getWorld();
            Entity var11 = ProtocolLibrary.getProtocolManager().getEntityFromID(var3, var10);
            Player var12 = null;
            if (var11 instanceof Player) {
               var12 = (Player)var11;
            }

            if (var12 != null) {
               this.holoMedals.getHologramManager().updateHologram(var12);
            }
         }

      }
   }
}
