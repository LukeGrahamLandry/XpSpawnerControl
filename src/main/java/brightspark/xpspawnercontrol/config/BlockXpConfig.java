package brightspark.xpspawnercontrol.config;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

// currently only supports IntegerProperty
// todo: support strings, enum, etc

// todo: allow changing xp vanilla drops?

public class BlockXpConfig {
    static HashMap<Block, List<BlockXpRule>> rules = new HashMap<>();
    static Random rand = new Random();

    static class BlockXpRule {
        public float chance = 1;
        int amount;
        public List<Pair<String, Integer>> intProps = new ArrayList<>();
        public BlockXpRule(int amount){
            this.amount = amount;
        }
    }

    public static void init(MinecraftServer server){
        System.out.println("init BlockXpConfig");

        JsonElement config = JsonConfig.load("blockxpdrops.json", server);

        System.out.println(config);
        for (JsonElement entry : config.getAsJsonArray()){
            try{
                JsonObject data = entry.getAsJsonObject();
                BlockXpRule rule = new BlockXpRule(data.get("amount").getAsInt());

                JsonObject props = data.getAsJsonObject("state");
                if (props != null){
                    for (Map.Entry<String, JsonElement> stateDescription : props.entrySet()){
                        try {
                            rule.intProps.add(new Pair<>(stateDescription.getKey(), stateDescription.getValue().getAsInt()));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                JsonElement chance = data.get("chance");
                if (chance != null){
                    try {
                        rule.chance = chance.getAsFloat();
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

    public static boolean hasRule(BlockState state){
        return rules.containsKey(state.getBlock());
    }

    public static int getXpAmount(BlockState state){
        for (BlockXpRule rule : rules.get(state.getBlock())){
            boolean isMatch = true;
            for (Pair<String, Integer> requiredState : rule.intProps){
                for (Property<?> prop : state.getProperties()){
                    if (requiredState.getFirst().equals(prop.getName()) && prop.getValueClass() == Integer.class){
                        if (requiredState.getSecond() != state.getValue(prop)){
                            isMatch = false;
                            break;
                        }
                    }
                }
            }

            if (isMatch) {
                if (rand.nextFloat() < rule.chance){
                    return rule.amount;
                } else {
                    return 0;
                }
            }
        }

        return 0;
    }
}