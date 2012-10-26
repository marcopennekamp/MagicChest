package org.tilegames.mc.magicchest;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerMagicChest extends Container {
    IInventory playerInventory;
    TileEntityMagicChest chestInventory;
    int rows;
    
    
    public ContainerMagicChest (IInventory playerInventory, TileEntityMagicChest chestInventory) {
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        
        rows = chestInventory.getSizeInventory () / 9;
        
        /* Add Magic Chest slots. */
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer (new Slot (chestInventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        
        /* Add Player Inventory slots. */
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer (new Slot (playerInventory, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
            }
        }
        
        /* Add Player quickslot bar. */
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer (new Slot (playerInventory, col, 8 + col * 18, 144));
        }
    }
    
    @Override /* transferStackInSlot */
    public ItemStack func_82846_b (EntityPlayer par1EntityPlayer, int slotId) {
        Slot slot = (Slot) inventorySlots.get (slotId);
        ItemStack itemStack = null;
        
        if (slot != null && slot.getHasStack ()) {
            ItemStack slotItemStack = slot.getStack ();
            
            if (slotId >= 27) { /* Put from player inventory into chest. */
                slotItemStack = chestInventory.processItemStack (slotItemStack, true);
                if (slotItemStack == null) slot.putStack (null);
                else slot.onSlotChanged ();
                return null;
            }else { /* Put from chest into player inventory. */
                itemStack = slotItemStack.copy ();
                if (!mergeItemStack (slotItemStack, 27, 27 + 36, false)) return null;
                if (slotItemStack.stackSize == 0) slot.putStack (null);
                else slot.onSlotChanged ();
            }
        }
        
        return itemStack;
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer player) {
        return chestInventory.isUseableByPlayer (player);
    }
    
    @Override
    public void onCraftGuiClosed (EntityPlayer player) {
        super.onCraftGuiClosed (player);
        chestInventory.closeChest ();
    }

}
