package org.tilegames.mc.magicchest;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.tilegames.mc.magicchest.upgrade.Upgrade;

public class ItemUpgrade extends Item {

    private Class<?> upgradeClass;
    
    public ItemUpgrade (int id, Class<?> upgradeClass) {
        super (id);
        this.upgradeClass = upgradeClass;
        this.setCreativeTab (CreativeTabs.tabMisc);
    }
    
    public Upgrade getUpgradeObject (TileEntityMagicChest chest) {
        Upgrade upgrade = null;
        try {
            upgrade = (Upgrade) upgradeClass.newInstance ();
            upgrade.init (chest, this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return upgrade;
    }

}
