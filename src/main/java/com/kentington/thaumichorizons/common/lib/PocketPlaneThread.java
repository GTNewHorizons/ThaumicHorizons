//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib;

import net.minecraft.world.World;

import thaumcraft.api.aspects.AspectList;

public class PocketPlaneThread implements Runnable {

    PocketPlaneData data;
    AspectList aspects;
    World world;
    int x;
    int y;
    int z;
    int returnID;

    public PocketPlaneThread(final PocketPlaneData data, final AspectList aspects, final World world, final int x,
            final int y, final int z, final int returnID) {
        this.data = data;
        this.aspects = aspects;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.returnID = returnID;
    }

    @Override
    public void run() {
        PocketPlaneData.generatePocketPlane(this.aspects, this.data, this.world, this.x, this.y, this.z, this.returnID);
    }
}
