package brightspark.xpspawnercontrol.config;


import brightspark.xpspawnercontrol.XpSpawnerControl;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XPModConfig {
    public static final ForgeConfigSpec server_config;

    public static final ForgeConfigSpec.BooleanValue isBlacklist;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> entityList;

    static {
        final ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

        serverBuilder.comment("Spawner XP Config server side configuration")
                .push("server");

        isBlacklist = serverBuilder
                .comment("Whether the entity list is a blacklist (true) or a whitelist (false) of removing experience drops")
                .define("isBlacklist", true);

        entityList = serverBuilder
                .comment("A list of all of the entity registry names which this mod will affect. ex. [\"minecraft:skeleton\", \"minecraft:zombie\"]. An empty list with isBlacklist=true will make no mobs from spawners drop xp. An empty list with isBlacklist=false will be vanilla behaviour (all spawner mobs drop xp)")
                .defineList("entityList", Collections.emptyList(), (s) -> ((String)s).split(":").length > 0);

        server_config = serverBuilder.build();
    }

    public static void init(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server_config);
        CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(XpSpawnerControl.MOD_ID + ".toml").toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        server_config.setConfig(file);
    }
}
