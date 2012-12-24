package org.tilegames.mc.magicchest.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class MagicChestRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    private static final String[] textures = {"Oak", "Spruce", "Birch", "Jungle"};
    
    private int renderId;
    private ModelMagicChest model;

    public MagicChestRenderer (int renderId) {
        this.renderId = renderId;
        this.model = new ModelMagicChest ();
    }
    
    
    @Override
    public void renderTileEntityAt (TileEntity entity, double x, double y, double z, float partialTickTime) {
        renderTileEntityMagicChest ((TileEntityMagicChest) entity, x, y, z, partialTickTime);
    }
    
    public void renderTileEntityMagicChest (TileEntityMagicChest entity, double x, double y, double z, float partialTickTime) {
        /* Get chest rotation in degrees. */
        int rotation = 0;
        int textureId = 0;
        if (entity != null) {
            if (entity.worldObj != null) {
                int metadata = entity.getBlockMetadata ();
                rotation = (metadata & 0x3) * 90 - 180; /* 0 -> -180°, 1 -> -90°, 2 -> 0°, 3 -> 90° */
                textureId = (metadata & 0xC) >> 2;
            }
        }
        
        /* Set angle. */
        float angle = entity.previousLidAngle + ((entity.lidAngle - entity.previousLidAngle) * partialTickTime); /* Okay this is silly Minecraft... */
        angle = 1.0F - angle;
        angle = 1.0F - angle * angle * angle;
        model.lid.rotateAngleX = -(angle * (float) Math.PI / 2.0F);
    
        render ((float) x, (float) y, (float) z, rotation, textureId);
    }
    
    public void render (float x, float y, float z, int rotation, int textureId) {
        /* Bind texture. */
        bindTextureByName ("/MagicChest/Model/Chest/" + textures[textureId] + ".png");
        
        /* Setup matrices. */
        GL11.glPushMatrix ();
        GL11.glEnable (GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f (1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef (x, y + 1.0f, z + 1.0f);
        GL11.glScalef (1.0f, -1.0f, -1.0f);
        GL11.glTranslatef (0.5f, 0.5f, 0.5f);
        GL11.glRotatef (rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        /* Render. */
        model.renderChest ();
        
        /* Cleanup. */
        GL11.glDisable (GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix ();
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
    }
    

    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer) {
        /* "metadata" is actually item damage here. */
        
        GL11.glRotatef (90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef (-0.5f, -0.5f, -0.5f);
        model.lid.rotateAngleX = 0.0f;
        render (0.0f, 0.0f, 0.0f, 0, metadata & 0x3);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    public boolean shouldRender3DInInventory () {
        return true;
    }

    public int getRenderId () {
        return renderId;
    }
    
}
