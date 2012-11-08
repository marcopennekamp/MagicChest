package MagicChest.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.client.gui.GuiFilteringItemBrowser;
import org.tilegames.mc.magicchest.client.gui.GuiMagicChest;

import cpw.mods.fml.common.network.IGuiHandler;

public class MagicChestGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity (x, y, z);
        if (tileEntity instanceof TileEntityMagicChest) {
            TileEntityMagicChest chest = (TileEntityMagicChest) tileEntity;
            if (id == 0) { /* Magic Chest Gui. */
                chest.openChest ();
                return new ContainerMagicChest (player.inventory, chest);
            }else if ((id & 0x000000FF) == 1) { /* Filtering Item Browser. */
                return null;
            }
        }
        return null;
    }


    @Override
    public Object getClientGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity (x, y, z);
        if (tileEntity instanceof TileEntityMagicChest){
            TileEntityMagicChest chest = (TileEntityMagicChest) tileEntity;
            if (id == 0) {
                chest.openChest ();
                return new GuiMagicChest (player.inventory, chest);
            }else if ((id & 0x000000FF) == 1) {
                return new GuiFilteringItemBrowser (chest, (id & 0x0000FF00) >> 8);
            }
        }
        return null;
    }
    
}
