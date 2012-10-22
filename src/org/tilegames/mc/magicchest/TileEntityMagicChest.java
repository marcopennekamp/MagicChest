package org.tilegames.mc.magicchest;

import java.util.List;

import org.tilegames.mc.magicchest.software.Software;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public class TileEntityMagicChest extends TileEntity implements IInventory {

    public static final int INVENTORY_SIZE = 27;
    
    
    
    public float lidAngle = 0.0f;
    public float previousLidAngle = lidAngle;
    
    public ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];
    public int numUsed = 0;
    
    public double pullDistance = 4.0;
    
    
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
            
            item.setVelocity (item.motionX + ax, item.motionY + ay, item.motionZ + az);
            
            /* if (distance < 0.5) distance = 0.5;
            
            double speed = 0.5 / distance;
            
            //if (speed > 1) speed = 1;
            //else if (speed < -1) speed = -1;
            
            double rotationMotion = Math.atan2 (item.motionZ, item.motionX);
            double rotation = Math.atan2 (dz, dx);
            
            double vx = Math.cos (rotation) * speed;
            double vz = Math.sin (rotation) * speed; */
            
            /* Rotation difference, if entity does not GO into the direction of the chest, don't increase Y. */
           /* double vy = 0.0;
            if (Math.abs (rotation - rotationMotion) <= 90) {
                if (distanceXSquared <= 1.5 && distanceZSquared <= 1.5 && distanceYSquared <= 1.5) {
                    if (dy < 0) {
                        vy = -0.05 * ((distanceXSquared + distanceZSquared) * 0.5 + 1f);
                    }else {
                        vy = 0.05 * ((distanceXSquared + distanceZSquared) * 0.5 + 1f);
                    }
                }else {
                    double sY = pullDistance - absDy;
                    if (sY < 0) sY = 0;
                    if (dy < 0) {
                        vy = -0.01 * sY;
                    }else {
                        vy = 0.01 * sY;
                    }
                }
                
                if (vy > 1) vy = 1;
                else if (vy < -1) vy = -1;
            }
            
            item.setVelocity (vx, vy, vz); */
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
        int stackSize = stack.stackSize;
        
        /* Search for stacks the item can be put in. */
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            ItemStack slotStack = getStackInSlot (i);
            if (canStoreItemInSlot (stack, i)) {
                if (slotStack == null) { /* Put in empty slot. */
                    if (storeItem) setInventorySlotContents (i, stack);
                    return null;
                }else if (slotStack.isItemEqual (stack)) { /* Put in not empty slots. */
                    int maxAmount = slotStack.getMaxStackSize () - stackSize;
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
    public boolean isUseableByPlayer (EntityPlayer var1) {
        return false;
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

}
