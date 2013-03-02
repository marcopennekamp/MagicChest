package org.tilegames.mc.magicchest.upgrade;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

public class UpgradeVortexDevice extends Upgrade {

    public static double pullDistance;
    
    @Override
    public void update () {
        /* Soak items in. */
        @SuppressWarnings("unchecked")
        List<EntityItem> entityList = chest.worldObj.getEntitiesWithinAABB (EntityItem.class, 
                AxisAlignedBB.getBoundingBox (
                        chest.xCoord + 0.5 - pullDistance, 
                        chest.yCoord + 0.5 - pullDistance, 
                        chest.zCoord + 0.5 - pullDistance, 
                        chest.xCoord + 0.5 + pullDistance, 
                        chest.yCoord + 0.5 + pullDistance, 
                        chest.zCoord + 0.5 + pullDistance
                ));
        int size = entityList.size();
        for (int i = 0; i < size; ++i) {
            soakEntityIn (entityList.get (i));
        }
    }
    
    private void soakEntityIn (EntityItem item) {
        if (item.isDead) return;
        if (chest.processItemStack (item.getEntityItem (), false) != null) {
            item.setDead ();
            return;
        }
        
        final double offsetX = 0.5;
        final double offsetY = 0.5;
        final double offsetZ = 0.5;
        
        double dx = (chest.xCoord + offsetX) - item.posX;
        double dy = (chest.yCoord + offsetY) - item.posY;
        double dz = (chest.zCoord + offsetZ) - item.posZ;
        double distance = dx * dx + dy * dy + dz * dz;
        double pullDistanceSquared = pullDistance * pullDistance;
        
        if (distance <= pullDistanceSquared) {
            double ax = dx / distance * 0.05;
            double ay = dy / distance * 0.05; 
            double az = dz / distance * 0.05;
            
            /* Reduce gravity by 80%. */
            if (dy > 0.0) ay += 0.032;
            
            item.setVelocity (item.motionX + ax, item.motionY + ay, item.motionZ + az);
        }
    }

}
