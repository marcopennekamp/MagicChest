package org.tilegames.mc.magicchest.client.gui;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class PageButton {
    
    public static final int STATE_NORMAL = 0;
    public static final int STATE_HOVER = 1;
    public static final int STATE_ACTIVE = 2;

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    
    private Page page;
    public int x;
    public int y;
    
    public PageButton (Page page, int x, int y) {
        this.page = page;
        this.x = x;
        this.y = y;
    }
    
    public int getState (Page activePage, int mx, int my) {
        if (page.getButtonId () == activePage.getButtonId ()) {
            return STATE_ACTIVE;
        }else if (inBounds (mx, my)) {
            return STATE_HOVER;
        }
        return STATE_NORMAL;
    }
    
    public int getTexture () {
        return page.getButtonTexture ();
    }
    
    public int getId () {
        return page.getButtonId ();
    }

    public Page getPage () {
        return page;
    }
    
    public boolean inBounds (int px, int py) {
        return px >= x && px <= x + WIDTH && py >= y && py <= y + HEIGHT;
    }
    
}
