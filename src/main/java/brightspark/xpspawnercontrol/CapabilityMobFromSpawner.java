package brightspark.xpspawnercontrol;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CapabilityMobFromSpawner extends INBTSerializable<CompoundTag> {
    ResourceLocation RL = new ResourceLocation(XpSpawnerControl.MOD_ID, "_cap");
    Capability<CapabilityMobFromSpawner> CAP = CapabilityManager.get(new CapabilityToken<>(){});

    void setSpawnedFromSpawner();

    boolean isSpawnedFromSpawner();

    class Impl implements CapabilityMobFromSpawner {
        private static final String NBT_KEY = "isfromspawner";
        private boolean spawnedBySpawner = false;

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(NBT_KEY, spawnedBySpawner);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            spawnedBySpawner = nbt.getBoolean(NBT_KEY);
        }

        @Override
        public void setSpawnedFromSpawner()
        {
            spawnedBySpawner = true;
        }

        @Override
        public boolean isSpawnedFromSpawner()
        {
            return spawnedBySpawner;
        }
    }


    class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        private CapabilityMobFromSpawner data;

        public Provider() {
            this.data = new Impl();
        }

        @Nullable
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return capability == CAP && data != null ? (LazyOptional<T>) LazyOptional.of(() -> data) : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            data.deserializeNBT(nbt);
        }
    }
}
