package org.tilegames.mc.magicchest.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class PageSorting extends Page {

    public static final int BUTTON_ID = Page.BASE_ID + 1;
    
    public PageSorting (GuiMagicChest gui) {
        super (gui);
    }
    

    @Override
    public String getTitle () {
        return "Sorting";
    }

    @Override
    public int getButtonId () {
        return BUTTON_ID;
    }
    
    @Override
    public int getButtonTexture () {
        return 18;
    }
    
    
    @Override
    public boolean onPageButtonRightClick () {
        GuiMagicChest gui = (GuiMagicChest) this.gui;
        
        /* Sort chest inventory. */
        gui.chest.sortInventory ();
        return true;
    }

    
    @Override
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/Options.png");
        
        String str = "Right click on the Sorting button above to sort the inventory by category!";
        final int maxWidth = gui.sizeX - 16;
        gui.getFontRenderer ().drawSplitString (str, 8, 20, maxWidth, 0x404040);
        
        gui.renderHelper.drawDevelopingBanner (20);
    }

    @Override
    public boolean onClick (int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onKeyType (char character, int key) {
        return false;
    }

}
