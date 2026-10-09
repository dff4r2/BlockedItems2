package com.blockeditems;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            BlockedConfig cfg = BlockedConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Blocked Items"))
                    .setSavingRunnable(BlockedConfig::save);

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entry = builder.entryBuilder();

            general.addEntry(entry.startBooleanToggle(Component.literal("Enabled"), cfg.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> cfg.enabled = v)
                    .build());

            general.addEntry(entry.startStrList(Component.literal("Blocked items"), new ArrayList<>(cfg.items))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(Component.literal("Item IDs, e.g. minecraft:dirt. Easier: hold the item in game and press B."))
                    .setSaveConsumer(v -> cfg.items = new ArrayList<>(v))
                    .build());

            return builder.build();
        };
    }
}
