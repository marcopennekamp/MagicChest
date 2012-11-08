package org.tilegames.mc.magicchest.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import org.lwjgl.input.Mouse;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import MagicChest.common.MagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly (Side.CLIENT)
public class GuiFilteringItemBrowser extends GuiPage {
	public static List<ItemStack> items;
	
	private float scroll = 0.0f;
	public int rowCount = 0;
	public int row = 0;
	
	public int filteringSlot;
	
    public GuiFilteringItemBrowser (TileEntityMagicChest chest, int filteringSlot) {
        super (chest);
        
        this.filteringSlot = filteringSlot;
        
        /* Create the displayed item list. */
        if (items == null) {
        	items = new ArrayList<ItemStack> (256);
        	Item[] itemList = Item.itemsList;
    		for (int i = 0; i < itemList.length; ++i) {
    			Item item = itemList[i];
    			if (item != null) {
    				if (item.getCreativeTab () != null)
    					item.getSubItems (item.shiftedIndex, CreativeTabs.tabAllSearch, items);
    			}
    		}
    		rowCount = (int) Math.ceil (items.size () / 9.0f);
    	}
        
        sizeX = 194;
        sizeY = 168; /* Image is 132px. */
    }

    @Override
    public Page setupPages () {
        return new PageInventoryTab (this, 0);
    }

    @Override
    public void drawForeground () {

    }
    
    @Override
    public void handleMouseInput () {
        super.handleMouseInput ();
        
        int wheel = Mouse.getEventDWheel ();
        if (wheel != 0 && rowCount > 6) {
        	int scrollStages = rowCount - 6;
        	double direction = (wheel > 0.0f) ? 1.0 : -1.0;
        	scroll -= direction / scrollStages;
        	if (scroll > 1.0f) scroll = 1.0f;
        	else if (scroll < 0.0f) scroll = 0.0f;
        	row = (int) (scroll * rowCount);
        }
    }
    
    @Override
    public boolean closeGui () {
        getMinecraft ().thePlayer.openGui (MagicChest.instance, 0, chest.worldObj, chest.xCoord, chest.yCoord, chest.zCoord);
        return true;
    }
    
}
