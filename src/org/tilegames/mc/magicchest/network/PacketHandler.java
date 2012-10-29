package org.tilegames.mc.magicchest.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static final int COMMAND_SORT = 0x10;
    
    
    @Override
    public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.data.length < 1) return;
        if (!(player instanceof EntityPlayerMP)) return;
        handlePacket (packet, (EntityPlayerMP) player);
    }
    
    public void handlePacket (Packet250CustomPayload packet, EntityPlayerMP player) {
        if (packet.data[0] == COMMAND_SORT) {
            ByteArrayInputStream stream = new ByteArrayInputStream (packet.data, 1, 12);
            DataInputStream in = new DataInputStream (stream);
            try {
                int x = in.readInt ();
                int y = in.readInt ();
                int z = in.readInt ();
                TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
                if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
                    ((TileEntityMagicChest) entity).sortInventory ();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
