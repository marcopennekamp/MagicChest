package org.tilegames.mc.magicchest.client.gui;


public class PageSorting extends Page {

    public static final int BUTTON_ID = 102;
    
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
        /* Sort chest inventory. */
        gui.chest.sortInventory ();
        return true;
    }

    
    @Override
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/Options.png");
        
        String str = "Right click on the Sorting button above to sort the inventory by category!";
        final int maxWidth = GuiMagicChest.SIZE_X - 16;
        gui.getFontRenderer ().drawSplitString (str, (GuiMagicChest.SIZE_X - maxWidth) / 2, 20, maxWidth, 0x404040);
        
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
