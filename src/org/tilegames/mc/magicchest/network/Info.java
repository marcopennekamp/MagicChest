package org.tilegames.mc.magicchest.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;

public abstract class Info {
    public int x;
    public int y;
    public int z;
    
    public Info () {
        
    }
    
    public Info (TileEntity tileEntity) {
        x = tileEntity.xCoord;
        y = tileEntity.yCoord;
        z = tileEntity.zCoord;
    }
    
    
    public void write (DataOutputStream out) throws IOException {
        out.writeInt (x);
        out.writeInt (y);
        out.writeInt (z);
    }
    
    public void read (DataInputStream in) throws IOException {
        x = in.readInt ();
        y = in.readInt ();
        z = in.readInt ();
    }
    
    public abstract void evaluate (EntityPlayer player);
    
    public int size () {
        return 12;
    }
    
}
