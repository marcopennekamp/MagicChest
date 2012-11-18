package org.tilegames.mc.magicchest.filter;

import net.minecraft.src.ItemStack;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class FilteringProfile {
    public static final int ROWS = TileEntityMagicChest.INVENTORY_ROWS;
    public static final int COLUMNS = TileEntityMagicChest.INVENTORY_COLUMNS;
    public static final int SIZE = TileEntityMagicChest.INVENTORY_SIZE;
    
    /* Provides a fast method for the filter to evaluate the current item without having to deal with row or chest filtering. */
    public ItemStack[] cache = new ItemStack[SIZE];
    
    /* The options for chest, row and slot filtering. */
    public ItemStack[] chestItems = new ItemStack[1];
    public ItemStack[] rowItems = new ItemStack[ROWS];
    public ItemStack[] slotItems = new ItemStack[SIZE];
    
    
    /** 
     *  Updates the filtering cache in the following range. 
     */
    private void updateCache (int start, int end) {
        for (int i = start; i < end; ++i) {
            /* Check for slot filtering. */
            {
                ItemStack item = slotItems[i];
                cache[i] = item; /* Clears cache simultaneously. */
                if (item != null) {
                    continue;
                }
            }
            
            /* Check for row filtering. */
            {
                ItemStack item = rowItems[(int) (i / COLUMNS)];
                if (item != null) {
                    cache[i] = item;
                    continue;
                }
            }
            
            /* Check for chest filtering. */
            {
            	if (chestItems[0] != null) {
            		cache[i] = chestItems[0];
            		continue;
            	}
            }
        }
    }
    
    
    /**
     * Set the item for chest filtering. 
     */
    public void setChestItem (ItemStack item) {
        chestItems[0] = item;
        updateCache (0, SIZE);
    }
    
    
    /**
     * Set the item for row filtering.
     */
    public void setRowItem (int row, ItemStack item) {
        if (row >= ROWS) return; /* Bounds check. */
        
        rowItems[row] = item;
        updateCache (row * COLUMNS, (row + 1) * COLUMNS);
    }
    
    
    /**
     * Set the item for slot filtering.
     */
    public void setSlotItem (int index, ItemStack item) {
        if (index >= SIZE) return;
        
        slotItems[index] = item;
        updateCache (index, index + 1);
    }
    
    
    /**
     * Universal methods for slot, row and chest items.
     * Index 0 - 26 : slot.
     * Index 27 - 29: row.
     * Index 30     : chest.
     */
    public void setItem (int index, ItemStack item) {
        if (index < SIZE) setSlotItem (index, item);
        else if (index <= 29) setRowItem (index - SIZE, item);
        else if (index == 30) setChestItem (item);
    }
    
    public ItemStack getItem (int index) {
        if (index < SIZE) {
            return slotItems[index];
        }else if (index <= 29) {
            return rowItems[index - SIZE];
        }else if (index == 30) {
            return chestItems[0];
        }
        return null;
    }
    
    
    /**
     * Get an element from the cache.
     */
    public ItemStack getCacheElement (int index) {
        return cache[index];
    }
    
}
