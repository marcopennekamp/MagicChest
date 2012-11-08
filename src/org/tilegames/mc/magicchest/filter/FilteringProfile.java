package org.tilegames.mc.magicchest.filter;

import net.minecraft.src.ItemStack;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class FilteringProfile {
    public static final int ROWS = TileEntityMagicChest.INVENTORY_ROWS;
    public static final int COLUMNS = TileEntityMagicChest.INVENTORY_COLUMNS;
    public static final int SIZE = TileEntityMagicChest.INVENTORY_SIZE;
    
    /* Provides a fast method for the filter to evaluate the current item without having to deal with row or chest filtering. */
    private ItemStack[] cache = new ItemStack[SIZE];
    
    /* The options for chest, row and slot filtering. */
    public ItemStack[] chestItems = null;
    public ItemStack[] rowItems = null;
    public ItemStack[] slotItems = null;
    
    
    /** 
     *  Updates the filtering cache in the following range. 
     */
    private void updateCache (int start, int end) {
        for (int i = start; i < end; ++i) {
            /* Check for slot filtering. */
            if (slotItems != null) {
                ItemStack item = slotItems[i];
                if (item != null) {
                    cache[i] = item;
                    continue;
                }
            }
            
            /* Check for row filtering. */
            if (rowItems != null) {
                ItemStack item = rowItems[(int) (i / COLUMNS)];
                if (item != null) {
                    cache[i] = item;
                    continue;
                }
            }
            
            /* Check for chest filtering. */
            if (chestItems != null) {
            	if (chestItems[0] != null) {
            		cache[i] = chestItems[0];
            		continue;
            	}
            }
        }
    }
    
    
    /**
     * Shared method for setRowItem and setSlotItem.
     * 
     * @return Whether the ItemStack array can be deleted.
     */
    private boolean setFilteringItemInArray (ItemStack[] itemArray, int itemArraySize, int index, ItemStack item) {
        if (item == null && itemArray != null) { /* Validate if items are empty and whether they have to be saved at all. */
            itemArray[index] = null;
            boolean delete = true;
            for (int i = 0; i < itemArray.length; ++i) {
                if (itemArray[i] != null) delete = false;
            }
            return delete;
        }
        
        if (itemArray == null) itemArray = new ItemStack[itemArraySize];
        itemArray[index] = item;
        
        return false;
    }
    
    
    /**
     * Set the item for chest filtering. 
     */
    public void setChestItem (ItemStack item) {
    	boolean delete = setFilteringItemInArray (chestItems, 1, 0, item);
        if (delete) chestItems = null;
    	
        updateCache (0, SIZE);
    }
    
    
    /**
     * Set the item for row filtering.
     */
    public void setRowItem (int row, ItemStack item) {
        if (row >= ROWS) return; /* Bounds check. */
        
        boolean delete = setFilteringItemInArray (rowItems, ROWS, row, item);
        if (delete) rowItems = null;
        
        updateCache (row * COLUMNS, (row + 1) * COLUMNS);
    }
    
    
    /**
     * Set the item for slot filtering.
     */
    public void setSlotItem (int index, ItemStack item) {
        if (index >= SIZE) return;
        
        boolean delete = setFilteringItemInArray (slotItems, SIZE, index, item);
        if (delete) slotItems = null;
        
        updateCache (index, index + 1);
    }
    
    
    /**
     * Get an element from the cache.
     */
    public ItemStack getCacheElement (int index) {
        return cache[index];
    }
    
}
