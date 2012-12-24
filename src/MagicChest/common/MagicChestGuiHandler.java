package MagicChest.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.tilegames.mc.magicchest.ContainerFilteringItemBrowser;
import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.client.gui.GuiFilteringItemBrowser;
import org.tilegames.mc.magicchest.client.gui.GuiMagicChest;

import cpw.mods.fml.common.network.IGuiHandler;

public class MagicChestGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement (int data, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity (x, y, z);
        if (tileEntity instanceof TileEntityMagicChest) {
            TileEntityMagicChest chest = (TileEntityMagicChest) tileEntity;
            int id = data & 0xFF;
        	// int param = (data >> 8) & 0xFF;
            if (id == GuiMagicChest.ID) {
                chest.openChest ();
                return new ContainerMagicChest (player.inventory, chest);
            }else if (id == GuiFilteringItemBrowser.ID) {
                return new ContainerFilteringItemBrowser ();
            }
        }
        return null;
    }


    @Override
    public Object getClientGuiElement (int data, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity (x, y, z);
        if (tileEntity instanceof TileEntityMagicChest){
            TileEntityMagicChest chest = (TileEntityMagicChest) tileEntity;
            int id = data & 0xFF;
        	int param = (data >> 8) & 0xFF;
            if (id == GuiMagicChest.ID) {
                chest.openChest ();
                return new GuiMagicChest (player.inventory, chest, param);
            }else if (id == GuiFilteringItemBrowser.ID) {
                return new GuiFilteringItemBrowser (chest, param);
            }
        }
        return null;
    }
    
}
