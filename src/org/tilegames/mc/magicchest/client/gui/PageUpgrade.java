package org.tilegames.mc.magicchest.client.gui;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.tilegames.mc.magicchest.ItemUpgrade;
import org.tilegames.mc.magicchest.network.PacketHandler;
import org.tilegames.mc.magicchest.upgrade.Upgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class PageUpgrade extends Page {

    private static final int slotX = 17;
    private static final int slotY = 35;
    
    public static final int BUTTON_ID = Page.BASE_ID + 3;
    
    private int id;
    
    private float errorDisplayTime = 0.0f;
    private String error = "";
    
    public PageUpgrade (GuiPage gui, int id) {
        super (gui);
        this.id = id;
    }
    

    @Override
    public String getTitle () {
        return "Upgrade";
    }

    @Override
    public int getButtonId () {
        return BUTTON_ID + id;
    }

    @Override
    public int getButtonTexture () {
        return 16;
    }
    
    
    @Override
    public void tick () {
        if (errorDisplayTime > 0.05f) {
            errorDisplayTime -= 0.05f;
        }else {
            errorDisplayTime = 0.0f;
        }
    }
    
    @Override
    public void draw (int mouseX, int mouseY) {
        gui.renderHelper.bindAndDrawBackgroundTexture ("Pages/Upgrade.png");
        
        /* Draw upgrade item. */
        Upgrade upgrade = gui.chest.upgrades[id];
        if (upgrade != null) {
            gui.renderHelper.drawItemStack (upgrade.stackCache, slotX + 1, slotY + 1);
        }
        
        /* Draw slot hover. */
        if (gui.renderHelper.pointInRectangle (slotX + 1, slotY + 1, 16, 16, mouseX, mouseY)) {
            gui.renderHelper.drawHoverRectangle (slotX + 1, slotY + 1, 16, 16, 0x80FFFFFF);
        }
        
        /* Render error message or Upgrade name. */
        boolean renderError = errorDisplayTime > 0.0f;
        if (renderError || upgrade != null) {
            /* Calculate string width. */
            String message;
            if (renderError) {
                message = error;
            }else {
                message = upgrade.stackCache.getDisplayName ();
            }
            
            /* Center it between the right of the gui and the slot. */
            int messageX = (slotX + 18) + (((gui.sizeX - 3 /* Padding. */) - (slotX + 18)) - gui.getFontRenderer ().getStringWidth (message)) / 2;
            int messageY = slotY + 5;
            
            int alpha;
            if (renderError) {
                /* Fade out within a second. */
                alpha = (int) (errorDisplayTime * 256.0f);
                if (alpha <= 0x03) {
                    alpha |= 0x04; /* Prevent setting 0 - 3 to 255. */
                }else if (alpha > 255) {
                    alpha = 255;
                }
            }else {
                alpha = 0xFF;
            }
        
            GL11.glEnable (GL11.GL_BLEND);
            GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            gui.getFontRenderer ().drawString (message, messageX, messageY, 0x404040 | (alpha << 24));
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public boolean onClick (int x, int y, int button) {
        x -= gui.offsetX;
        y -= gui.offsetY;
        if (x >= slotX && x <= slotX + 18 && y >= slotY && y <= slotY + 18) {
            if (button == 0) { /* Left click. */
                CallbackOverlay callback = new CallbackOverlay () {
                    @Override
                    public void onItemSelected (ItemStack stack) {
                        if (!gui.chest.hasUpgrade (stack.getItem ())) { /* Check if the upgrade is already inserted. */
                            PacketHandler.sendPacketChestSetUpgradeItem (gui.chest, id, 
                                stack.getItem (), PacketHandler.TARGET_SERVER, null);
                        }else {
                            /* Display error message. */
                            error = "Upgrade already installed!";
                            errorDisplayTime = 3.0f;
                        }
                    }
                };
                gui.overlay = new OverlayItemSelect ((GuiMagicChest) gui, callback, ItemUpgrade.class, slotX + 20, slotY);
            }else if (button == 1) { /* Right click. */
                /* Remove upgrade. */
                PacketHandler.sendPacketChestSetUpgradeItem (gui.chest, id, 
                        null, PacketHandler.TARGET_SERVER, null);
            }
            return true;
        }
        
        return false;
    }

    @Override
    public boolean onKeyType (char character, int key) {
        return false;
    }

}
