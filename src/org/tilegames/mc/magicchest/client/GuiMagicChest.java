package org.tilegames.mc.magicchest.client;

import java.util.Iterator;
import java.util.List;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Slot;
import net.minecraft.src.Tessellator;

import org.lwjgl.input.Keyboard;
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
    
    private static RenderItem itemRenderer = new RenderItem();
    
    public static final int SIZE_Y = 168;
    public static final int SIZE_X = 176;
    
    
    private ContainerMagicChest container;
    //private TileEntityMagicChest chest;
    
    
    /**
     * Starting X and Y position for the GUI.
     */
    protected int offsetX;
    protected int offsetY;
    
    private Slot hoveredSlot; /* whatever... */
    
    
    
    
    private void drawQuickButton (int id, double x, double y) {
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
    
    
    public GuiMagicChest (IInventory playerInventory, TileEntityMagicChest chestInventory) {
        container = new ContainerMagicChest (playerInventory, chestInventory);
        //chest = chestInventory;

    }
    
    
    public void initGui () {
        super.initGui ();
        mc.thePlayer.craftingInventory = container;
        
        offsetX = (width - SIZE_X) / 2;
        offsetY = (height - SIZE_Y) / 2;
    }
    
    
    public void drawScreen (int mouseX, int mouseY, float par3) {
        
        
        
        /* Draw Background. */
        drawDefaultBackground ();
        
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture (mc.renderEngine.getTexture ("/MagicChest/ChestGui.png"));
        drawTexturedModalRect (offsetX, offsetY, 0, 0, SIZE_X, SIZE_Y);
        
        
        // super.drawScreen (par1, par2, par3); /* Do NOT draw buttons the standard way! */
        
        
        
        
        /* Enable _GUI_ item lighting. */
        RenderHelper.disableStandardItemLighting ();
        GL11.glDisable (GL11.GL_LIGHTING);
        GL11.glDisable (GL11.GL_DEPTH_TEST);
        
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable (GL12.GL_RESCALE_NORMAL);
        
        /* Draw slots. */
        GL11.glPushMatrix ();
        GL11.glTranslatef (offsetX, offsetY, 0.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        
        this.hoveredSlot = null;
        
        
        // ????
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords (OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);

        
        int size = container.inventorySlots.size ();
        for (int i = 0; i < size; ++i) {
            Slot slot = (Slot) container.inventorySlots.get (i);
            drawSlot (slot);
            
            /* Check hover status. */
            if (isMouseOverSlot (slot, mouseX, mouseY)) {
                hoveredSlot = slot;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                int x = slot.xDisplayPosition;
                int y = slot.yDisplayPosition;
                drawGradientRect (x, y, x + 16, y + 16, 0x80FFFFFF, 0x80FFFFFF);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            
            
        }
        
        /* Draw buttons. */
        GL11.glColor4f (1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture (mc.renderEngine.getTexture ("/MagicChest/Buttons.png"));
        
        drawQuickButton (0, 135.0, 4.0);
        
        /* Render item taken by player. */
        InventoryPlayer playerInventory = mc.thePlayer.inventory;

        if (playerInventory.getItemStack () != null) {
            GL11.glTranslatef (0.0F, 0.0F, 32.0F);
            zLevel = itemRenderer.zLevel = 200.0F;
            itemRenderer.func_82406_b (fontRenderer, mc.renderEngine, playerInventory.getItemStack(), mouseX - offsetX - 8, mouseY - offsetY - 8);
            itemRenderer.renderItemOverlayIntoGUI (fontRenderer, mc.renderEngine, playerInventory.getItemStack(), mouseX - offsetX - 8, mouseY - offsetY - 8);
            zLevel = itemRenderer.zLevel = 0.0F;
        }
        /* Draw tooltip. */
        else if (hoveredSlot != null && hoveredSlot.getHasStack ()) { 
            drawTooltip (hoveredSlot.getStack(), mouseX - offsetX, mouseY - offsetY);
        }
        
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }
    
    private void drawTooltip (ItemStack stack, int x, int y) {
        GL11.glDisable (GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
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
            this.zLevel = itemRenderer.zLevel = 300.0F;
            
            final int color = 0xF0100010;
            this.drawGradientRect (minX - 3, minY - 4, maxX + 3, minY - 3, color, color);
            this.drawGradientRect (minX - 3, maxY + 3, maxX + 3, maxY + 4, color, color);
            this.drawGradientRect (minX - 3, minY - 3, maxX + 3, maxY + 3, color, color);
            this.drawGradientRect (minX - 4, minY - 3, minX - 3, maxY + 3, color, color);
            this.drawGradientRect (maxX + 3, minY - 3, maxX + 4, maxY + 3, color, color);
            
            final int color1 = 0x505000FF;
            final int color2 = (color1 & 0xFEFEFE) >> 1 | color1 & 0xFF000000;
            this.drawGradientRect (minX - 3, minY - 2, minX - 2, maxY + 2, color1, color2);
            this.drawGradientRect (maxX + 2, minY - 2, maxX + 3, maxY + 2, color1, color2);
            this.drawGradientRect (minX - 3, minY - 3, maxX + 3, minY - 2, color1, color1);
            this.drawGradientRect (minX - 3, maxY + 2, maxX + 3, maxY + 3, color2, color2);

            /* Render strings. */
            for (int i = 0; i < size; ++i) {
                String string = (String) strings.get (i);

                if (i == 0) string = "\u00a7" + Integer.toHexString (stack.getRarity ().rarityColor) + string;
                else string = "\u00a77" + string;

                fontRenderer.drawStringWithShadow (string, minX, minY, -1);

                if (i == 0) minY += 2;
                minY += 10;
            }

            zLevel = itemRenderer.zLevel = 0.0F;
        }
    }


    private void drawSlot (Slot slot) {
        int x = slot.xDisplayPosition;
        int y = slot.yDisplayPosition;
        ItemStack stack = slot.getStack();
        
        zLevel = itemRenderer.zLevel = 100.0F;

        if (stack == null) {
            int backgroundIconIndex = slot.getBackgroundIconIndex ();
            if (backgroundIconIndex >= 0) {
                GL11.glDisable(GL11.GL_LIGHTING);
                mc.renderEngine.bindTexture (mc.renderEngine.getTexture ("/gui/items.png")); /* "MC... Whatcha doin'? MINECRAAFT! STAAHP!" - performance */
                drawTexturedModalRect (x, y, backgroundIconIndex % 16 * 16, backgroundIconIndex / 16 * 16, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }else { /* Render stack. */
            GL11.glEnable (GL11.GL_DEPTH_TEST);
            itemRenderer.func_82406_b (fontRenderer, mc.renderEngine, stack, x, y);
            itemRenderer.renderItemOverlayIntoGUI (fontRenderer, this.mc.renderEngine, stack, x, y);
        }

        itemRenderer.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }


    private boolean isMouseOverSlot (Slot slot, int x, int y) {
        return isInBounds (slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x, y);
    }
    
    protected boolean isInBounds (int x, int y, int width, int height, int testX, int testY) {
        testX -= offsetX;
        testY -= offsetY;
        return testX >= x - 1 && testX < x + width + 1 && testY >= y - 1 && testY < y + height + 1;
    }
    
    private Slot getSlotAtPosition (int x, int y) {
        int size = container.inventorySlots.size ();
        for (int i = 0; i < size; ++i) {
            Slot slot = (Slot) container.inventorySlots.get(i);
            if (isMouseOverSlot (slot, x, y)) return slot;
        }
        return null;
    }

    @Override
    protected void mouseClicked (int x, int y, int button) {
        super.mouseClicked (x, y, button);
        boolean blockPickPressed = button == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;

        if (button == 0 || button == 1 || blockPickPressed) {
            Slot slot = getSlotAtPosition (x, y);
            boolean dropItem = x < offsetX || y < offsetY || x >= offsetX + SIZE_X || y >= offsetY + SIZE_Y;
            int slotId = -1;

            if (slot != null) slotId = slot.slotNumber;
            if (dropItem) slotId = -999;
            
            if (slotId != -1) {
                if (blockPickPressed) {
                    handleMouseClick (slot, slotId, button, 3);
                }else {
                    boolean transferStack = slotId != -999 && (Keyboard.isKeyDown (42) || Keyboard.isKeyDown (54)); /* Keys: Shift. */
                    handleMouseClick (slot, slotId, button, transferStack ? 1 : 0);
                }
            }
        }
    }
    
    protected void handleMouseClick (Slot slot, int id, int button, int transferStack) { /* TODO(Marco): Really 'button'? */
        if (slot != null) id = slot.slotNumber;
        mc.playerController.windowClick (container.windowId, id, button, transferStack, mc.thePlayer);
    }
    
    
    
    
    
    protected void keyTyped (char character, int key) {
        if (key == 1 || key == mc.gameSettings.keyBindInventory.keyCode) mc.thePlayer.closeScreen ();
        func_82319_a (key);

        if (key == mc.gameSettings.keyBindPickBlock.keyCode && hoveredSlot != null && hoveredSlot.getHasStack()) {
            handleMouseClick (hoveredSlot, hoveredSlot.slotNumber, key, 3);
        }
    }

    protected boolean func_82319_a (int key) {
        if (mc.thePlayer.inventory.getItemStack () == null && hoveredSlot != null) {
            for (int i = 0; i < 9; ++i) {
                if (key == 2 + i) {
                    handleMouseClick (hoveredSlot, hoveredSlot.slotNumber, i, 2);
                    return true;
                }
            }
        }
        return false;
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
    
}
