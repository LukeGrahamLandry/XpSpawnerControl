package brightspark.xpspawnercontrol;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = XpSpawnerControl.MODID, name = XpSpawnerControl.NAME, version = XpSpawnerControl.VERSION)
public class XpSpawnerControl
{
    public static final String MODID = "xpspawnercontrol";
    public static final String NAME = "Xp Spawner Control";
    public static final String VERSION = "@VERSION@";

    private static Logger LOGGER;

    @CapabilityInject(CapabilityMobFromSpawner.class)
    private static Capability<CapabilityMobFromSpawner> CAP = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();
        CapabilityManager.INSTANCE.register(CapabilityMobFromSpawner.class, new CapabilityMobFromSpawner.Storage(), CapabilityMobFromSpawner.Impl::new);
    }

    private static void log(String message, Object... args)
    {
        LOGGER.info(String.format(MODID + " -> " + message, args));
    }

    @Mod.EventBusSubscriber
    public static class ModEventHandler
    {
        @SubscribeEvent
        public static void attachCap(AttachCapabilitiesEvent<Entity> event)
        {
            if(event.getObject() instanceof EntityLivingBase && !(event.getObject() instanceof EntityPlayer))
            {
                if(ModConfig.debug)
                    log("Added capability to %s (%s)", event.getObject().getName(), getEntityId(event.getObject()));
                event.addCapability(CapabilityMobFromSpawner.RL, new CapabilityMobFromSpawner.Provider(CAP));
            }
        }

        @SubscribeEvent
        public static void onMobSpawnerSpawn(LivingSpawnEvent.CheckSpawn event)
        {
            if(event.getWorld().isRemote || event.getEntityLiving() == null || event.getSpawner() == null)
                return;

            CapabilityMobFromSpawner cap = event.getEntityLiving().getCapability(CAP, null);
            if(cap != null && !cap.isSpawnedFromSpawner())
            {
                cap.setSpawnedFromSpawner();
                if(ModConfig.debug)
                    log("Setting as spawned from spawner (%s)", getEntityId(event.getEntityLiving()));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onMobDeathXp(LivingExperienceDropEvent event)
        {
            CapabilityMobFromSpawner cap = event.getEntityLiving().getCapability(CAP, null);
            if(cap == null || !cap.isSpawnedFromSpawner()) return;

            String name = getEntityId(event.getEntityLiving());
            boolean isInList = contains(ModConfig.entityList, name);
            boolean shouldRemoveXp = ModConfig.isBlacklist != isInList;
            if(shouldRemoveXp) event.setDroppedExperience(0);
            if(ModConfig.debug)
                log("Should remove XP from %s? %s, Blacklist: %s, Is in list: %s",
                        name, shouldRemoveXp, ModConfig.isBlacklist, isInList);
        }

        private static boolean contains(String[] list, String value)
        {
            for(String s : list)
                if(s.equalsIgnoreCase(value))
                    return true;
            return false;
        }

        private static String getEntityId(Entity entity)
        {
            ResourceLocation key = EntityList.getKey(entity);
            return key == null ? "null" : key.toString();
        }
    }
}
