package org.tilegames.mc.magicchest.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static final int COMMAND_OPEN_GUI = 0x10;
    
    public static final int COMMAND_CHEST_SORT = 0x20;
    public static final int COMMAND_CHEST_SET_FILTER_ITEM = 0x21;
    
    
    private static void writePacket (int command, Info info, boolean toClients, int dimensionId) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream (1 + info.size ());
        DataOutputStream out = new DataOutputStream (stream);
        try {
            out.writeByte (command);
            info.write (out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Packet packet = new Packet250CustomPayload ("magicchest", stream.toByteArray ());
        if (toClients) PacketDispatcher.sendPacketToAllInDimension (packet, dimensionId);
        else PacketDispatcher.sendPacketToServer (packet);
    }
    
    public static void sendPacketOpenGui (TileEntity tileEntity, int guiId, int param) {
        writePacket (COMMAND_OPEN_GUI, new InfoOpenGui (tileEntity, guiId, param), false, 0);
    }
    
    public static void sendPacketChestSort (TileEntity tileEntity) {
        writePacket (COMMAND_CHEST_SORT, new InfoChestSort (tileEntity), false, 0);
    }
    
    public static void sendPacketChestSetFilterItem (TileEntity tileEntity, int slot, ItemStack stack, boolean toClients) {
        writePacket (COMMAND_CHEST_SET_FILTER_ITEM, new InfoChestSetFilterItem (tileEntity, slot, stack), 
                toClients, tileEntity.worldObj.getWorldInfo ().getDimension ());
    }
    
    @Override
    public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.data.length < 1) return;
        if ((player instanceof EntityPlayerMP) || (player instanceof EntityClientPlayerMP)) {
            handlePacket (packet, (EntityPlayer) player);
        }
    }
    
    public void handlePacket (Packet250CustomPayload packet, EntityPlayer player) {
        Info info;
        switch (packet.data[0]) {
          case COMMAND_OPEN_GUI:
            info = new InfoOpenGui ();
            break;
            
          case COMMAND_CHEST_SET_FILTER_ITEM:
            info = new InfoChestSetFilterItem ();
            break;
            
          case COMMAND_CHEST_SORT:
            info = new InfoChestSort ();
            break;
            
          default:
            System.out.println ("Server Packet Error: Unknown packet id encountered!");
            return;
        }
        
        ByteArrayInputStream stream = new ByteArrayInputStream (packet.data, 1, info.size ());
        DataInputStream in = new DataInputStream (stream);
        try {
            info.read (in);
            info.evaluate (player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
