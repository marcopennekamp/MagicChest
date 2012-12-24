package org.tilegames.mc.magicchest.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class InfoChestSetFilterItem extends InfoSlotUpdate {

    public InfoChestSetFilterItem () {
        super ();
    }
    
    public InfoChestSetFilterItem (TileEntity tileEntity, int[] slots, ItemStack[] stacks) {
        super (tileEntity, slots, stacks);
    }
    
    @Override
    public void evaluate (EntityPlayer player) {
        TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
        if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
            TileEntityMagicChest chest = (TileEntityMagicChest) entity;
            
            for (int i = 0; i < slots.length; ++i) {
                chest.filteringCache.setItem (slots[i], stacks[i]);
            }
            
            /* The server has to broadcast the filtering items. */
            if (!player.worldObj.isRemote) {
                chest.broadcastFilteringProfileItems (slots, stacks);
            }
        }
    }
    
}
