package brightspark.xpspawnercontrol.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CropGrowConfig {
    static HashMap<Block, List<CropGrowthRule>> rules = new HashMap<>();

    public static class CropGrowthRule {
        public float chance = 0F;
        public Integer maxY;
        public Integer minY;
        public Float maxTemp;
        public Float minTemp;
        public boolean effectBoneMeal = true;

        public CropGrowthRule(Integer maxY, Integer minY, Float maxTemp, Float minTemp){
            this.maxY = maxY;
            this.minY = minY;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
        }

        public boolean matches(LevelAccessor world, BlockPos pos, BlockState state) {
            if (this.minY != null){
                if (pos.getY() >= this.minY) return true;
            }

            if (this.maxY != null){
                if (pos.getY() <= this.maxY) return true;
            }

            Float temp = null;
            if (this.minTemp != null) {
                temp = world.getBiome(pos).value().getBaseTemperature();
                if (temp >= this.minTemp) return true;
            }

            if (this.maxTemp != null) {
                if (temp == null) temp = world.getBiome(pos).value().getBaseTemperature();
                if (temp >= this.maxTemp) return true;
            }

            return false;
        }
    }

    private static Integer getIntOrNull(JsonObject obj, String key){
        return obj.has(key) ? obj.get(key).getAsInt() : null;
    }

    private static Float getFloatOrNull(JsonObject obj, String key){
        return obj.has(key) ? obj.get(key).getAsFloat() : null;
    }

    public static void init(MinecraftServer server){
        System.out.println("init CropGrowthRule");

        JsonElement config = JsonConfig.load("cropgrowthmodifiers.json", server);

        System.out.println(config);
        for (JsonElement entry : config.getAsJsonArray()){
            try {
                JsonObject data = entry.getAsJsonObject();
                CropGrowthRule rule = new CropGrowthRule(getIntOrNull(data, "maxY"), getIntOrNull(data, "minY"),getFloatOrNull(data, "maxTemp"), getFloatOrNull(data, "minTemp"));

                JsonElement chance = data.get("chance");
                if (chance != null){
                    try {
                        rule.chance = chance.getAsFloat();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                JsonElement effectBoneMeal = data.get("bonemeal");
                if (effectBoneMeal != null){
                    try {
                        rule.effectBoneMeal = effectBoneMeal.getAsBoolean();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for (JsonElement blockEntry : data.get("blocks").getAsJsonArray()){
                    try{
                        ResourceLocation registryName = new ResourceLocation(blockEntry.getAsString());
                        Block key = ForgeRegistries.BLOCKS.getValue(registryName);

                        if (!rules.containsKey(key)){
                            rules.put(key, new ArrayList<>());
                        }
                        rules.get(key).add(rule);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static List<CropGrowthRule> getRule(BlockState state){
        if (!rules.containsKey(state.getBlock())) return Collections.emptyList();
        return rules.get(state.getBlock());
    }
}
