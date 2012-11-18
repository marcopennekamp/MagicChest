package org.tilegames.mc.magicchest.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public abstract class InfoSlotUpdate extends Info {
    
    public int slot;
    public ItemStack stack;

    public InfoSlotUpdate () {
        super ();
    }
    
    public InfoSlotUpdate(TileEntity tileEntity, int slot, ItemStack stack) {
        super (tileEntity);
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void write (DataOutputStream out) throws IOException {
        super.write (out);
        out.writeByte (slot);
        if (stack != null) {
            out.writeShort (stack.itemID);
            out.writeShort (stack.getItemDamage ());
        }else {
            out.writeShort (-1);
            out.writeShort (0);
        }
    }
    
    @Override
    public void read (DataInputStream in) throws IOException {
        super.read (in);
        slot = in.readByte ();
        int id = in.readShort ();
        int damage = in.readShort ();
        if (id >= 0) {
            stack = new ItemStack (id, 1, damage);
        }else {
            stack = null;
        }
    }
  
    @Override
    public int size () {
        return super.size () + 5;
    }
    
}
