package org.tilegames.mc.magicchest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import org.tilegames.mc.magicchest.filter.FilteringProfile;
import org.tilegames.mc.magicchest.network.PacketHandler;
import org.tilegames.mc.magicchest.upgrade.Upgrade;

import MagicChest.common.MagicChest;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMagicChest extends TileEntity implements IInventory {

    private static final Random random = new Random ();
    
    /* TODO(Marco): Replace hardcoded values with these constants. */
    public static final int INVENTORY_ROWS = 3;
    public static final int INVENTORY_COLUMNS = 9;
    public static final int INVENTORY_SIZE = INVENTORY_ROWS * INVENTORY_COLUMNS;
    
    public static final ItemStackComparator ITEM_STACK_COMPARATOR = new ItemStackComparator ();
    
    @SideOnly(Side.CLIENT)
    public float lidAngle = 0.0f;
    
    @SideOnly(Side.CLIENT)
    public float previousLidAngle = lidAngle;
    
    public ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];
    
    public Upgrade[] upgrades = new Upgrade[3];
    
    
    /* Filtering. */
    public FilteringProfile filteringCache = new FilteringProfile ();
    
    
    public int numUsed = 0;
    
    public boolean isChestFull = false; /* When the chest is COMPLETELY full (all slots filled with maximum size stacks. */
    
    
    private boolean initialized = false;
    
    
    @Override
    public void updateEntity () {
        super.updateEntity ();
        
        if (!initialized) {
            if (worldObj.isRemote) {
                /* Request chest update. */
                PacketHandler.sendPacketChestRequestUpdate (this);
            }
            
            initialized = true;
        }
        
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
        
        for (int i = 0; i < upgrades.length; ++i) {
            Upgrade upgrade = upgrades[i];
            if (upgrade != null) {
                upgrade.update ();
            }
        }
    }
    
    
    public void onCollisionWithItem (EntityItem item) {
        ItemStack in = item.func_92014_d (); /* get item */
        int stackSizeBefore = in.stackSize;
        ItemStack out = processItemStack (in, true);
        
        if (out != null) {
            /* Update stack size on clients. */
            if (stackSizeBefore != out.stackSize) {
                PacketHandler.sendPacketUpdateItem (item); 
            }
        }else {
            item.setDead ();
        }
    }
    
    
    public ItemStack processItemStack (ItemStack stack, boolean storeItem) {
        if (isChestFull) return stack; /* This stack can not be taken anymore. */
        return processItemStack (inventory, stack, storeItem, false);
    }
    
    /**
     * @return The rest of the stack.
     */
    public ItemStack processItemStack (ItemStack[] array, ItemStack stack, boolean storeItem, boolean ignoreFilteredSlots) {
        int stackSize = stack.stackSize;
        
        /* Search for stacks the item can be put in. */
        for (int i = 0; i < array.length; ++i) {
            if (ignoreFilteredSlots && slotHasFilter (i)) continue;
            
            ItemStack slotStack = array[i];
            if (slotStack != null && slotStack.isItemEqual (stack)) {
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
        
        /* Try to put in free places. */
        for (int i = 0; i < array.length; ++i) {
            if (ignoreFilteredSlots && slotHasFilter (i)) continue;
            
            ItemStack slotStack = array[i];
            if (slotStack == null && canStoreItemInSlot (stack, i)) {
                if (storeItem) {
                    stack.stackSize = stackSize;
                    array[i] = stack;
                }
                return null;
            }
        }
        
        if (storeItem) stack.stackSize = stackSize;
        return stack;
    }
    
    /**
     * Note: 'stack' will be modified (So you might need to use stack.copy () before).
     */
    public ItemStack removeItemStack (ItemStack stack) {
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            ItemStack slot = getStackInSlot (i);
            if (slot != null && slot.isItemEqual (stack)) {
                if (slot.stackSize > stack.stackSize) {
                    slot.stackSize -= stack.stackSize;
                    stack = null;
                    break;
                }else {
                    stack.stackSize -= slot.stackSize;
                    setInventorySlotContents (i, null);
                    if (stack.stackSize == 0) {
                        stack = null;
                        break;
                    }
                }
            }
        }
        
        onInventoryChanged ();
        return stack;
    }
     
    
    /*
     * Checks Filtering and possibly other parameters.
     */
    public boolean canStoreItemInSlot (ItemStack stack, int slot) {
        ItemStack filter = filteringCache.cache[slot];
        if (filter != null) {
            return filter.itemID == stack.itemID && filter.getItemDamage () == stack.getItemDamage ();
        }
        return true;
    }
    
    public boolean slotHasFilter (int slot) {
        return filteringCache.cache[slot] != null;
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
    
    
    /* Sorting. */
    
    /* Preserves filtered slots. */
    public void sortInventory () {
        if (worldObj.isRemote) { /* Notify server. */
            ByteArrayOutputStream stream = new ByteArrayOutputStream (13);
            DataOutputStream out = new DataOutputStream (stream);
            try {
                out.writeByte (PacketHandler.COMMAND_CHEST_SORT);
                out.writeInt (xCoord);
                out.writeInt (yCoord);
                out.writeInt (zCoord);
                PacketDispatcher.sendPacketToServer (new Packet250CustomPayload ("magicchest", stream.toByteArray ()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        inventory = sortItemStackArray (inventory);
        onInventoryChanged ();
    }
    
    public ItemStack[] sortItemStackArray (ItemStack[] stacks) {
        ItemStack[] toSort = new ItemStack[stacks.length];
        
        /* Merge item stacks. */
        for (int i = 0; i < stacks.length; ++i) {
            if (slotHasFilter (i)) continue;
            
            ItemStack stack = stacks[i];
            if (stack != null) {
                processItemStack (toSort, stack, true, true);
            }
        }
        
        /* Sort item list. */
        Arrays.sort (toSort, ITEM_STACK_COMPARATOR);
        
        /* Update slots. */
        for (int i = 0, j = 0; i < stacks.length; ++i) {
            if (slotHasFilter (i)) continue;
            
            stacks[i] = toSort[j];
            ++j;
        }
        
        return stacks;
    }
    
    
    
    
    /* Inventory Functions. */
    
    /**
     * @return Whether the upgrade has been set.
     */
    public boolean setUpgrade (int index, Upgrade upgrade, InventoryPlayer playerInventory) {
        /* Don't set the upgrade when it is already set somewhere. */
        if (upgrade != null && hasUpgrade (upgrade.item)) return false;
        
        Upgrade before = upgrades[index];
        if (before != null) {
            ItemStack stack = before.stackCache;
            if (processItemStack (stack, true) != null) {
                /* Try to put it in a player slot. */
                if (!playerInventory.addItemStackToInventory (stack)) {
                    return false;
                }
            }
        }
        
        upgrades[index] = upgrade;
        return true;
    }
    
    public boolean hasUpgrade (Item item) {
        for (int i = 0; i < upgrades.length; ++i) {
            Upgrade upgrade = upgrades[i];
            if (upgrade != null && upgrade.item.shiftedIndex == item.shiftedIndex) {
                return true;
            }
        }
        return false;
    }
    
    public void dropItems () {
        /* Drop items. */
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            ItemStack stack = getStackInSlot (i);
            if (stack != null) {
                dropItem (stack);
            }
        }
        
        /* Drop upgrades. */
        for (int i = 0; i < upgrades.length; ++i) {
            Upgrade upgrade = upgrades[i];
            if (upgrade != null) {
                dropItem (upgrade.stackCache);
            }
        }
    }
    
    private void dropItem (ItemStack stack) {
        float x = random.nextFloat () * 0.8F + 0.1F;
        float y = random.nextFloat () * 0.8F + 0.1F;
        EntityItem item;

        for (float z = random.nextFloat () * 0.8F + 0.1F; stack.stackSize > 0; worldObj.spawnEntityInWorld (item)) {
            int size = random.nextInt (21) + 10;

            if (size > stack.stackSize) {
                size = stack.stackSize;
            }

            stack.stackSize -= size;
            item = new EntityItem (worldObj, 
                    (double) ((float) xCoord + x), 
                    (double) ((float) yCoord + y), 
                    (double) ((float) zCoord + z), 
                    new ItemStack (stack.itemID, size, stack.getItemDamage ()));
            float time = 0.05F;
            item.motionX = (double) ((float) random.nextGaussian() * time);
            item.motionY = (double) ((float) random.nextGaussian() * time + 0.2F);
            item.motionZ = (double) ((float) random.nextGaussian() * time);

            if (stack.hasTagCompound ()) {
                item.func_92014_d ()/* get item */.setTagCompound ((NBTTagCompound) stack.getTagCompound ().copy ());
            }
        }
    }
    
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
        worldObj.addBlockEvent (xCoord, yCoord, zCoord, MagicChest.instance.blockMagicChest.blockID, 1, numUsed);
    }

    @Override
    public void closeChest () {
        --numUsed;
        worldObj.addBlockEvent (xCoord, yCoord, zCoord, MagicChest.instance.blockMagicChest.blockID, 1, numUsed);
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
        
        /* Read filtering cache. */
        filteringCache.read (nbt);
        
        /* Read upgrades. */
        NBTTagList upgradeTagList = nbt.getTagList ("Upgrades");
        int upgradeTagCount = upgradeTagList.tagCount ();
        for (int i = 0; i < upgradeTagCount; ++i) {
            NBTTagCompound tag = (NBTTagCompound) upgradeTagList.tagAt (i);
            ItemUpgrade item = (ItemUpgrade) Item.itemsList[tag.getShort ("item")];
            Upgrade upgrade = item.getUpgradeObject (this);
            upgrade.read (tag);
            upgrades[tag.getByte ("id")] = upgrade;
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
        
        /* Write filtering cache. */
        filteringCache.write (nbt);
        
        /* Write upgrades. */
        NBTTagList upgradeTagList = new NBTTagList ();
        for (int i = 0; i < upgrades.length; ++i) {
            Upgrade upgrade = upgrades[i];
            if (upgrade != null) {
                NBTTagCompound tag = new NBTTagCompound ();
                tag.setByte ("id", (byte) i);
                tag.setShort ("item", (short) upgrade.item.shiftedIndex);
                upgrade.write (tag);
                upgradeTagList.appendTag (tag);
            }
        }
        nbt.setTag ("Upgrades", upgradeTagList);
    }
    
    
    /* Networking. */
    
    public void broadcastFilteringProfileItems (int[] indices, ItemStack[] stacks) {
        if (stacks == null) {
            stacks = new ItemStack[indices.length];
            for (int i = 0; i < indices.length; ++i) {
                stacks[i] = filteringCache.getItem (indices[i]);
            }
        }
        
        PacketHandler.sendPacketChestSetFilterItem (this, indices, stacks, PacketHandler.TARGET_ALL_CLIENTS, null);
    }

    public void broadcastUpgradeSlot (int index) {
        Upgrade upgrade = upgrades[index];
        ItemUpgrade item = (upgrade == null) ? null : upgrade.item;
        PacketHandler.sendPacketChestSetUpgradeItem (this, index, item, PacketHandler.TARGET_ALL_CLIENTS, null);
    }

}
