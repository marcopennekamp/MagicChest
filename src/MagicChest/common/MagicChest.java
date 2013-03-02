/*
 *  Magic Chest v1.0
 * 
 *  copyright 2012 Marco "Aero96" Pennekamp. All rights reserved.
 * 
 *  Contact:
 *      E-Mail:     marco.pennekamp96@gmail.com
 *      Internet:   http://tilegames.org/
 *      Twitter:    @marcopennekamp
 * 
 */

package MagicChest.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.tilegames.mc.magicchest.BlockMagicChest;
import org.tilegames.mc.magicchest.ItemBlockMagicChest;
import org.tilegames.mc.magicchest.ItemUpgrade;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.network.PacketHandler;
import org.tilegames.mc.magicchest.upgrade.UpgradeVortexDevice;

import MagicChest.client.ClientProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod (modid = "Tilegames_MagicChest", name = "MagicChest", version = "1.0.2 alpha")
@NetworkMod (clientSideRequired = true, serverSideRequired = false)
public class MagicChest {
    
    public static final String TEXTURE_ATLAS = "/MagicChest/Atlas.png";
    
    public static MagicChest instance;
    
    
    @SidedProxy (clientSide = "MagicChest.client.ClientProxy", serverSide = "MagicChest.common.CommonProxy")
    public static ClientProxy proxy;
    
    
    public BlockMagicChest blockMagicChest;
    
    public ItemUpgrade upgradeVortexDevice;
    
    @PreInit
    public void preInit (FMLPreInitializationEvent event) {
        /* Apply configuration file. */
        Configuration config = new Configuration (event.getSuggestedConfigurationFile ());
        config.load ();
        UpgradeVortexDevice.pullDistance = config.get (Configuration.CATEGORY_GENERAL, "VortexDevice_PullDistance", 4.0).getDouble (4.0);
        config.save ();
    }

    @Init
    public void load (FMLInitializationEvent event) {
        instance = this;
        
        /* Magic Chest Block. */
        blockMagicChest = new BlockMagicChest (2000);
        blockMagicChest.setBlockName ("magicchest");
        blockMagicChest.setStepSound (Block.soundWoodFootstep);
        blockMagicChest.setHardness (1.0f);
        blockMagicChest.setResistance (1.0f);
        
        /* Upgrades. */
        upgradeVortexDevice = new ItemUpgrade (16660, UpgradeVortexDevice.class);
        upgradeVortexDevice.setItemName ("magicchest.upgrade.vortexdevice");
        
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
        
        GameRegistry.registerBlock (blockMagicChest, ItemBlockMagicChest.class, "magicchest");
        GameRegistry.registerTileEntity (TileEntityMagicChest.class, "MagicChest");
        
        LanguageRegistry.addName (blockMagicChest, "Magic Chest");
        LanguageRegistry.addName (upgradeVortexDevice, "Vortex Device");
        
        NetworkRegistry.instance ().registerGuiHandler (this, new MagicChestGuiHandler ());
        NetworkRegistry.instance ().registerChannel (new PacketHandler (), "magicchest");
        
        proxy.registerGraphics (blockMagicChest);
    }
}
