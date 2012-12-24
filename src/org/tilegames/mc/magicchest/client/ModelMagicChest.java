package org.tilegames.mc.magicchest.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelMagicChest extends ModelBase {
    public ModelRenderer lid;
    public ModelRenderer knob;
    public ModelRenderer body;
    
    public ModelMagicChest () {
        /* Shamelessly copied from ModelChest. :D */
        
        lid = new ModelRenderer (this, 0, 0);
        lid.setTextureSize (64, 64);
        lid.addBox (0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
        lid.rotationPointX = 1.0F;
        lid.rotationPointY = 7.0F;
        lid.rotationPointZ = 15.0F;
        
        knob = new ModelRenderer (this, 0, 0);
        knob.setTextureSize (64, 64);
        knob.addBox (-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
        knob.rotationPointX = 8.0F;
        knob.rotationPointY = 7.0F;
        knob.rotationPointZ = 15.0F;
        
        body = new ModelRenderer(this, 0, 19);
        body.setTextureSize (64, 64);
        body.addBox (0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        body.rotationPointX = 1.0F;
        body.rotationPointY = 6.0F;
        body.rotationPointZ = 1.0F;
    }

    public void renderChest () {
        knob.rotateAngleX = lid.rotateAngleX;
        lid.render (0.0625f);
        knob.render (0.0625f);
        body.render (0.0625f);
    }
    
}
