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

   public HologramPacketListener(HoloMedals holoMedals) {
      super(holoMedals, Server.REL_ENTITY_MOVE, Server.REL_ENTITY_MOVE_LOOK, Server.ENTITY_VELOCITY, Server.ENTITY_TELEPORT, Server.ENTITY_DESTROY);
      this.holoMedals = holoMedals;
   }

   public void onPacketSending(PacketEvent event) {
      if (!event.isAsync() && !event.isAsynchronous()) {
         World world = event.getPlayer().getWorld();
         if (event.getPacketType() == Server.ENTITY_DESTROY) {
            int[] entityIds = event.getPacket().getIntegerArrays().read(0);
            for (int entityId : entityIds) {
               Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityId);
               if (entity instanceof Player) {
                  Player player = (Player) entity;
                  this.holoMedals.getHologramManager().removeHologram(player);
               }
            }
         } else {
            int entityId = event.getPacket().getIntegers().read(0);
            Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityId);
            if (entity instanceof Player) {
               Player player = (Player) entity;
               this.holoMedals.getHologramManager().updateHologram(player);
            }
         }
      }
   }
}