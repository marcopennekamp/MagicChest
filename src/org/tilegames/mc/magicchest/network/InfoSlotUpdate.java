package org.tilegames.mc.magicchest.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class InfoSlotUpdate extends Info {
    
    public int[] slots;
    public ItemStack[] stacks;

    public InfoSlotUpdate () {
        super ();
    }
    
    public InfoSlotUpdate(TileEntity tileEntity, int[] slots, ItemStack[] stacks) {
        super (tileEntity);
        this.slots = slots;
        this.stacks = stacks;
    }

    @Override
    public void write (DataOutputStream out) throws IOException {
        super.write (out);
        
        out.writeByte (slots.length);
        for (int i = 0; i < slots.length; ++i) {
            out.writeByte (slots[i]);
            ItemStack stack = stacks[i];
            if (stack != null) {
                out.writeShort (stack.itemID);
                out.writeShort (stack.getItemDamage ());
            }else {
                out.writeShort (-1);
                out.writeShort (0);
            }
        }
    }
    
    @Override
    public void read (DataInputStream in) throws IOException {
        super.read (in);
        
        int length = in.readByte ();
        slots = new int[length];
        stacks = new ItemStack[length];
        for (int i = 0; i < length; ++i) {
            slots[i] = in.readByte ();
            int id = in.readShort ();
            int damage = in.readShort ();
            if (id >= 0) {
                stacks[i] = new ItemStack (id, 1, damage);
            }else {
                stacks[i] = null;
            }
        }
    }
  
    @Override
    public int size () {
        return super.size () + 1 + slots.length * 5;
    }
    
}
