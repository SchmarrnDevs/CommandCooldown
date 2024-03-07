package dev.schmarrn.command_cooldown;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CooldownManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("CommandCooldown");
    private static final String PATH = FabricLoader.getInstance().getConfigDir().toString() + "/command_cooldown.config";

    private static final Map<String, Integer> configuredCooldowns = new HashMap<>();
    private static final Map<String, Map<String, Integer>> uuidCD = new HashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        File config = new File(PATH);
        if (!config.exists()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PATH));

            AtomicInteger line_number = new AtomicInteger();
            reader.lines().forEach(line -> {
                line_number.incrementAndGet();
                var splitted = line.split(":"); if (splitted.length == 2) {
                    String command = splitted[0].trim();
                    int cooldown = Integer.parseInt(splitted[1].trim());
                    configuredCooldowns.put(command, cooldown);
                } else {
                    LOGGER.warn("Invalid line at command_cooldown.config line {}: {}", line_number, line);
                }
            });
        } catch (FileNotFoundException e) {
            // shouldn't happen
            return;
        }
    }

    public static int getCooldown(String uuid, String command) {
        load();
        return uuidCD.getOrDefault(uuid, new HashMap<>()).getOrDefault(command, 0);
    }

    public static void activateCooldown(String uuid, String command) {
        load();
        uuidCD.putIfAbsent(uuid, new HashMap<>());
        uuidCD.get(uuid).put(command, configuredCooldowns.getOrDefault(command, 0));
    }

    public static void tick() {
        uuidCD.values().forEach(map -> map.replaceAll((key, value) -> value > 0 ? value - 1 : 0));
    }
}
