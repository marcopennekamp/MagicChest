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
        return 0;
    }

    
    @Override
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindAndDrawBackgroundTexture ("/MagicChest/Pages/Options.png");
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
