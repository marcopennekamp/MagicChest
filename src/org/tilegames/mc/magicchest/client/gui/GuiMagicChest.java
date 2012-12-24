package org.tilegames.mc.magicchest.client.gui;

import net.minecraft.inventory.IInventory;

import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/* Note:
 *  To make the GUI more customizable, I unfortunately had to copy (and improve 
 *  of course) the rendering code of GuiContainer. 
 */

@SideOnly(Side.CLIENT)
public class GuiMagicChest extends GuiPage {
    public ContainerMagicChest container;
    
    public static final int ID = 0;
    
    public static final int PAGE_INVENTORY = 0;
    public static final int PAGE_SORTING = 1;
    public static final int PAGE_FILTERING = 2;
    public static final int PAGE_UPGRADE_1 = 3;
    public static final int PAGE_SOFTWARE_1 = 6;
    
    
    public GuiMagicChest (IInventory playerInventory, TileEntityMagicChest chest, int pageId) {
        super (chest, pageId);
        
        sizeX = 176;
        sizeY = 168;
        
        container = new ContainerMagicChest (playerInventory, chest);
    }
    
    
    @Override
    public Page setupPages (int pageId) {
        pages.add (new PageInventory (this));
        
        setupPage (new PageSorting (this), 79, 6);
        setupPage (new PageFiltering (this), 87, 6);
        for (int i = 0; i < 3; ++i) {
            setupPage (new PageUpgrade (this, i), 108 + i * 8, 6);
        }
        for (int i = 0; i < 3; ++i) {
            setupPage (new PageSoftware (this, i), 145 + i * 8, 6);
        }
        
        return pages.get (pageId);
    }
    
    public void initGui () {
        super.initGui ();
        mc.thePlayer.openContainer = container;
    }
    
    @Override
    public Page getSelectedPage (PageButton button) {
        if (button.getId () == page.getButtonId ()) { /* "Close" page. */
            return new PageInventory (this);
        }
        return null;
    }
    
    @Override
    public void onGuiClosed () {
        super.onGuiClosed ();
        if (mc.thePlayer != null) container.onCraftGuiClosed (mc.thePlayer);
    }


    @Override
    public void drawForeground () {
        renderHelper.drawPageButton (1, 71, 6, PageButton.STATE_NORMAL);
        renderHelper.drawPageButton (2, 100, 6, PageButton.STATE_NORMAL);
        renderHelper.drawPageButton (3, 137, 6, PageButton.STATE_NORMAL);
    }
    
}
