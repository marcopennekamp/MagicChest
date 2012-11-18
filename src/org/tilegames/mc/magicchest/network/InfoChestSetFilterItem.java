package org.tilegames.mc.magicchest.network;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class InfoChestSetFilterItem extends InfoSlotUpdate {

    public InfoChestSetFilterItem () {
        super ();
    }
    
    public InfoChestSetFilterItem (TileEntity tileEntity, int slot, ItemStack stack) {
        super (tileEntity, slot, stack);
    }
    
    @Override
    public void evaluate (EntityPlayer player) {
        TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
        if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
            TileEntityMagicChest chest = (TileEntityMagicChest) entity;
            chest.filteringCache.setItem (slot, stack);
            
            /* The server has to broadcast the filtering items. */
            if (!player.worldObj.isRemote) {
                chest.broadcastFilteringProfileItem (slot);
            }
        }
    }
    
}
