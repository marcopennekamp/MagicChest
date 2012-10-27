package org.tilegames.mc.magicchest.client.gui;

public class PageSoftware extends Page {

    public static final int BASE_ID = 104;
    
    private int id;
    
    public PageSoftware(GuiMagicChest gui, int id) {
        super (gui);
        this.id = id;
    }
    

    @Override
    public String getTitle () {
        return "Software";
    }

    @Override
    public int getButtonId () {
        return BASE_ID + id;
    }
    
    @Override
    public int getButtonTexture () {
        return 0;
    }
    
    public int getId () {
        return id;
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
