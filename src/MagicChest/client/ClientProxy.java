package MagicChest.client;

import org.tilegames.mc.magicchest.BlockMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.client.MagicChestRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import MagicChest.common.CommonProxy;
import MagicChest.common.MagicChest;


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
