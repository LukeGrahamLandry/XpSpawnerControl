package brightspark.xpspawnercontrol;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CapabilityMobFromSpawner extends INBTSerializable<CompoundNBT> {
    ResourceLocation RL = new ResourceLocation(XpSpawnerControl.MOD_ID, "_cap");

    void setSpawnedFromSpawner();

    boolean isSpawnedFromSpawner();

    class Impl implements CapabilityMobFromSpawner {
        private static final String NBT_KEY = "isfromspawner";
        private boolean spawnedBySpawner = false;

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean(NBT_KEY, spawnedBySpawner);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
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

    class Storage implements Capability.IStorage<CapabilityMobFromSpawner> {

        @Nullable
        @Override
        public CompoundNBT writeNBT(Capability<CapabilityMobFromSpawner> capability, CapabilityMobFromSpawner instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CapabilityMobFromSpawner> capability, CapabilityMobFromSpawner instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }

    class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
        private CapabilityMobFromSpawner capabilityI;
        private Capability<CapabilityMobFromSpawner> capability;

        public Provider(Capability<CapabilityMobFromSpawner> capability) {
            this.capabilityI = capability.getDefaultInstance();
            this.capability = capability;
        }

        @Nullable
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return capability == this.capability && capabilityI != null ? (LazyOptional<T>) LazyOptional.of(() -> capabilityI) : LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            return capabilityI.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            capabilityI.deserializeNBT(nbt);
        }
    }
}
