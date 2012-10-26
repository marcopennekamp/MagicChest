package org.tilegames.mc.magicchest.client;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Tessellator;

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
    
    @Override
    protected void drawGuiContainerForegroundLayer () {
        int texture = this.mc.renderEngine.getTexture("/MagicChest/Buttons.png");
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture (texture);
        
        float u = 0.0f;
        float v = 0.0f;
        float z = zLevel;
        float uEnd = 32.0f / 256.0f;
        float vEnd = 32.0f / 256.0f;
        float x = guiLeft / 256.0f + 40.0f / 256.0f;
        float y = guiTop / 256.0f + 5.0f / 256.0f;
        float xEnd = 8.0f / 256.0f;
        float yEnd = 8.0f / 256.0f;
        
        Tessellator.instance.startDrawingQuads ();
        Tessellator.instance.addVertexWithUV (x, yEnd, z, u, vEnd);
        Tessellator.instance.addVertexWithUV (xEnd, yEnd, z, uEnd, vEnd);
        Tessellator.instance.addVertexWithUV (xEnd, y, z, uEnd, v);
        Tessellator.instance.addVertexWithUV (x, y, z, u, vEnd);
        Tessellator.instance.draw ();
    }

}
