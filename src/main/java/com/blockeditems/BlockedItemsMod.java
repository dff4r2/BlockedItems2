package com.blockeditems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class BlockedItemsMod implements ClientModInitializer {
    private static KeyMapping toggleItemKey;
    private static KeyMapping toggleModKey;

    @Override
    public void onInitializeClient() {
        BlockedConfig.load();

        toggleItemKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockeditems.toggle_item", GLFW.GLFW_KEY_B, KeyMapping.Category.MISC));
        toggleModKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockeditems.toggle_mod", GLFW.GLFW_KEY_N, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleItemKey.consumeClick()) {
                if (client.player == null) {
                    continue;
                }
                ItemStack held = client.player.getMainHandItem();
                if (held.isEmpty()) {
                    client.player.displayClientMessage(Component.literal("Hold an item to block it"), true);
                    continue;
                }
                boolean nowBlocked = BlockedConfig.get().toggleItem(held);
                client.player.displayClientMessage(
                        Component.literal(nowBlocked ? "Blocked: " : "Unblocked: ").append(held.getHoverName()), true);
            }
            while (toggleModKey.consumeClick()) {
                BlockedConfig cfg = BlockedConfig.get();
                cfg.enabled = !cfg.enabled;
                BlockedConfig.save();
                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.literal("Blocked Items: " + (cfg.enabled ? "ON" : "OFF")), true);
                }
            }
        });
    }
}
