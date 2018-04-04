package brightspark.xpspawnercontrol;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CapabilityMobFromSpawner extends INBTSerializable<NBTTagByte>
{
    ResourceLocation RL = new ResourceLocation(XpSpawnerControl.MODID, "_cap");

    void setSpawnedFromSpawner();

    boolean isSpawnedFromSpawner();

    class Impl implements CapabilityMobFromSpawner
    {
        //0 = false, 1 = true
        private byte spawnedBySpawner = 0;

        @Override
        public NBTTagByte serializeNBT()
        {
            return new NBTTagByte(spawnedBySpawner);
        }

        @Override
        public void deserializeNBT(NBTTagByte nbt)
        {
            spawnedBySpawner = nbt.getByte();
        }

        @Override
        public void setSpawnedFromSpawner()
        {
            spawnedBySpawner = 1;
        }

        @Override
        public boolean isSpawnedFromSpawner()
        {
            return spawnedBySpawner > 0;
        }
    }

    class Storage implements Capability.IStorage<CapabilityMobFromSpawner>
    {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CapabilityMobFromSpawner> capability, CapabilityMobFromSpawner instance, EnumFacing side)
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CapabilityMobFromSpawner> capability, CapabilityMobFromSpawner instance, EnumFacing side, NBTBase nbt)
        {
            instance.deserializeNBT((NBTTagByte) nbt);
        }
    }

    class Provider implements ICapabilityProvider, ICapabilitySerializable<NBTTagByte>
    {
        private CapabilityMobFromSpawner capabilityI;
        private Capability<CapabilityMobFromSpawner> capability;

        public Provider(Capability<CapabilityMobFromSpawner> capability)
        {
            this.capabilityI = capability.getDefaultInstance();
            this.capability = capability;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return this.capability == capability;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) capabilityI : null;
        }

        @Override
        public NBTTagByte serializeNBT()
        {
            return capabilityI.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagByte nbt)
        {
            capabilityI.deserializeNBT(nbt);
        }
    }
}
