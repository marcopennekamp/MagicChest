package org.tilegames.mc.magicchest.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.filter.FilteringProfile;

import cpw.mods.fml.common.network.Player;

public class InfoChestRequestUpdate extends Info {

    public InfoChestRequestUpdate () {
        super ();
    }
    
    public InfoChestRequestUpdate (TileEntity tileEntity) {
        super (tileEntity);
    }
    
    @Override
    public void evaluate (EntityPlayer player) {
        TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
        if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
            TileEntityMagicChest chest = (TileEntityMagicChest) entity;
        
            /* Broadcast filtering items. */
            int length = chest.filteringCache.getItemCount ();
            int[] indices = new int[length];
            ItemStack[] stacks = new ItemStack[length];
            for (int i = 0, j = 0; i <= FilteringProfile.MAX_INDEX; ++i) {
                ItemStack stack = chest.filteringCache.getItem (i);
                if (stack != null) {
                    indices[j] = i;
                    stacks[j] = stack;
                    ++j;
                }
            }
            PacketHandler.sendPacketChestSetFilterItem (chest, indices, stacks, PacketHandler.TARGET_PLAYER, (Player) player);
            
            /* Broadcast upgrade items. */
            for (int i = 0; i < chest.upgrades.length; ++i) {
                if (chest.upgrades[i] != null) {
                    chest.broadcastUpgradeSlot (i);
                }
            }
        }
    }

}
