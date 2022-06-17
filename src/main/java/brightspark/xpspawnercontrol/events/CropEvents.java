package brightspark.xpspawnercontrol.events;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import brightspark.xpspawnercontrol.config.BlockXpConfig;
import brightspark.xpspawnercontrol.config.CropGrowConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = XpSpawnerControl.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CropEvents {
    static Random rand = new Random();

    @SubscribeEvent
    public static void stopCropGrowth(BlockEvent.CropGrowEvent.Pre event){
        if (event.getWorld().isClientSide()) return;

        float chance = 1;
        for (CropGrowConfig.CropGrowthRule rule : CropGrowConfig.getRule(event.getState())){
            if (rule.matches(event.getWorld(), event.getPos(), event.getState())){
                chance *= rule.chance;
            }
        }

        float r = rand.nextFloat();
        if (r < chance){
            event.setResult(Event.Result.DEFAULT);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void stopCropGrowth(BonemealEvent event){
        if (event.getWorld().isClientSide()) return;

        float chance = 1;
        for (CropGrowConfig.CropGrowthRule rule : CropGrowConfig.getRule(event.getBlock())){
            if (rule.effectBoneMeal && rule.matches(event.getWorld(), event.getPos(), event.getBlock())){
                chance *= rule.chance;
            }
        }

        float r = rand.nextFloat();
        if (r < chance){
            // pass
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void startserver(FMLServerStartedEvent event) {
        CropGrowConfig.init(event.getServer());
    }
}
