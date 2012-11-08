package org.tilegames.mc.magicchest.client.gui;

import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.network.PacketHandler;

public class PageInventoryTab extends Page {
	private static final int SLOT_X = 8;
	private static final int SLOT_X_END = SLOT_X + 18 * 9;
	private static final int SLOT_Y = 18;
	private static final int SLOT_Y_END = SLOT_Y + 18 * 6;
	
	private int id;
    
    public PageInventoryTab (GuiPage gui, int id) {
        super (gui);
        this.id = id;
    }

    @Override
    public String getTitle () {
        return "Choose Item";
    }

    @Override
    public int getButtonId () {
        return Page.BASE_ID + id;
    }

    @Override
    public int getButtonTexture () {
        return 0;
    }

    
    @Override
    public void draw (int mouseX, int mouseY) {
    	GuiFilteringItemBrowser gui = (GuiFilteringItemBrowser) this.gui;
    	
    	/* Draw Background. */
    	gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/FilteringItemBrowser.png");
    	
        /* Set Lightmap. */
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords (OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        
        /* Draw items. */
        ItemStack[] items = GuiFilteringItemBrowser.items;
        /* int start = gui.row * 9;
        int end = (gui.row + 6) * 9;
        if (end > items.length) end = items.length;
        int i = start;
        
    	for (int y = 18; y < 18 + 18 * 6; y += 18) {
    		for (int x = 8; x < 8 + 18 * 9; x += 18) {
        		ItemStack stack = null;
        		if (i < end) stack = items[i];
        		
        		/* Draw Item Stack. */
        		/* if (stack != null) {
	        		gui.setZLevel (100.0f);
	        		gui.renderHelper.drawItemStack (stack, x, y);
	        		gui.setZLevel (0.0f);
        		}
        		
        		/* Check hover status. */
               /* if (gui.renderHelper.pointInRectangle (x, y, 16, 16, mouseX, mouseY)) {
                    selectedItemStack = stack;
                    gui.renderHelper.drawHoverRectangle (x, y, 16, 16, 0x80FFFFFF);
                }
        		
        		++i;
        	}
        } */
        ItemStack selectedItemStack = gui.renderHelper.drawItemStacks (items, 18, 8, mouseX, mouseY, 9, 6, gui.row * 9);
        
        
        /* Draw Tooltip. */
        if (selectedItemStack != null) { 
            gui.renderHelper.drawTooltip (selectedItemStack, mouseX - gui.offsetX, mouseY - gui.offsetY);
        }
    }

    
    @Override
    public boolean onClick (int x, int y, int button) {
    	if (button != 0) return false;
    	
    	GuiFilteringItemBrowser gui = (GuiFilteringItemBrowser) this.gui;
    	ItemStack[] items = GuiFilteringItemBrowser.items;

    	x -= gui.offsetX;
    	y -= gui.offsetY;
    	
    	ItemStack stack = null;
    	if (x >= SLOT_X - 1 && x <= SLOT_X_END + 1 && y >= SLOT_Y - 1 && y <= SLOT_Y_END + 1) {
			int id = (y - SLOT_Y + 1) / 18 * 9 + (x - SLOT_X + 1) / 18 + gui.row * 9;
			if (id < items.length) {
				stack = items[id];
			}
    	}

    	if (stack != null) {
    		/* Send item to server. */
    		
    		
    		/* Open Filtering GUI. */
	    	PacketHandler.sendPacketOpenGui (0, GuiMagicChest.PAGE_FILTERING, gui.chest.xCoord, gui.chest.yCoord, gui.chest.zCoord);
	    	return true;
    	}
        return false;
    }

    @Override
    public boolean onKeyType (char character, int key) {
        return false;
    }
    
}
