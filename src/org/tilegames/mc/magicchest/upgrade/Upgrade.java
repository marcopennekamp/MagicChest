package org.tilegames.mc.magicchest.upgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.tilegames.mc.magicchest.ItemUpgrade;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

public abstract class Upgrade {

    public TileEntityMagicChest chest;
    public ItemUpgrade item;
    public ItemStack stackCache;
    
    
    public void init (TileEntityMagicChest chest, ItemUpgrade item) {
        this.chest = chest;
        this.item = item;
        stackCache = new ItemStack (item, 1);
    }


    public abstract void update ();

    public void read (NBTTagCompound tag) { }
    public void write (NBTTagCompound tag) { }
}
