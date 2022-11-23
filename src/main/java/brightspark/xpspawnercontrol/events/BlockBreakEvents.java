package brightspark.xpspawnercontrol.events;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import brightspark.xpspawnercontrol.config.BlockXpConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                popExperience((ServerLevel) event.getWorld(), event.getPos(), xp);
            } else {
                event.setExpToDrop(xp);
            }
        }
    }

    private static void popExperience(ServerLevel world, BlockPos pos, int amount) {
        if (world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots) {
            while(amount > 0) {
                int i = ExperienceOrb.getExperienceValue(amount);
                amount -= i;
                world.addFreshEntity(new ExperienceOrb(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, i));
            }
        }

    }

    @SubscribeEvent
    public static void startserver(ServerStartedEvent event) {
        BlockXpConfig.init(event.getServer());
    }
}
