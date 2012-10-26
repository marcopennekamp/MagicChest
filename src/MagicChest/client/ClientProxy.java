package MagicChest.client;

import net.minecraftforge.client.MinecraftForgeClient;

import org.tilegames.mc.magicchest.BlockMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.client.MagicChestRenderer;

import MagicChest.common.CommonProxy;
import MagicChest.common.MagicChest;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;


public class ClientProxy extends CommonProxy {

    @Override
    public void registerGraphics (BlockMagicChest block) {
        MinecraftForgeClient.preloadTexture (MagicChest.TEXTURE_ATLAS);
        
        /* Give Magic Chest a custom block renderer! */
        int renderId = RenderingRegistry.getNextAvailableRenderId ();
        MagicChestRenderer renderer = new MagicChestRenderer (renderId);
        ClientRegistry.bindTileEntitySpecialRenderer (TileEntityMagicChest.class, renderer);
        RenderingRegistry.registerBlockHandler (renderId, renderer);
        block.setRenderType (renderId);
    }
    
}
