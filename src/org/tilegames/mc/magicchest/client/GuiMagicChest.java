package org.tilegames.mc.magicchest.client;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;

@SideOnly(Side.CLIENT)
public class GuiMagicChest extends GuiContainer {
    TileEntityMagicChest chest;
    
    public GuiMagicChest (IInventory playerInventory, TileEntityMagicChest chestInventory) {
        super (new ContainerMagicChest (playerInventory, chestInventory));
        chest = chestInventory;
        ySize = 168;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int x, int y) {
        int texture = this.mc.renderEngine.getTexture("/MagicChest/ChestGui.png");
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture (texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

}
