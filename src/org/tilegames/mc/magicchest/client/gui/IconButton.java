package org.tilegames.mc.magicchest.client.gui;

public class IconButton {
    
    public static final int STATE_NORMAL = 0;
    public static final int STATE_HOVER = 1;
    public static final int STATE_ACTIVE = 2;

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    
    public int id;
    public int x;
    public int y;
    public int texture;
    
    public IconButton (int id, int x, int y, int texture) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.texture = texture;
    }
    
    public int getState (Page page, int mx, int my) {
        if (id == page.getButtonId ()) {
            return STATE_ACTIVE;
        }else if (inBounds (mx, my)) {
            return STATE_HOVER;
        }
        return STATE_NORMAL;
    }
    
    public int getTexture () {
        return texture;
    }
    
    public boolean inBounds (int px, int py) {
        return px >= x && px <= x + WIDTH && py >= y && py <= y + HEIGHT;
    }
    
}
