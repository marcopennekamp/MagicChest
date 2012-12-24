package MagicChest.server;

import org.tilegames.mc.magicchest.BlockMagicChest;

import MagicChest.common.CommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {

    @Override
    public void registerGraphics (BlockMagicChest block) {
        
    }
    
}
