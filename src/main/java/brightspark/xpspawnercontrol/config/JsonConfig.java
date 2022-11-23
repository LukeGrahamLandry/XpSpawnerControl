package brightspark.xpspawnercontrol.config;

import brightspark.xpspawnercontrol.XpSpawnerControl;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class JsonConfig {
    public static JsonElement load(String filename, MinecraftServer server){
        final Path serverConfig = server.getWorldPath(new LevelResource("serverconfig"));
        FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");

        File statsDataFile = new File(serverConfig.toFile(), filename);
        if (!statsDataFile.exists()){
            copyDefaults(serverConfig.toFile());
        }

        return loadStats(statsDataFile);
    }

    private static void copyDefaults(File target) {
        // load the default json file from jar
        try {
            URI uri = JsonConfig.class.getResource("/config").toURI();
            AtomicReference<Path> myPath = new AtomicReference<>();
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath.set(fileSystem.getPath("/config"));
            } else if (uri.getScheme().equals("modjar")){
                // fixes java.nio.file.FileSystemNotFoundException: Provider modjar not installed
                FMLLoader.getLoadingModList().getModFiles().forEach((modFile) -> {
                    modFile.getMods().forEach((modInfo) -> {
                        if (modInfo.getModId().equals(XpSpawnerControl.MOD_ID)){
                            myPath.set(modFile.getFile().findResource("config"));
                        }
                    });
                });
            }
            else {
                myPath.set(Paths.get(uri));
            }
            Stream<Path> walk = Files.walk(myPath.get(), 1);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();){
                String filename = it.next().getFileName().toString();

                if (!filename.contains(".")) continue;
                System.out.println("load default: /config/" + filename);

                InputStream in = JsonConfig.class.getClassLoader().getResourceAsStream("/config/" + filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                File newFile = new File(target, filename);
                System.out.println("writing to: " + newFile.toString());
                if (!newFile.exists()) newFile.createNewFile();
                FileWriter writer = new FileWriter(newFile);

                reader.lines().forEach(str -> {
                    try {
                        writer.write(str + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.close();


            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonElement loadStats(File statsDataFile) {
        System.out.println("reading " + statsDataFile.toString());

        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(statsDataFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String statsJson = String.join("\n", lines);

        return new JsonParser().parse(statsJson);
    }
}
