package brightspark.xpspawnercontrol;

import brightspark.xpspawnercontrol.config.BlockXpConfig;
import brightspark.xpspawnercontrol.config.XPModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(XpSpawnerControl.MOD_ID)
public class XpSpawnerControl {
    public static final String MOD_ID = "xpspawnercontrol";

    public XpSpawnerControl(){
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        XPModConfig.init();

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @CapabilityInject(CapabilityMobFromSpawner.class)
    private static Capability<CapabilityMobFromSpawner> CAP = null;

    public void setup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(CapabilityMobFromSpawner.class, new CapabilityMobFromSpawner.Storage(), CapabilityMobFromSpawner.Impl::new);
    }

    @Mod.EventBusSubscriber
    public static class ModEventHandler {
        @SubscribeEvent
        public static void attachCap(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity && !(event.getObject() instanceof PlayerEntity)) {
                event.addCapability(CapabilityMobFromSpawner.RL, new CapabilityMobFromSpawner.Provider(CAP));
            }
        }

        @SubscribeEvent
        public static void onMobSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.getWorld().isClientSide() || event.getEntityLiving() == null || event.getSpawner() == null){
                return;
            }

            LazyOptional<CapabilityMobFromSpawner> capProvider = event.getEntityLiving().getCapability(CAP, null);
            capProvider.ifPresent(CapabilityMobFromSpawner::setSpawnedFromSpawner);
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onMobDeathXp(LivingExperienceDropEvent event) {
            LazyOptional<CapabilityMobFromSpawner> capProvider = event.getEntityLiving().getCapability(CAP, null);
            capProvider.ifPresent((cap) -> {
                if (!cap.isSpawnedFromSpawner()) return;


                ResourceLocation registryName = event.getEntityLiving().getType().getRegistryName();
                if (registryName == null) return;

                boolean isInList = XPModConfig.entityList.get().contains(registryName.toString());
                if (XPModConfig.isBlacklist.get() && !isInList){
                    event.setDroppedExperience(0);
                }
                if (!XPModConfig.isBlacklist.get() && isInList){
                    event.setDroppedExperience(0);
                }
            });
        }
    }
}
