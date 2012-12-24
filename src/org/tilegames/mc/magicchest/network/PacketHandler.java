package org.tilegames.mc.magicchest.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static final int TARGET_SERVER = 0x00;
    public static final int TARGET_ALL_CLIENTS = 0x01;
    public static final int TARGET_PLAYER = 0x02;
    
    public static final int COMMAND_OPEN_GUI = 0x10;
    
    public static final int COMMAND_CHEST_REQUEST_UPDATE = 0x20;
    public static final int COMMAND_CHEST_SORT = 0x21;
    public static final int COMMAND_CHEST_SET_FILTER_ITEM = 0x22;
    public static final int COMMAND_CHEST_SET_UPGRADE_ITEM = 0x23;
    
    public static final int COMMAND_UPDATE_ITEM = 0x30;
    
    
    private static void writePacket (int command, Info info, int target, int dimensionId, Player player) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream (1 + info.size ());
        DataOutputStream out = new DataOutputStream (stream);
        try {
            out.writeByte (command);
            info.write (out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Packet packet = new Packet250CustomPayload ("magicchest", stream.toByteArray ());
        
        if (target == 0) {
            PacketDispatcher.sendPacketToServer (packet);
        }else if (target == 1) {
            PacketDispatcher.sendPacketToAllInDimension (packet, dimensionId);
        }else if (target == 2) {
            PacketDispatcher.sendPacketToPlayer (packet, player);
        }
    }
    
    public static void sendPacketOpenGui (TileEntity tileEntity, int guiId, int param) {
        writePacket (COMMAND_OPEN_GUI, new InfoOpenGui (tileEntity, guiId, param), TARGET_SERVER, 0, null);
    }
    
    public static void sendPacketChestRequestUpdate (TileEntity tileEntity) {
        writePacket (COMMAND_CHEST_REQUEST_UPDATE, new InfoChestRequestUpdate (tileEntity), TARGET_SERVER, 0, null);
    }
    
    public static void sendPacketChestSort (TileEntity tileEntity) {
        writePacket (COMMAND_CHEST_SORT, new InfoChestSort (tileEntity), TARGET_SERVER, 0, null);
    }
    
    public static void sendPacketChestSetFilterItem (TileEntity tileEntity, int[] indices, ItemStack[] stacks, int target, Player player) {
        writePacket (COMMAND_CHEST_SET_FILTER_ITEM, new InfoChestSetFilterItem (tileEntity, indices, stacks), 
                target, tileEntity.worldObj.getWorldInfo ().getDimension (), player);
    }
    
    public static void sendPacketChestSetUpgradeItem (TileEntity tileEntity, int index, Item item, int target, Player player) {
        writePacket (COMMAND_CHEST_SET_UPGRADE_ITEM, new InfoChestSetUpgradeItem (tileEntity, index, item), 
                target, tileEntity.worldObj.getWorldInfo ().getDimension (), player);
    }
    
    public static void sendPacketUpdateItem (EntityItem item) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream (7);
        DataOutputStream out = new DataOutputStream (stream);
        try {
            out.writeByte (COMMAND_UPDATE_ITEM);
            out.writeInt (item.entityId);
            out.writeShort (item.func_92014_d ()/* get item. */.stackSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        PacketDispatcher.sendPacketToAllInDimension (new Packet250CustomPayload ("magicchest", stream.toByteArray ()), 
                item.worldObj.getWorldInfo ().getDimension ());
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
            
          case COMMAND_CHEST_REQUEST_UPDATE:
            info = new InfoChestRequestUpdate ();
            break;
            
          case COMMAND_CHEST_SET_FILTER_ITEM:
            info = new InfoChestSetFilterItem ();
            break;
            
          case COMMAND_CHEST_SET_UPGRADE_ITEM:
            info = new InfoChestSetUpgradeItem ();
            break;
            
          case COMMAND_CHEST_SORT:
            info = new InfoChestSort ();
            break;
            
          case COMMAND_UPDATE_ITEM:
            ByteArrayInputStream stream = new ByteArrayInputStream (packet.data, 1, packet.data.length - 1);
            DataInputStream in = new DataInputStream (stream);
            try {
                int entityId = in.readInt ();
                int stackSize = in.readShort ();
                Entity entity = player.worldObj.getEntityByID (entityId);
                if (entity instanceof EntityItem) {
                    ((EntityItem) entity).func_92014_d ()/* get item. */.stackSize = stackSize; 
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return;
            
          default:
            System.out.println ("Server Packet Error: Unknown packet id encountered!");
            return;
        }
        
        ByteArrayInputStream stream = new ByteArrayInputStream (packet.data, 1, packet.data.length - 1);
        DataInputStream in = new DataInputStream (stream);
        try {
            info.read (in);
            info.evaluate (player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
