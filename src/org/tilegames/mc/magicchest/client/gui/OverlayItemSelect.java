package org.tilegames.mc.magicchest.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class OverlayItemSelect extends Gui {
    private final int verticalPadding = 5; /* Includes header and footer. */
    private final int horizontalPadding = 6;
    
    private CallbackOverlay callback;
    
    public ItemStack[] items;
    public int rows;
    
    public int x;
    public int y;
    public int sizeX;
    public int sizeY;
    public int bodySizeY;
    
    private GuiMagicChest gui;
    
    
    public OverlayItemSelect (GuiMagicChest gui, CallbackOverlay callback, Class<?> base, int x, int y) {
        List<ItemStack> itemList = new ArrayList<ItemStack> ();
        
        searchItemStacks (itemList, gui.chest.inventory, base);
        searchItemStacks (itemList, gui.getInventoryPlayer ().mainInventory, base);
        
        items = itemList.toArray (new ItemStack[0]);
        if (items.length == 0) {
            rows = 1;
        }else {
            rows = (int) Math.ceil ((double) items.length / 3.0);
        }
        
        this.gui = gui;
        this.callback = callback;
        this.x = x;
        this.y = y;
        this.sizeX = 68;
        this.bodySizeY = rows * 18;
        this.sizeY = bodySizeY + verticalPadding * 2;
    }
    
    
    private void searchItemStacks (List<ItemStack> itemList, ItemStack[] inventoryItems, Class<?> base) {
        /* Create item list. */
        for (int i = 0; i < inventoryItems.length; ++i) {
            ItemStack item = inventoryItems[i];
            if (item != null && base.isAssignableFrom (item.getItem ().getClass ())) {
                /* Search whether item has been found before. */
                boolean found = false;
                for (int j = 0; j < itemList.size (); ++j) {
                    if (itemList.get (j).isItemEqual (item)) {
                        found = true;
                        break;
                    }
                }
                
                /* Add to itemList if not found. */
                if (!found) {
                    ItemStack stack = item.copy ();
                    stack.stackSize = 1;
                    itemList.add (stack);
                }
            }
        }
    }
    
    
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindTexture ("Overlay.png");
        GL11.glColor4f (1.0f, 1.0f, 1.0f, 1.0f);
        
        /* Header. */
        drawTexturedModalRect (x, y, 0, 0, sizeX, 5);
        
        /* Body. */
        gui.renderHelper.drawRectangle ((double) x, y + 5.0, (double) (x + sizeX), y + 5.0 + bodySizeY, 
                gui.getZLevel (), 0, 5.0 / 256.0, sizeX / 256.0, 10.0 / 256.0);
        
        /* Footer. */
        drawTexturedModalRect (x, y + 5 + bodySizeY, 0, 10, sizeX, 5);
        
        /* Draw slots or no items message. */
        int posX = x + horizontalPadding;
        int posY = y + verticalPadding;
        if (items.length == 0) {
            gui.getFontRenderer ().drawSplitString ("No items found.", posX, posY, sizeX, 0x404040);
        }else {
            for (int iy = 0; iy < rows; ++iy) {
                for (int ix = 0; ix < 3; ++ix) {
                    drawTexturedModalRect (posX + ix * 18, posY + iy * 18, 0, 15, 18, 18);
                }
            }
            
            /* Draw Slot stacks. */
            ItemStack selectedItemStack = gui.renderHelper.drawItemStacks (items, 
                    x + horizontalPadding + 1, y + verticalPadding + 1, mouseX, mouseY, 3, rows, 0);
            
            /* Tooltip. */
            if (selectedItemStack != null) {
                gui.renderHelper.drawTooltip (selectedItemStack, mouseX - gui.offsetX, mouseY - gui.offsetY);
            }
        }
    }
    
    /** 
     * Expects mouseX and mouseY to be at gui.offset! 
     * @return Whether the overlay should be closed.
     */
    public boolean onClick (int mouseX, int mouseY, int button) {
        int x = this.x + horizontalPadding;
        int y = this.y + verticalPadding;
        int xEnd = x + 3 * 18;
        int yEnd = y + rows * 18;
        
        if (mouseX >= x && mouseX < xEnd && mouseY >= y && mouseY < yEnd) {
            int iX = (int) Math.floor ((mouseX - x) / 18.0);
            int iY = (int) Math.floor ((mouseY - y) / 18.0);
            int id = iY * 3 + iX;
            
            /* An item has been selected. */
            if (id < items.length) {
                callback.onItemSelected (items[id]);
                return true;
            }
        }
        
        return false;
    }
    
    
    
}
