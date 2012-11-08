package org.tilegames.mc.magicchest.client.gui;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class PageOptions extends Page {

    public static final int BUTTON_ID = Page.BASE_ID + 0;
    
    public PageOptions (GuiPage gui) {
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
