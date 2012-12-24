package org.tilegames.mc.magicchest.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

public class FilteringProfile {
    public static final int MAX_INDEX = 30;
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
        else if (index < MAX_INDEX) setRowItem (index - SIZE, item);
        else if (index == MAX_INDEX) setChestItem (item);
    }
    
    public ItemStack getItem (int index) {
        if (index < SIZE) {
            return slotItems[index];
        }else if (index < MAX_INDEX) {
            return rowItems[index - SIZE];
        }else if (index == MAX_INDEX) {
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
    
    
    /**
     * Get the total amount of individual filtering items.
     */
    public int getItemCount () {
        int count = 0;
        for (int i = 0; i <= MAX_INDEX; ++i) {
            if (getItem (i) != null) ++count;
        }
        return count;
    }
    
    
    public void read (NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList ("FilteringProfile");

        int tagCount = list.tagCount ();
        for (int i = 0; i < tagCount; ++i) {
            NBTTagCompound itemTag = (NBTTagCompound) list.tagAt (i);
            int index = itemTag.getByte ("i") & 0xFF;
            int id = itemTag.getShort ("I");
            int damage = itemTag.getShort ("d");
            ItemStack stack = new ItemStack (id, 1, damage);
            if (index >= 0 && index <= MAX_INDEX) setItem (index, stack);
        }
    }
    
    public void write (NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i <= MAX_INDEX; ++i) {
            ItemStack stack = getItem (i);
            if (stack != null) {
                NBTTagCompound slotTag = new NBTTagCompound ();
                slotTag.setByte ("i", (byte) i);
                slotTag.setShort ("I", (short) stack.itemID);
                slotTag.setShort ("d", (short) stack.getItemDamage ());
                list.appendTag (slotTag);
            }
        }

        nbt.setTag ("FilteringProfile", list);
    }
    
    
    
}
