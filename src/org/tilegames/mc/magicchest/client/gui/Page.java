package org.tilegames.mc.magicchest.client.gui;

public abstract class Page {
    public static final int BASE_ID = 101;
    
    protected GuiMagicChest gui;
    protected PageButton button;
    
    public Page (GuiMagicChest gui) {
        this.gui = gui;
    }
    
    public void setButton (PageButton button) {
        this.button = button;
    }
    
    public abstract String getTitle ();
    public abstract int getButtonId ();
    public abstract int getButtonTexture ();
    
    public boolean onPageButtonRightClick () { return false; }
    
    public abstract void draw (int mouseX, int mouseY);
    public abstract boolean onClick (int x, int y, int button);
    public abstract boolean onKeyType (char character, int key);
}
