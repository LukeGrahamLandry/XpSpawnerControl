package brightspark.xpspawnercontrol;

import net.minecraftforge.common.config.Config;

@Config(modid = XpSpawnerControl.MODID)
@Config.LangKey(XpSpawnerControl.NAME)
public class ModConfig
{
    @Config.Comment("Whether the entity list is a blacklist (true) or a whitelist (false) of removing experience drops")
    public static boolean isBlacklist = true;

    @Config.Comment("A list of all of the entity registry names which this mod will affect (e.g. minecraft:zombie)")
    public static String[] entityList = {""};

    @Config.Comment("Set to true to turn on debugging log messages (WARNING: This may spam the console with logs)")
    public static boolean debug = false;
}
