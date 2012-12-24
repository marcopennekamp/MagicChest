package org.tilegames.mc.magicchest;

import net.minecraft.item.ItemBlock;

public class ItemBlockMagicChest extends ItemBlock {

    public ItemBlockMagicChest (int id) {
        super (id);
        setHasSubtypes (true);
    }
    
    public int getMetadata (int damage) {
        return damage << 2;
    }

}
