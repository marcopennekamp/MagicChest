package org.tilegames.mc.magicchest.network;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class InfoChestSort extends Info {

    public InfoChestSort () {
        super ();
    }
    
    public InfoChestSort (TileEntity tileEntity) {
        super (tileEntity);
    }

    @Override
    public void evaluate (EntityPlayer player) {
        TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
        if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
            ((TileEntityMagicChest) entity).sortInventory ();
        }
    }

}
