package org.tilegames.mc.magicchest.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import MagicChest.common.MagicChest;

public class InfoOpenGui extends Info {
    public int guiId;
    public int param;
    
    public InfoOpenGui () {
        super ();
    }
    
    public InfoOpenGui (TileEntity tileEntity, int guiId, int param) {
        super (tileEntity);
        this.guiId = guiId;
        this.param = param;
    }
    
    @Override
    public void write (DataOutputStream out) throws IOException {
        super.write (out);
        out.writeByte (guiId);
        out.writeByte (param);
    }
    
    @Override
    public void read (DataInputStream in) throws IOException {
        super.read (in);
        guiId = in.readByte ();
        param = in.readByte ();
    }
    
    @Override
    public int size () {
        return super.size () + 2;
    }

    @Override
    public void evaluate (EntityPlayer player) {
        player.openGui (MagicChest.instance, (((int) param) << 8) | (int) guiId, player.worldObj, x, y, z);
    }

}
