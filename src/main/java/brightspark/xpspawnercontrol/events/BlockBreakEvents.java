package brightspark.xpspawnercontrol.events;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import brightspark.xpspawnercontrol.config.BlockXpConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
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

        BlockXpConfig.BlockXpRule rule = BlockXpConfig.getRule(event.getState());
        if (rule != null) {
            int xp = rule.amount;
            if (xp > 0 && rule.ignoreCancel){
                popExperience((ServerWorld) event.getWorld(), event.getPos(), xp);
            } else {
                event.setExpToDrop(xp);
            }
        }
    }

    private static void popExperience(ServerWorld p_180637_1_, BlockPos p_180637_2_, int p_180637_3_) {
        if (p_180637_1_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_180637_1_.restoringBlockSnapshots) {
            while(p_180637_3_ > 0) {
                int i = ExperienceOrbEntity.getExperienceValue(p_180637_3_);
                p_180637_3_ -= i;
                p_180637_1_.addFreshEntity(new ExperienceOrbEntity(p_180637_1_, (double)p_180637_2_.getX() + 0.5D, (double)p_180637_2_.getY() + 0.5D, (double)p_180637_2_.getZ() + 0.5D, i));
            }
        }

    }

    @SubscribeEvent
    public static void startserver(FMLServerStartedEvent event) {
        BlockXpConfig.init(event.getServer());
    }
}
