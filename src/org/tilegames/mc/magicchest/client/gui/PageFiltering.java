package org.tilegames.mc.magicchest.client.gui;

import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.filter.FilteringProfile;
import org.tilegames.mc.magicchest.network.PacketHandler;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class PageFiltering extends Page {

    public static final int BUTTON_ID = Page.BASE_ID + 2;
    
    public PageFiltering (GuiPage gui) {
        super (gui);
    }

    
    @Override
    public String getTitle () {
        return "Filtering";
    }

    @Override
    public int getButtonId () {
        return BUTTON_ID;
    }
    
    @Override
    public int getButtonTexture () {
        return 17;
    }
    
    
    @Override
    public void onPageOpen () {

    }
    
    @Override
    public void draw (int mouseX, int mouseY) {
    	GuiMagicChest gui = (GuiMagicChest) this.gui;
    	
        gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/Filtering.png");

        /* Draw chest filtering slot background when no item is in there. */
        if (gui.chest.filteringCache.chestItems[0] == null) {
            double u = 0.0;
            double v = 168.0 / 256.0;
            double uEnd = u + 32.0 / 256.0;
            double vEnd = v + 32.0 / 256.0;
            double x = 152.0;
            double y = 76.0;
            double xEnd = x + 16.0;
            double yEnd = y + 16.0;
            gui.renderHelper.drawRectangle (x, y, xEnd, yEnd, gui.getZLevel (), u, v, uEnd, vEnd);
        }

        /* Set Lightmap. */
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords (OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        
        /* The ItemStack that is currently hovered. */
        ItemStack selectedItemStack = null;
        
        /* Draw slot items. */
        ItemStack tmpItemStack = gui.renderHelper.drawItemStacks (gui.chest.filteringCache.slotItems, 8, 18, mouseX, mouseY, FilteringProfile.COLUMNS, FilteringProfile.ROWS, 0);
        if (tmpItemStack != null) selectedItemStack = tmpItemStack;
        
        /* Draw row items. */
        tmpItemStack = gui.renderHelper.drawItemStacks (gui.chest.filteringCache.rowItems, 8, 76, mouseX, mouseY, FilteringProfile.ROWS, 1, 0);
        if (tmpItemStack != null) selectedItemStack = tmpItemStack;
        
        /* Draw chest item. */
        tmpItemStack = gui.renderHelper.drawItemStacks (gui.chest.filteringCache.chestItems, 152, 76, mouseX, mouseY, 1, 1, 0);
        if (tmpItemStack != null) selectedItemStack = tmpItemStack;
        
        /* Draw Tooltip. */
        if (selectedItemStack != null) { 
            gui.renderHelper.drawTooltip (selectedItemStack, mouseX - gui.offsetX, mouseY - gui.offsetY);
        }
    }

    private int checkSlotClicks (int mouseX, int mouseY, int slotX, int slotY, int columns, int rows) {
    	int slotXEnd = slotX + 18 * columns;
    	int slotYEnd = slotY + 18 * rows;
    	if (mouseX >= slotX - 1 && mouseX <= slotXEnd + 1 && mouseY >= slotY - 1 && mouseY <= slotYEnd + 1) {
			return (mouseY - slotY + 1) / 18 * columns + (mouseX - slotX + 1) / 18;
    	}
    	return -1;
    }
    
    @Override
    public boolean onClick (int x, int y, int button) {
    	if (button != 0 && button != 1) return false;
    	
    	GuiMagicChest gui = (GuiMagicChest) this.gui;

    	x -= gui.offsetX;
    	y -= gui.offsetY;
    	
    	int id;
    	
    	/* Check for slot items click. */
    	id = checkSlotClicks (x, y, 8, 18, FilteringProfile.COLUMNS, FilteringProfile.ROWS);
    	
    	/* Check for row items click. */
    	if (id == -1) {
    		id = checkSlotClicks (x, y, 8, 76, FilteringProfile.ROWS, 1);
    		if (id != -1) id += FilteringProfile.SIZE;
    	}
    	
    	/* Check for chest items click. */
    	if (id == -1) {
    		id = checkSlotClicks (x, y, 152, 76, 1, 1);
    		if (id != -1) id += FilteringProfile.SIZE + FilteringProfile.ROWS;
    	}
    	
    	/* Slot clicked -> Open ItemBrowser. */
    	if (id != -1) {
    	    if (button == 0) {
    	        /* Open Item Browser GUI. */
    	        PacketHandler.sendPacketOpenGui (gui.chest, GuiFilteringItemBrowser.ID, id);
    	    }else if (button == 1) {
    	        /* Clean slot. */
    	        PacketHandler.sendPacketChestSetFilterItem (gui.chest, new int[] { id }, new ItemStack[] { null }, PacketHandler.TARGET_SERVER, null);
    	    }
	    	return true;
    	}
        return false;
    }

    @Override
    public boolean onKeyType (char character, int key) {
        return false;
    }
    
    
}
