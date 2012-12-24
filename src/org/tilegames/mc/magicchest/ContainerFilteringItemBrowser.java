package org.tilegames.mc.magicchest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerFilteringItemBrowser extends Container {

	@Override
	public boolean canInteractWith (EntityPlayer player) {
		return true;
	}

}
