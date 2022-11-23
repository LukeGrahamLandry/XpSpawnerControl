package brightspark.xpspawnercontrol;

import brightspark.xpspawnercontrol.config.XPModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(XpSpawnerControl.MOD_ID)
public class XpSpawnerControl {
    public static final String MOD_ID = "xpspawnercontrol";

    public XpSpawnerControl(){
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setupCaps);

        XPModConfig.init();

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public void setupCaps(RegisterCapabilitiesEvent event) {
        event.register(CapabilityMobFromSpawner.class);
    }

    @Mod.EventBusSubscriber
    public static class ModEventHandler {
        @SubscribeEvent
        public static void attachCap(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity && !(event.getObject() instanceof Player)) {
                event.addCapability(CapabilityMobFromSpawner.RL, new CapabilityMobFromSpawner.Provider());
            }
        }

        @SubscribeEvent
        public static void onMobSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.getWorld().isClientSide() || event.getEntityLiving() == null || event.getSpawner() == null){
                return;
            }

            LazyOptional<CapabilityMobFromSpawner> capProvider = event.getEntityLiving().getCapability(CapabilityMobFromSpawner.CAP, null);
            capProvider.ifPresent(CapabilityMobFromSpawner::setSpawnedFromSpawner);
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onMobDeathXp(LivingExperienceDropEvent event) {
            LazyOptional<CapabilityMobFromSpawner> capProvider = event.getEntityLiving().getCapability(CapabilityMobFromSpawner.CAP, null);
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
