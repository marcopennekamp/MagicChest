package org.tilegames.mc.magicchest.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.tilegames.mc.magicchest.TileEntityMagicChest;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Slot;
import net.minecraft.src.Tessellator;

@SideOnly (Side.CLIENT)
public abstract class GuiPage extends GuiScreen {
    public static RenderItem itemRenderer = new RenderItem ();
    
    /* x and y boundaries. */
    public int offsetX;
    public int offsetY;
    public int sizeX;
    public int sizeY;
    
    /* An instance of the RenderHelper. */
    public RenderHelper renderHelper;
    
    /* All page buttons. */
    public List<PageButton> pageButtons = new ArrayList<PageButton> ();
    
    /* The page that is currently rendered. */
    public Page page;
    
    /* All the pages. */
    public List<Page> pages = new ArrayList<Page> ();
    
    public TileEntityMagicChest chest;
    
    public class RenderHelper {
        
        public void bindAndDrawBackgroundTexture (String path) {
            /* Bind texture. */
            bindTexture (path);
            
            /* Draw. */
            GL11.glColor4f (1.0f, 1.0f, 1.0f, 1.0f);
            drawTexturedModalRect (0, 0, 0, 0, sizeX, sizeY);
        }
        
        public void bindTexture (String path) {
            getRenderEngine ().bindTexture (getRenderEngine ().getTexture ("/MagicChest/" + path));
        }
        
        public void drawHoverRectangle (int x, int y, int width, int height, int color) {
            GL11.glDisable (GL11.GL_LIGHTING);
            GL11.glDisable (GL11.GL_DEPTH_TEST);
            drawGradientRect (x, y, x + width, y + height, color, color);
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
        
        public void drawPageButton (int texture, int x, int y, int state) {
            double u = (double) (texture % 16) / 16.0;
            double v = Math.floor ((double) texture / 16.0) / 16.0;
            
            double z = zLevel;
            double uEnd = u + 16.0 / 256.0;
            double vEnd = v + 16.0 / 256.0;
            double xEnd = x + PageButton.WIDTH;
            double yEnd = y + PageButton.HEIGHT;
            
            Tessellator.instance.startDrawingQuads ();
            Tessellator.instance.addVertexWithUV (x, yEnd, z, u, vEnd);
            Tessellator.instance.addVertexWithUV (xEnd, yEnd, z, uEnd, vEnd);
            Tessellator.instance.addVertexWithUV (xEnd, y, z, uEnd, v);
            Tessellator.instance.addVertexWithUV (x, y, z, u, v);
            Tessellator.instance.draw ();
            
            if (state != PageButton.STATE_NORMAL) {
                int color;
                if (state == PageButton.STATE_HOVER) { /* HOVER */
                    color = 0x20FFFFFF;
                }else { /* ACTIVE */
                    color = 0x40FFFFFF;
                }
                
                drawHoverRectangle (x, y, PageButton.WIDTH, PageButton.HEIGHT, color);
            }
        }
        
        public void drawDevelopingBanner (int yOffset) {
            GL11.glColor4f (1.0f, 1.0f, 1.0f, 1.0f);
            bindTexture ("Developing.png");
            final int width = 110;
            final int height = 57;
            
            int x = (sizeX - width) / 2;
            int y = (sizeY - height) / 2 - 13 + yOffset; /* Subtract string height and padding. */
            drawTexturedModalRect (x, y, 0, 0, width, height);
            
            String string = "Developing!";
            fontRenderer.drawString (string, (sizeX - fontRenderer.getStringWidth (string)) / 2, y + height + 5, 0x404040);
        }
        
        public boolean pointInRectangle (int x, int y, int width, int height, int px, int py) {
            px -= offsetX;
            py -= offsetY;
            return px >= x - 1 && px < x + width + 1 && py >= y - 1 && py < y + height + 1;
        }
        
    }
    
    
    /* Registers a Page with its PageButton. */
    public void setupPage (Page page, int x, int y) {
        PageButton button = new PageButton (page, x, y);
        page.setButton (button);
        pages.add (page);
        pageButtons.add (button);
    }
    
    
    public GuiPage (TileEntityMagicChest chest) {
        renderHelper = new RenderHelper ();
        page = setupPages ();
        this.chest = chest;
        
    }
    
    
    @Override
    public void initGui () {
        super.initGui ();
        offsetX = (width - sizeX) / 2;
        offsetY = (height - sizeY) / 2;
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float par3) {
        // super.drawScreen (mouseX, mouseY, par3); /* Do NOT draw buttons the standard way! */
        
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
        
        
        /* Draw foreground. */
        GL11.glPushMatrix ();
        GL11.glTranslatef (0.0f, 0.0f, 100.0f);
        
        /* Draw icon buttons. */
        renderHelper.bindTexture ("Buttons.png");
        
        int size = pageButtons.size ();
        for (int i = 0; i < size; ++i) {
            PageButton button = pageButtons.get (i);
            renderHelper.drawPageButton (button.getTexture (), button.x, button.y, button.getState (page, mouseX - offsetX, mouseY - offsetY));
        }
        
        drawForeground ();
        
        /* Draw titles. */
        fontRenderer.drawString (page.getTitle (), 8, 6, 0x404040);
        
        GL11.glPopMatrix ();
        
        
        /* Render page. */
        page.draw (mouseX, mouseY);
        
        /* Rendering cleanup. */
        GL11.glPopMatrix();
        GL11.glEnable (GL11.GL_LIGHTING);
        GL11.glEnable (GL11.GL_DEPTH_TEST);
        net.minecraft.src.RenderHelper.enableStandardItemLighting ();
    }
    
    public abstract void drawForeground ();
    
    public Page getSelectedPage (PageButton button) { return null; }
    public abstract Page setupPages ();
    
    public boolean closeGui () { return false; }
    
    public void onPageButtonClick (PageButton button, int mouseButton) {
        boolean playSound = false;
        if (mouseButton == 0) {
            Page newPage = getSelectedPage (button);
            if (newPage != null) {
                changePage (newPage);
            }else {
                changePage (pages.get (button.getId () - Page.BASE_ID));
            }
            playSound = true;
        }else if (mouseButton == 1) {
            playSound = button.getPage ().onPageButtonRightClick ();
        }
        if (playSound) mc.sndManager.playSoundFX ("random.click", 1.0F, 1.0F);
    }
    
    @Override
    protected void mouseClicked (int x, int y, int mouseButton) {
        super.mouseClicked (x, y, mouseButton);
        
        /* Check icon buttons. */
        int size = pageButtons.size ();
        for (int i = 0; i < size; ++i) {
            PageButton button = pageButtons.get (i);
            if (button.inBounds (x - offsetX, y - offsetY)) {
                onPageButtonClick (button, mouseButton);
                return;
            }
        }
        
        page.onClick (x, y, mouseButton);
    }
    
    @Override
    protected void keyTyped (char character, int key) {
        if (key == 1 || key == mc.gameSettings.keyBindInventory.keyCode) { /* Escape closes page or UI depending on page. */
            if (!closeGui ()) mc.thePlayer.closeScreen ();
        }else {
            page.onKeyType (character, key);
        }
    }
    
    public void changePage (Page newPage) {
        if (page != null) page.onPageClose ();
        page = newPage;
        page.onPageOpen ();
    }
    
    
    @Override
    public void onGuiClosed () {
        page.onPageClose ();
    }
    

    @Override
    public boolean doesGuiPauseGame () {
        return false;
    }

    @Override
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
