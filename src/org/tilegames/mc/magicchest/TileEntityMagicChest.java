package org.tilegames.mc.magicchest;

import java.util.List;

import org.tilegames.mc.magicchest.filter.FilteringProfile;
import org.tilegames.mc.magicchest.software.Software;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class TileEntityMagicChest extends TileEntity implements IInventory {

    /* TODO(Marco): Replace hardcoded values with these constants. */
    public static final int INVENTORY_ROWS = 3;
    public static final int INVENTORY_COLUMNS = 9;
    public static final int INVENTORY_SIZE = INVENTORY_ROWS * INVENTORY_COLUMNS;
    
    @SideOnly(Side.CLIENT)
    public float lidAngle = 0.0f;
    
    @SideOnly(Side.CLIENT)
    public float previousLidAngle = lidAngle;
    
    public ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];
    
    
    /* Filtering. */
    public FilteringProfile filteringCache = null;
    
    
    public int numUsed = 0;
    
    public double pullDistance = 4.0;
    
    public boolean isChestFull = false; /* When the chest is COMPLETELY full (all slots filled with maximum size stacks. */
    
    
    public Software software;
    
    
    @Override
    public void updateEntity () {
        super.updateEntity ();
        
        if (worldObj.isRemote) { /* Only client side. */
            /* Update Chest lid angle. */
            previousLidAngle = lidAngle;
    
            /* TODO(Marco): Play Open Sound effect. */
            if (numUsed > 0 && lidAngle == 0.0f) {
                
            }
            
            if (numUsed == 0 && lidAngle > 0.0f || numUsed > 0 && lidAngle < 1.0f) {
                float previousLidAngle = lidAngle;
                if (numUsed == 0) {
                    lidAngle += -0.1f;
                }else {
                    lidAngle += 0.1f;
                }
                
                if (lidAngle > 1.0f) lidAngle = 1.0f;
                else if (lidAngle < 0.0f) lidAngle = 0.0f;
                
                /* TODO(Marco): Play Close Sound effect. */
                if (lidAngle < 0.5f && previousLidAngle >= 0.5f) {
                    
                }
            }
        
        }
        
        /* Soak items in. */
        @SuppressWarnings("unchecked")
        List<EntityItem> entityList = worldObj.getEntitiesWithinAABB (EntityItem.class, 
                AxisAlignedBB.getBoundingBox (
                        xCoord + 0.5 - pullDistance, 
                        yCoord + 0.5 - pullDistance, 
                        zCoord + 0.5 - pullDistance, 
                        xCoord + 0.5 + pullDistance, 
                        yCoord + 0.5 + pullDistance, 
                        zCoord + 0.5 + pullDistance
                ));
        int size = entityList.size();
        for (int i = 0; i < size; ++i) {
            soakEntityIn (entityList.get (i));
        }
        
    }
    
    
    
    
    private void soakEntityIn (EntityItem item) {
        if (item.isDead) return;
        if (processItemStack (item.item, false) != null) return;
        
        final double offsetX = 0.5;
        final double offsetY = 0.5;
        final double offsetZ = 0.5;
        
        double dx = (xCoord + offsetX) - item.posX;
        double dy = (yCoord + offsetY) - item.posY;
        double dz = (zCoord + offsetZ) - item.posZ;
        double distance = dx * dx + dy * dy + dz * dz;
        double pullDistanceSquared = pullDistance * pullDistance;
        
        if (distance <= pullDistanceSquared) {
            double ax = dx / distance * 0.05;
            double ay = dy / distance * 0.05; 
            double az = dz / distance * 0.05;
            
            /* Reduce gravity by 80%. */
            if (dy > 0.0) ay += 0.032;
            
            item.setVelocity (item.motionX + ax, item.motionY + ay, item.motionZ + az);
        }
    }
    
    public void onCollisionWithItem (EntityItem item) {
        ItemStack in = item.item;
        ItemStack out = processItemStack (in, true);
        if (out == null) {
            item.setDead ();
        } /* else is implicit: Stack size was reduced. */
    }
    
    
    
    public ItemStack processItemStack (ItemStack stack, boolean storeItem) {
        if (isChestFull) return stack; /* This stack can not be taken anymore. */
        
        int stackSize = stack.stackSize;
        
        /* Search for stacks the item can be put in. */
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            ItemStack slotStack = getStackInSlot (i);
            if (canStoreItemInSlot (stack, i)) {
                if (slotStack == null) { /* Put in empty slot. */
                    if (storeItem) setInventorySlotContents (i, stack);
                    return null;
                }else if (slotStack.isItemEqual (stack)) { /* Put in not empty slots. */
                    int maxAmount = slotStack.getMaxStackSize () - slotStack.stackSize;
                    if (maxAmount >= stackSize) { /* Can put on top of stack. */
                        if (storeItem) slotStack.stackSize += stackSize;
                        return null;
                    }else { /* Can only partially put on top. */
                        stackSize -= maxAmount;
                        if (storeItem) slotStack.stackSize += maxAmount;
                    }
                }
            }
        }
        
        if (storeItem) stack.stackSize = stackSize;
        return stack;
    }
    
    
    /*
     * Checks Filtering and possibly other parameters.
     */
    public boolean canStoreItemInSlot (ItemStack stack, int slot) {
        return true;
    }
    
    
    
    /* Functions that update the state of the chest. */
    
    public void updateIsChestFull () {
        isChestFull = true;
        for (int i = INVENTORY_SIZE - 1; i > 0; --i) { /* Begin at back, because the last slots are more often empty than first slots. */
            ItemStack stack = getStackInSlot (i);
            if (stack == null || stack.stackSize < stack.getMaxStackSize ()) {
                isChestFull = false;
                break;
            }
        }
    }
    
    
    
    /* Inventory Functions. */
    
    @Override
    public int getSizeInventory () {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot (int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize (int index, int number) {
        if (index >= INVENTORY_SIZE) return null;
        
        ItemStack itemStack = inventory[index];
        if (itemStack == null) return null;
        
        if (itemStack.stackSize <= number) {
            inventory[index] = null;
            onInventoryChanged ();
            return itemStack;
        }else if (itemStack.stackSize > number) {
            ItemStack newStack = itemStack.splitStack (number);
            onInventoryChanged ();
            return newStack;
        }
        
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int index, ItemStack stack) {
        inventory[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit ()) stack.stackSize = this.getInventoryStackLimit ();
        this.onInventoryChanged ();
    }

    @Override
    public String getInvName () {
        return "magic_chest.container";
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return player.getDistanceSq ((double) this.xCoord + 0.5, (double) yCoord + 0.5, (double) zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openChest () {
        ++numUsed;
        worldObj.addBlockEvent (this.xCoord, this.yCoord, this.zCoord, Block.chest.blockID, 1, numUsed);
    }

    @Override
    public void closeChest () {
        --numUsed;
        worldObj.addBlockEvent (this.xCoord, this.yCoord, this.zCoord, Block.chest.blockID, 1, numUsed);
    }
    
    public void receiveClientEvent (int id, int value) {
        if (id == 1) {
            numUsed = value;
        }
    }
    
    
    
    /* Save and load. */
    public void readFromNBT (NBTTagCompound nbt) {
        super.readFromNBT (nbt);
        NBTTagList list = nbt.getTagList ("Items");

        int tagCount = list.tagCount ();
        for (int i = 0; i < tagCount; ++i) {
            NBTTagCompound slotTag = (NBTTagCompound) list.tagAt (i);
            int id = slotTag.getByte ("Slot") & 255;

            if (id >= 0 && id < INVENTORY_SIZE) inventory[id] = ItemStack.loadItemStackFromNBT (slotTag);
        }
    }

    public void writeToNBT (NBTTagCompound nbt) {
        super.writeToNBT (nbt);
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (inventory[i] != null) {
                NBTTagCompound slotTag = new NBTTagCompound ();
                slotTag.setByte ("Slot", (byte) i);
                inventory[i].writeToNBT (slotTag);
                list.appendTag (slotTag);
            }
        }

        nbt.setTag ("Items", list);
    }
    

}
