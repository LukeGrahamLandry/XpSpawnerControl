package brightspark.xpspawnercontrol.events;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import brightspark.xpspawnercontrol.config.BlockXpConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

@Mod.EventBusSubscriber(modid = XpSpawnerControl.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakEvents {
    @SubscribeEvent
    public static void dropConfigurableBlockXP(BlockEvent.BreakEvent event){
        if (event.getPlayer().level.isClientSide()) return;
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, event.getPlayer()) > 0) return;

        if (BlockXpConfig.hasRule(event.getState())) event.setExpToDrop(BlockXpConfig.getXpAmount(event.getState()));
     }

    @SubscribeEvent
    public static void startserver(FMLServerStartedEvent event) {
        BlockXpConfig.init(event.getServer());
    }
}
