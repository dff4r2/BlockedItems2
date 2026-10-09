package com.blockeditems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BlockedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("blockeditems.json");
    private static BlockedConfig instance = new BlockedConfig();

    public boolean enabled = true;
    public List<String> items = new ArrayList<>();

    private transient volatile Set<String> lookup = new HashSet<>();

    public static BlockedConfig get() {
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                BlockedConfig loaded = GSON.fromJson(Files.readString(FILE), BlockedConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            }
        } catch (Exception e) {
            System.err.println("[BlockedItems] Failed to read config: " + e);
        }
        if (instance.items == null) {
            instance.items = new ArrayList<>();
        }
        instance.rebuild();
        save();
    }

    public static void save() {
        instance.rebuild();
        try {
            Files.writeString(FILE, GSON.toJson(instance));
        } catch (IOException e) {
            System.err.println("[BlockedItems] Failed to write config: " + e);
        }
    }

    private static String normalize(String id) {
        String s = id.trim().toLowerCase(Locale.ROOT);
        return s.contains(":") ? s : "minecraft:" + s;
    }

    private void rebuild() {
        Set<String> set = new HashSet<>();
        for (String s : items) {
            if (s != null && !s.isBlank()) {
                set.add(normalize(s));
            }
        }
        lookup = set;
    }

    public static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    public boolean isBlocked(ItemStack stack) {
        if (!enabled || stack.isEmpty()) {
            return false;
        }
        Set<String> current = lookup;
        return !current.isEmpty() && current.contains(idOf(stack));
    }

    /** يرجع true إذا صار الايتم ممنوع، false إذا انشال من القائمة */
    public boolean toggleItem(ItemStack stack) {
        String id = idOf(stack);
        boolean removed = items.removeIf(s -> s != null && normalize(s).equals(id));
        if (!removed) {
            items.add(id);
        }
        save();
        return !removed;
    }
}
