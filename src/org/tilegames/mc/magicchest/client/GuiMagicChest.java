package org.tilegames.mc.magicchest.client;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.ContainerMagicChest;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;


public class GuiMagicChest extends GuiContainer {

    public GuiMagicChest (IInventory playerInventory, IInventory chestInventory) {
        super (new ContainerMagicChest (playerInventory, chestInventory));
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
