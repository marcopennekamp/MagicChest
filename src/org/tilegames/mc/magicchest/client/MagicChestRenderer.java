package org.tilegames.mc.magicchest.client;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;


@SideOnly(Side.CLIENT)
public class MagicChestRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    private int renderId;
    private ModelMagicChest model;

    public MagicChestRenderer (int renderId) {
        this.renderId = renderId;
        this.model = new ModelMagicChest ();
    }
    
    
    @Override
    public void renderTileEntityAt (TileEntity _entity, double _x, double _y, double _z, float partialTickTime) {
        TileEntityMagicChest entity = null;
        if (_entity != null) entity = (TileEntityMagicChest) _entity;
        float x = (float) _x;
        float y = (float) _y;
        float z = (float) _z;

        /* Get chest rotation in degrees. */
        int rotation = 0;
        if (entity != null) {
            if (entity.worldObj != null) {
                rotation = entity.getBlockMetadata () * 90 - 180; /* 0 -> -180°, 1 -> -90°, 2 -> 0°, 3 -> 90° */
            }
        }
        
        /* Bind texture. */
        bindTextureByName ("/MagicChest/MagicChest.png");
        
        /* Setup matrices. */
        GL11.glPushMatrix ();
        GL11.glEnable (GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f (1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef (x, y + 1.0f, z + 1.0f);
        GL11.glScalef (1.0f, -1.0f, -1.0f);
        GL11.glTranslatef (0.5f, 0.5f, 0.5f);
        GL11.glRotatef (rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        /* Set angle. */
        if (entity != null) {
            float angle = entity.previousLidAngle + ((entity.lidAngle - entity.previousLidAngle) * partialTickTime); /* Okay this is silly Minecraft... */
            angle = 1.0F - angle;
            angle = 1.0F - angle * angle * angle;
            model.lid.rotateAngleX = -(angle * (float) Math.PI / 2.0F);
        }else {
            model.lid.rotateAngleX = 0.0f;
        }
        
        /* Render. */
        model.renderChest ();
        
        /* Cleanup. */
        GL11.glDisable (GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix ();
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glRotatef (90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef (-0.5F, -0.5F, -0.5F);
        renderTileEntityAt (null, 0, 0, 0, 0.0f);
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
