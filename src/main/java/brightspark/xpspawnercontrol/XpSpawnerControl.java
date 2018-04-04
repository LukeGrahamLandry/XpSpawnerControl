package brightspark.xpspawnercontrol;

import brightspark.xpspawnercontrol.capability.CapabilityMobFromSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = XpSpawnerControl.MODID, name = XpSpawnerControl.NAME, version = XpSpawnerControl.VERSION)
public class XpSpawnerControl
{
    public static final String MODID = "xpspawnercontrol";
    public static final String NAME = "Xp Spawner Control";
    public static final String VERSION = "@VERSION@";

    @CapabilityInject(CapabilityMobFromSpawner.class)
    private static Capability<CapabilityMobFromSpawner> CAP = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(CapabilityMobFromSpawner.class, new CapabilityMobFromSpawner.Storage(), CapabilityMobFromSpawner.Impl::new);
    }

    @Mod.EventBusSubscriber
    public static class ModEventHandler
    {
        @SubscribeEvent
        public static void attachCap(AttachCapabilitiesEvent<Entity> event)
        {
            if(event.getObject() instanceof EntityLivingBase && !(event.getObject() instanceof EntityPlayer))
                event.addCapability(CapabilityMobFromSpawner.RL, new CapabilityMobFromSpawner.Provider(CAP));
        }

        @SubscribeEvent
        public static void onMobSpawnerSpawn(LivingSpawnEvent.SpecialSpawn event)
        {
            if(event.getWorld().isRemote || event.getSpawner() == null || event.getEntityLiving() == null)
                return;

            CapabilityMobFromSpawner cap = event.getEntityLiving().getCapability(CAP, null);
            if(cap != null) cap.setSpawnedFromSpawner();
        }

        @SubscribeEvent
        public static void onMobDeathXp(LivingExperienceDropEvent event)
        {
            CapabilityMobFromSpawner cap = event.getEntityLiving().getCapability(CAP, null);
            if(cap == null || !cap.isSpawnedFromSpawner()) return;

            String name = EntityList.getKey(event.getEntityLiving()).toString();
            boolean shouldRemoveXp = ModConfig.isBlacklist != contains(ModConfig.entityList, name);
            if(shouldRemoveXp) event.setDroppedExperience(0);
        }

        private static boolean contains(String[] list, String value)
        {
            for(String s : list)
                if(s.equalsIgnoreCase(value))
                    return true;
            return false;
        }
    }
}
