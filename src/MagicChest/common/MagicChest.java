/*
 *  Magic Chest v1.0
 * 
 *  copyright 2012 Marco "Aero96" Pennekamp. All rights reserved.
 * 
 *  Contact:
 *      E-Mail:     marco.pennekamp@unitybox.de
 *      Internet:   http://tilegames.org/
 *      Twitter:    @marcopennekamp
 * 
 */

package MagicChest.common;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import org.tilegames.mc.magicchest.BlockMagicChest;
import org.tilegames.mc.magicchest.ItemBlockMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import MagicChest.client.ClientProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod (modid = "Tilegames_MagicChest", name = "MagicChest", version = "1.0 alpha")
@NetworkMod (clientSideRequired = true, serverSideRequired = false)
public class MagicChest {
    
    public static final String TEXTURE_ATLAS = "/MagicChest/Atlas.png";
    
    public static MagicChest instance;
    
    
    @SidedProxy (clientSide = "MagicChest.client.ClientProxy", serverSide = "MagicChest.common.CommonProxy")
    public static ClientProxy proxy;
    
    
    public BlockMagicChest blockMagicChest;
    

    @Init
    public void load (FMLInitializationEvent event) {
        instance = this;
        
        blockMagicChest = new BlockMagicChest (2000);
        blockMagicChest.setBlockName ("magicchest");
        blockMagicChest.setStepSound (Block.soundWoodFootstep);
        blockMagicChest.setHardness (1.0f);
        blockMagicChest.setResistance (1.0f);
        
        GameRegistry.addRecipe (new ItemStack (blockMagicChest, 1, 0), new Object[] {
            "000", "R R", "000", '0', new ItemStack (Block.planks, 1, 0), 'R', Item.redstone
        });
        GameRegistry.addRecipe (new ItemStack (blockMagicChest, 1, 1), new Object[] {
            "000", "R R", "000", '0', new ItemStack (Block.planks, 1, 1), 'R', Item.redstone
        });
        GameRegistry.addRecipe (new ItemStack (blockMagicChest, 1, 2), new Object[] {
            "000", "R R", "000", '0', new ItemStack (Block.planks, 1, 2), 'R', Item.redstone
        });
        GameRegistry.addRecipe (new ItemStack (blockMagicChest, 1, 3), new Object[] {
            "000", "R R", "000", '0', new ItemStack (Block.planks, 1, 3), 'R', Item.redstone
        });
        
        GameRegistry.registerBlock (blockMagicChest, ItemBlockMagicChest.class);
        GameRegistry.registerTileEntity (TileEntityMagicChest.class, "MagicChest");
        
        LanguageRegistry.addName (blockMagicChest, "Magic Chest");
        
        NetworkRegistry.instance ().registerGuiHandler (this, new MagicChestGuiHandler ());
        
        proxy.registerGraphics (blockMagicChest);
    }
    
}
