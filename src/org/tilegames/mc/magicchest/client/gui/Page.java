package org.tilegames.mc.magicchest.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public abstract class Page {
    public static final int BASE_ID = 101;
    
    protected GuiPage gui;
    protected PageButton button;
    
    public Page (GuiPage gui) {
        this.gui = gui;
    }
    
    public void setButton (PageButton button) {
        this.button = button;
    }
    
    public abstract String getTitle ();
    public abstract int getButtonId ();
    public abstract int getButtonTexture ();
    
    public boolean onPageButtonRightClick () { return false; }
    
    public void tick () { }
    public void onPageOpen () { }
    public void onPageClose () { }
    
    public abstract void draw (int mouseX, int mouseY);
    public abstract boolean onClick (int x, int y, int button);
    public abstract boolean onKeyType (char character, int key);
}
