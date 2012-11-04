package org.tilegames.mc.magicchest.client.gui;

import org.tilegames.mc.magicchest.TileEntityMagicChest;

import MagicChest.common.MagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class GuiFilteringItemBrowser extends GuiPage {

    public GuiFilteringItemBrowser (TileEntityMagicChest chest) {
        super (chest);
        
        sizeX = 176;
        sizeY = 168;
    }

    @Override
    public Page setupPages () {
        return new PageInventoryTab (this, 0);
    }

    @Override
    public void drawForeground () {

    }
    
    @Override
    public boolean closeGui () {
        getMinecraft ().thePlayer.openGui (MagicChest.instance, 0, chest.worldObj, chest.xCoord, chest.yCoord, chest.zCoord);
        return true;
    }
    
}
