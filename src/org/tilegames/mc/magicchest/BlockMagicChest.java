package org.tilegames.mc.magicchest;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import MagicChest.common.MagicChest;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;


public class BlockMagicChest extends BlockContainer {
    private static final float FREE_SPACE_SIDES = 15.0f / 16.0f; /* The free space on each chest side. */
    private static final float FREE_SPACE_TOP = 14.0f / 16.0f; /* The free space on the top side. */
    
    private int renderType;
    
    public BlockMagicChest (int id) {
        super (id, Material.wood);
        setCreativeTab (CreativeTabs.tabBlock);
        /* This fixes the super bad onCollision stuff. Basically the item has to be WITHIN the block, not just touch it. Hilarious, I mean it! */
        setBlockBounds (1.0f - FREE_SPACE_SIDES, 0.01f, 1.0f - FREE_SPACE_SIDES, FREE_SPACE_SIDES, FREE_SPACE_TOP, FREE_SPACE_SIDES);
    }
    
    
    @Override
    @SideOnly(Side.CLIENT)
    /* This declaration fixes the 0.01f offset of the fix above. So basically it required a fix for a fix, great API Mojang, great API. */
    public AxisAlignedBB getSelectedBoundingBoxFromPool (World world, int x, int y, int z) {
        return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool (x + 1.0f - FREE_SPACE_SIDES, y, z + 1.0f - FREE_SPACE_SIDES, x + FREE_SPACE_SIDES, y + FREE_SPACE_TOP, z + FREE_SPACE_SIDES);
    }
    
    
    /* Drops. */
    
    @Override
    public int quantityDropped (int meta, int fortune, Random random) {
        return 1;
    }
    
    @Override
    public int damageDropped (int metadata) {
        return (metadata & 0xC) >> 2;
    }
    
    
    /* Events. */
    
    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLiving entity) {
        /* Calculate placement angle. */
        int metadata = world.getBlockMetadata (x, y, z) | (int) ((entity.rotationYaw * 4.0f / 360.0f) + 0.5f) & 0x3;
        world.setBlockMetadataWithNotify (x, y, z, metadata);
    }
    
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        if (world.isRemote) return true;
        player.openGui (MagicChest.instance, 0, world, x, y, z);
        return true;
    }
    
    
    
    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity) {
        if (world.isRemote) return;
        if (entity.getClass ().isAssignableFrom (EntityItem.class)) {
            TileEntityMagicChest chest = (TileEntityMagicChest) world.getBlockTileEntity (x, y, z);
            chest.onCollisionWithItem ((EntityItem) entity);
        }
    }
    
    public void breakBlock (World world, int x, int y, int z, int par5, int par6) {
        TileEntityMagicChest entity = (TileEntityMagicChest) world.getBlockTileEntity (x, y, z);
        if (entity != null) {
            entity.dropItems ();
        }
        super.breakBlock (world, x, y, z, par5, par6);
    }

    
    /* Render Specific. */
    @Override
    public boolean isOpaqueCube () {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock () {
        return false;
    }

    public int getRenderType () {
        return renderType;
    }
    
    public void setRenderType (int type) {
        renderType = type;
    }
    
    
    /* For the particle textures. */
    @SideOnly(Side.CLIENT)
    public int getBlockTexture (IBlockAccess world, int x, int y, int z, int meta) {
        return 4;
    }

    public int getBlockTextureFromSide (int side) {
        return 4;
    }
    
    
    /* Miscellaneous Functions. */
    @Override
    public TileEntity createNewTileEntity (World world) {
        return new TileEntityMagicChest ();
    }
    
}
