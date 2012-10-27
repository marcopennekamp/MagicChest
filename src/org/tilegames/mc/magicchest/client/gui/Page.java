package org.tilegames.mc.magicchest.client.gui;

public abstract class Page {
    protected GuiMagicChest gui;
    
    public Page (GuiMagicChest gui) {
        this.gui = gui;
    }
    
    public abstract String getTitle ();
    public abstract int getButtonId ();
    
    public abstract void draw (int mouseX, int mouseY);
    public abstract void onClick (int x, int y, int button);
    public abstract void onKeyType (char character, int key);
}
