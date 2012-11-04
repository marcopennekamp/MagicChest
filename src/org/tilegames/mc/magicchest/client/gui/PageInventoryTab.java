package org.tilegames.mc.magicchest.client.gui;

public class PageInventoryTab extends Page {

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
