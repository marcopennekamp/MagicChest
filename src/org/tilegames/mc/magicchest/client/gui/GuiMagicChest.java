package org.tilegames.mc.magicchest.client.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Slot;
import net.minecraft.src.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.tilegames.mc.magicchest.ContainerMagicChest;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/* Note:
 *  To make the GUI more customizable, I unfortunately had to copy (and improve 
 *  of course) the rendering code of GuiContainer. 
 */

@SideOnly(Side.CLIENT)
public class GuiMagicChest extends GuiScreen {
    
    public static RenderItem itemRenderer = new RenderItem ();
    
    public static final int SIZE_Y = 168;
    public static final int SIZE_X = 176;
    
    public class RenderHelper {
        
        public void bindAndDrawBackgroundTexture (String path) {
            /* Bind texture. */
            bindTexture (path);
            
            /* Draw. */
            GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect (0, 0, 0, 0, SIZE_X, SIZE_Y);
        }
        
        public void bindTexture (String path) {
            getRenderEngine ().bindTexture (getRenderEngine ().getTexture (path));
        }
        
        public void drawHoverRectangle (int x, int y, int width, int height) {
            GL11.glDisable (GL11.GL_LIGHTING);
            GL11.glDisable (GL11.GL_DEPTH_TEST);
            drawGradientRect (x, y, x + width, y + height, 0x80FFFFFF, 0x80FFFFFF);
            GL11.glEnable (GL11.GL_LIGHTING);
            GL11.glEnable (GL11.GL_DEPTH_TEST);
        }
        
        public void drawItemStack (ItemStack stack, int x, int y) {
            RenderEngine renderEngine = getRenderEngine ();
            
            GL11.glEnable (GL11.GL_DEPTH_TEST);
            itemRenderer.func_82406_b (fontRenderer, renderEngine, stack, x, y);
            itemRenderer.renderItemOverlayIntoGUI (fontRenderer, renderEngine, stack, x, y);
        }
        
        public void drawSlot (Slot slot) {
            int x = slot.xDisplayPosition;
            int y = slot.yDisplayPosition;
            ItemStack stack = slot.getStack();
            
            setZLevel (100.0f);

            if (stack == null) {
                int backgroundIconIndex = slot.getBackgroundIconIndex ();
                if (backgroundIconIndex >= 0) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    mc.renderEngine.bindTexture (mc.renderEngine.getTexture ("/gui/items.png"));
                    drawTexturedModalRect (x, y, backgroundIconIndex % 16 * 16, backgroundIconIndex / 16 * 16, 16, 16);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }else { /* Render stack. */
                drawItemStack (stack, x, y);
            }

            setZLevel (0.0f);
        }
        
        public void drawTooltip (ItemStack stack, int x, int y) {
            GL11.glDisable (GL12.GL_RESCALE_NORMAL);
            net.minecraft.src.RenderHelper.disableStandardItemLighting ();
            GL11.glDisable (GL11.GL_LIGHTING);
            GL11.glDisable (GL11.GL_DEPTH_TEST);
            
            @SuppressWarnings("rawtypes")
            List strings = stack.func_82840_a (mc.thePlayer, mc.gameSettings.field_82882_x);

            if (!strings.isEmpty ()) {
                int size = strings.size();
                int tooltipWidth = 0;
                @SuppressWarnings("rawtypes")
                Iterator iterator = strings.iterator();

                /* Get tooltip width by maximum string width. */
                while (iterator.hasNext ()) {
                    int stringWidth = fontRenderer.getStringWidth ((String) iterator.next ());
                    if (stringWidth > tooltipWidth) tooltipWidth = stringWidth;
                }

                /* Calculate height. */
                int height = 8;
                if (size > 1) height += 2 + (size - 1) * 10;
                
                /* Bounds. */
                int minX = x + 12;
                int minY = y - 12;
                int maxX = minX + tooltipWidth;
                int maxY = minY + height;

                /* Render background.*/
                setZLevel (300.0f);
                
                final int color = 0xF0100010;
                drawGradientRect (minX - 3, minY - 4, maxX + 3, minY - 3, color, color);
                drawGradientRect (minX - 3, maxY + 3, maxX + 3, maxY + 4, color, color);
                drawGradientRect (minX - 3, minY - 3, maxX + 3, maxY + 3, color, color);
                drawGradientRect (minX - 4, minY - 3, minX - 3, maxY + 3, color, color);
                drawGradientRect (maxX + 3, minY - 3, maxX + 4, maxY + 3, color, color);
                
                final int color1 = 0x505000FF;
                final int color2 = (color1 & 0xFEFEFE) >> 1 | color1 & 0xFF000000;
                drawGradientRect (minX - 3, minY - 2, minX - 2, maxY + 2, color1, color2);
                drawGradientRect (maxX + 2, minY - 2, maxX + 3, maxY + 2, color1, color2);
                drawGradientRect (minX - 3, minY - 3, maxX + 3, minY - 2, color1, color1);
                drawGradientRect (minX - 3, maxY + 2, maxX + 3, maxY + 3, color2, color2);

                /* Render strings. */
                for (int i = 0; i < size; ++i) {
                    String string = (String) strings.get (i);

                    if (i == 0) string = "\u00a7" + Integer.toHexString (stack.getRarity ().rarityColor) + string;
                    else string = "\u00a77" + string;

                    fontRenderer.drawStringWithShadow (string, minX, minY, -1);

                    if (i == 0) minY += 2;
                    minY += 10;
                }

                setZLevel (0.0f);
            }
        }
        
        public void drawIconButton (int id, double x, double y) {
            double u = Math.floor (id / 16.0) * 16.0;
            double v = id % 16 * 16.0;
            double z = zLevel;
            double uEnd = u + 16.0 / 256.0;
            double vEnd = v + 16.0 / 256.0;
            double xEnd = x + 8.0;
            double yEnd = y + 8.0;
            
            Tessellator.instance.startDrawingQuads ();
            Tessellator.instance.addVertexWithUV (x, yEnd, z, u, vEnd);
            Tessellator.instance.addVertexWithUV (xEnd, yEnd, z, uEnd, vEnd);
            Tessellator.instance.addVertexWithUV (xEnd, y, z, uEnd, v);
            Tessellator.instance.addVertexWithUV (x, y, z, u, v);
            Tessellator.instance.draw ();
        }
        
        
        public boolean pointInRectangle (int x, int y, int width, int height, int px, int py) {
            px -= offsetX;
            py -= offsetY;
            return px >= x - 1 && px < x + width + 1 && py >= y - 1 && py < y + height + 1;
        }
        
    }
    
    
    public ContainerMagicChest container;
    //private TileEntityMagicChest chest;
    
    
    
    /* The page that is currently rendered. */
    private Page page;
    
    
    /**
     * Starting X and Y position for the GUI.
     */
    protected int offsetX;
    protected int offsetY;
    
    public RenderHelper renderHelper;
    
    
    public GuiMagicChest (IInventory playerInventory, TileEntityMagicChest chestInventory) {
        container = new ContainerMagicChest (playerInventory, chestInventory);
        //chest = chestInventory;
    }
    
    
    public void initGui () {
        super.initGui ();
        mc.thePlayer.craftingInventory = container;
        
        offsetX = (width - SIZE_X) / 2;
        offsetY = (height - SIZE_Y) / 2;
        
        renderHelper = new RenderHelper ();
        page = new PageInventory (this);
    }
    
    
    public void drawScreen (int mouseX, int mouseY, float par3) {
        // super.drawScreen (par1, par2, par3); /* Do NOT draw buttons the standard way! */
        
        /* Draw default background. */
        drawDefaultBackground ();
        
        /* Prepare rendering. */
        net.minecraft.src.RenderHelper.disableStandardItemLighting ();
        GL11.glDisable (GL11.GL_LIGHTING);
        // GL11.glDisable (GL11.GL_DEPTH_TEST);
        
        net.minecraft.src.RenderHelper.enableGUIStandardItemLighting ();
        GL11.glEnable (GL12.GL_RESCALE_NORMAL);
        
        GL11.glPushMatrix ();
        GL11.glTranslatef (offsetX, offsetY, 0.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        
        /* Draw buttons. */
        renderHelper.bindTexture ("/MagicChest/Buttons.png");
        
        setZLevel (100.0f);
        renderHelper.drawIconButton (0, 135.0, 4.0);
        setZLevel (0.0f);
        
        /* Render page. */
        page.draw (mouseX, mouseY);
        
        /* Rendering cleanup. */
        GL11.glPopMatrix();
        GL11.glEnable (GL11.GL_LIGHTING);
        GL11.glEnable (GL11.GL_DEPTH_TEST);
        net.minecraft.src.RenderHelper.enableStandardItemLighting ();
    }

    @Override
    protected void mouseClicked (int x, int y, int button) {
        super.mouseClicked (x, y, button);
        page.onClick (x, y, button);
    }
    
    @Override
    protected void keyTyped (char character, int key) {
        if (key == 1 || key == mc.gameSettings.keyBindInventory.keyCode) { /* Escape closes page or UI depending on page. */
            if (page instanceof PageInventory) {
                mc.thePlayer.closeScreen ();
            }else {
                page = new PageInventory (this);
            }
        }else {
            page.onKeyType (character, key);
        }
    }
    

    public void onGuiClosed () {
        if (mc.thePlayer != null) container.onCraftGuiClosed (mc.thePlayer);
    }

    public boolean doesGuiPauseGame () {
        return false;
    }

    public void updateScreen () {
        super.updateScreen ();
        if (!mc.thePlayer.isEntityAlive () || mc.thePlayer.isDead) mc.thePlayer.closeScreen();
    }
    
    
    public Minecraft getMinecraft () {
        return mc;
    }
    
    public RenderEngine getRenderEngine () {
        return mc.renderEngine;
    }
    
    public InventoryPlayer getInventoryPlayer () {
        return mc.thePlayer.inventory;
    }
    
    public FontRenderer getFontRenderer () {
        return fontRenderer;
    }
    
    public float getZLevel () {
        return zLevel;
    }
    
    public void setZLevel (float zLevel) {
        itemRenderer.zLevel = zLevel;
        this.zLevel = zLevel;
    }
    
}
