package com.kentington.thaumichorizons.common.items.lenses;

import net.minecraft.potion.PotionEffect;

public class LensPotionEffects {

    public static boolean isNightVisionGrantedByLens(PotionEffect effect) {
        return effect instanceof LensNightVision lensNightvision && lensNightvision.isGrantedByLens;
    }

    static class LensNightVision extends PotionEffect {

        public boolean isGrantedByLens;

        public LensNightVision(int potionID, int duration, int amplifier, boolean isAmbient) {
            super(potionID, duration, amplifier, isAmbient);
            isGrantedByLens = true;
        }

        public void combine(PotionEffect effect) {
            // If this is replaced by another night vision effect, for example from drinking a potion, it should not be
            // turned off by the lens.
            super.combine(effect);
            isGrantedByLens = effect instanceof LensNightVision;
        }
    }
}
