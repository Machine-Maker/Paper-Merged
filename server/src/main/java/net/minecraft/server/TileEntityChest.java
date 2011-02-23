package net.minecraft.server;

public class TileEntityChest extends TileEntity implements IInventory {

    private ItemStack[] a = new ItemStack[27]; // CraftBukkit

    // CraftBukkit start
    public ItemStack[] getContents() {
        return a;
    }
    // CraftBukkit end

    public TileEntityChest() {}

    public int m_() {
        return 27;
    }

    public ItemStack c_(int i) {
        return this.a[i];
    }

    public ItemStack a(int i, int j) {
        if (this.a[i] != null) {
            ItemStack itemstack;

            if (this.a[i].count <= j) {
                itemstack = this.a[i];
                this.a[i] = null;
                this.h();
                return itemstack;
            } else {
                itemstack = this.a[i].a(j);
                if (this.a[i].count == 0) {
                    this.a[i] = null;
                }

                this.h();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void a(int i, ItemStack itemstack) {
        this.a[i] = itemstack;
        if (itemstack != null && itemstack.count > this.n_()) {
            itemstack.count = this.n_();
        }

        this.h();
    }

    public String c() {
        return "Chest";
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.l("Items");

        this.a = new ItemStack[this.m_()];

        for (int i = 0; i < nbttaglist.c(); ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.a(i);
            int j = nbttagcompound1.c("Slot") & 255;

            if (j >= 0 && j < this.a.length) {
                this.a[j] = new ItemStack(nbttagcompound1);
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.a.length; ++i) {
            if (this.a[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.a("Slot", (byte) i);
                this.a[i].a(nbttagcompound1);
                nbttaglist.a((NBTBase) nbttagcompound1);
            }
        }

        nbttagcompound.a("Items", (NBTBase) nbttaglist);
    }

    public int n_() {
        return 64;
    }

    public boolean a_(EntityHuman entityhuman) {
        return this.d.getTileEntity(this.e, this.f, this.g) != this ? false : entityhuman.d((double) this.e + 0.5D, (double) this.f + 0.5D, (double) this.g + 0.5D) <= 64.0D;
    }
}
