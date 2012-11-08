package org.tilegames.mc.magicchest.client.gui;

import MagicChest.common.MagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class PageFiltering extends Page {

    public static final int BUTTON_ID = Page.BASE_ID + 2;
    
    public PageFiltering (GuiPage gui) {
        super (gui);
    }

    
    @Override
    public String getTitle () {
        return "Filtering";
    }

    @Override
    public int getButtonId () {
        return BUTTON_ID;
    }
    
    @Override
    public int getButtonTexture () {
        return 17;
    }
    
    
    @Override
    public void onPageOpen () {
        gui.getMinecraft ().thePlayer.openGui (MagicChest.instance, 0x00000001, gui.chest.worldObj, gui.chest.xCoord, gui.chest.yCoord, gui.chest.zCoord);
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
