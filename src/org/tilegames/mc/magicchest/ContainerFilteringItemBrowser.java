package org.tilegames.mc.magicchest;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;

public class ContainerFilteringItemBrowser extends Container {

	@Override
	public boolean canInteractWith (EntityPlayer player) {
		return true;
	}

}
