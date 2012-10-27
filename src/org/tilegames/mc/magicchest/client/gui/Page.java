package org.tilegames.mc.magicchest.client.gui;

public abstract class Page {
    public static final int BASE_ID = 100;
    
    protected GuiMagicChest gui;
    protected IconButton button;
    
    public Page (GuiMagicChest gui) {
        this.gui = gui;
    }
    
    public void setButton (IconButton button) {
        this.button = button;
    }
    
    public abstract String getTitle ();
    public abstract int getButtonId ();
    public abstract int getButtonTexture ();
    
    public void onIconButtonRightClick () { }
    
    public abstract void draw (int mouseX, int mouseY);
    public abstract boolean onClick (int x, int y, int button);
    public abstract boolean onKeyType (char character, int key);
}
