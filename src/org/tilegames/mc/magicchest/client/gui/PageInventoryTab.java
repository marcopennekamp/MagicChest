package org.tilegames.mc.magicchest.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;

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
    	
    	/* Draw scroll button. */
        final double scrollFieldMax = 106 - 15;
        double u = (194.0 + (gui.rowCount <= 6 ? 12.0 : 0.0)) / 256.0;
        double v = 0.0;
        double uEnd = u + 12.0 / 256.0;
        double vEnd = v + 15.0 / 256.0;
        double x = 174.0;
        double y = 18.0 + (scrollFieldMax * gui.scroll);
        double xEnd = x + 12.0;
        double yEnd = y + 15.0;
        gui.renderHelper.drawRectangle (x, y, xEnd, yEnd, gui.getZLevel (), u, v, uEnd, vEnd);
    	
        /* Set Lightmap. */
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords (OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        
        /* Draw items. */
        ItemStack selectedItemStack = gui.renderHelper.drawItemStacks (GuiFilteringItemBrowser.items, 8, 18, mouseX, mouseY, 9, 6, gui.row * 9);
        
        
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
    	    PacketHandler.sendPacketChestSetFilterItem (gui.chest, new int [] { gui.filteringSlot }, new ItemStack[] { stack }, PacketHandler.TARGET_SERVER, null);
    	    
    		/* Open Filtering GUI. */
	    	PacketHandler.sendPacketOpenGui (gui.chest, GuiMagicChest.ID, GuiMagicChest.PAGE_FILTERING);
	    	return true;
    	}
    	
        return false;
    }

    @Override
    public boolean onKeyType (char character, int key) {
        return false;
    }
    
}
