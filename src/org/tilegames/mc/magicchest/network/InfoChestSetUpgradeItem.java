package org.tilegames.mc.magicchest.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import org.tilegames.mc.magicchest.ItemUpgrade;
import org.tilegames.mc.magicchest.TileEntityMagicChest;
import org.tilegames.mc.magicchest.upgrade.Upgrade;

public class InfoChestSetUpgradeItem extends Info {

    private int index;
    private Item item;
    
    public InfoChestSetUpgradeItem () {
        super ();
    }
    
    public InfoChestSetUpgradeItem (TileEntity tileEntity, int index, Item item) {
        super (tileEntity);
        this.index = index;
        this.item = item;
    }
    
    @Override
    public void write (DataOutputStream out) throws IOException {
        super.write (out);
        out.writeByte (index);
        out.writeShort ((item == null) ? 0 : item.itemID);
    }
    
    @Override
    public void read (DataInputStream in) throws IOException {
        super.read (in);
        index = in.readByte ();
        int itemId = in.readShort ();
        if (itemId == 0) {
            item = null;
        }else {
            item = Item.itemsList[itemId];
        }
    }
    
    @Override
    public void evaluate (EntityPlayer player) {
        TileEntity entity = player.worldObj.getBlockTileEntity (x, y, z);
        if (entity != null && entity.getClass () == TileEntityMagicChest.class) {
            TileEntityMagicChest chest = (TileEntityMagicChest) entity;
            
            if (index >= 3) {
                System.out.println ("Wrong upgrade index. Ignoring packet!");
                return;
            }
            
            if (item != null && !(item instanceof ItemUpgrade)) {
                System.out.println ("The item is not a subclass of ItemUpgrade. Ignoring packet!");
                return;
            }
            
            Upgrade upgrade;
            if (item != null) {
                upgrade = ((ItemUpgrade) item).getUpgradeObject (chest);
            }else {
                upgrade = null;
            }
            
            boolean set = chest.setUpgrade (index, upgrade, player.inventory);
            if (set) { /* Don't do anything when something went wrong. */ 
                /* Server: Remove upgrade item from inventory. Broadcast the upgrade slots. */
                if (!player.worldObj.isRemote) {
                    if (item != null) {
                        /* Try to take out of chest inventory. */
                        if (chest.removeItemStack (upgrade.stackCache.copy ()) != null) {
                            /* Take out of player inventory. */
                            player.inventory.consumeInventoryItem (item.itemID);
                            player.inventory.onInventoryChanged ();
                        }
                    }
                    
                    chest.broadcastUpgradeSlot (index);
                }
            }
        }
    }
    
    @Override
    public int size () {
        return super.size () + 2 /* item id. */ + 1 /* index. */;
    }

}
