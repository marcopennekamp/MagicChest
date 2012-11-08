package org.tilegames.mc.magicchest.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

import MagicChest.common.MagicChest;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static final int COMMAND_SORT = 0x10;
    public static final int COMMAND_OPEN_GUI = 0x11;
    
    public static void sendPacketOpenGui (int guiId, int param, int x, int y, int z) {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream (3 * 1 + 4 * 3);
        DataOutputStream out = new DataOutputStream (stream);
        try {
            out.writeByte (PacketHandler.COMMAND_OPEN_GUI);
            out.writeByte (guiId);
            out.writeByte (param);
            out.writeInt (x);
            out.writeInt (y);
            out.writeInt (z);
            PacketDispatcher.sendPacketToServer (new Packet250CustomPayload ("magicchest", stream.toByteArray ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
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
        }else if (packet.data[0] == COMMAND_OPEN_GUI) {
        	ByteArrayInputStream stream = new ByteArrayInputStream (packet.data, 1, 2 * 1 + 3 * 4);
            DataInputStream in = new DataInputStream (stream);
            try {
            	byte guiId = in.readByte ();
            	byte param = in.readByte ();
            	int x = in.readInt ();
                int y = in.readInt ();
                int z = in.readInt ();
            	player.openGui (MagicChest.instance, (((int) param) << 8) | (int) guiId, player.worldObj, x, y, z);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
