package org.tilegames.mc.magicchest;

import java.util.Comparator;

import net.minecraft.item.ItemStack;

public class ItemStackComparator implements Comparator<ItemStack> {

    @Override
    public int compare (ItemStack a, ItemStack b) {
        if (a == null && b == null) return 0;
        else if (a == null) return 1;
        else if (b == null) return -1;
        
        if (a.itemID == b.itemID) return 0;
        else if (a.itemID > b.itemID) return 1;
        return -1;
    }

}
