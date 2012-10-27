package org.tilegames.mc.magicchest.client.gui;

public class PageOptions extends Page {

    public static final int BUTTON_ID = 101;
    
    public PageOptions (GuiMagicChest gui) {
        super (gui);
    }
    

    @Override
    public String getTitle () {
        return "Options";
    }
    
    @Override
    public int getButtonId () {
        return BUTTON_ID;
    }
    
    @Override
    public int getButtonTexture () {
        return 1;
    }
    

    @Override
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/Options.png");
        
        gui.renderHelper.drawDevelopingBanner (0);
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
