package net.ibubble.bettercreativity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.ibubble.bettercreativity.api.AbilityHolder;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class BetterCreativityInteractionManager {
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<Integer, Runnable> onSuccessHandlers = new HashMap<>();

    BetterCreativityInteractionManager() {}

    public void init() {
        ConfigObject config = ConfigManager.getInstance().getConfig();
        ClientPlayNetworking.registerGlobalReceiver(BetterCreativity.GRANT_ABILITY, (client, handler, buf, responseSender) -> {
            int seq = buf.readInt();
            Ability ability = Ability.ordinalOf(buf.readByte());
            client.execute(() -> {
                assert client.player != null;
                AbilityHolder player = (AbilityHolder) client.player;
                player.bc$addAbility(ability);
                this.onSuccess(seq);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(BetterCreativity.REVOKE_ABILITY, (client, handler, buf, responseSender) -> {
            int seq = buf.readInt();
            Ability ability = Ability.ordinalOf(buf.readByte());
            client.execute(() -> {
                assert client.player != null;
                AbilityHolder player = (AbilityHolder) client.player;
                player.bc$removeAbility(ability);
                this.onSuccess(seq);
            });
        });
        for (Ability ability : Ability.values()) {
            KeyBinding keyBinding = config.getAbilityKeyBinding(ability);
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.interactionManager == null) return;
            if (client.interactionManager.getCurrentGameMode().isSurvivalLike()) return;
            for (Ability ability : Ability.values()) {
                KeyBinding keyBinding = config.getAbilityKeyBinding(ability);
                if (keyBinding.isUnbound()) continue;
                while (keyBinding.wasPressed()) {
                    if (BetterCreativityClient.isClientMode() && !ability.client) break;
                    if (ability.isEnabled(client)) {
                        deleteAbility(ability);
                    } else {
                        requestAbility(ability);
                    }
                }
            }
        });
    }

    public void requestAbility(Ability ability) {
        requestAbility(ability, null);
    }

    public void requestAbility(Ability ability, Runnable onSuccess) {
        int seq = this.seq.getAndUpdate(n -> n + 1);
        if (onSuccess != null) onSuccessHandlers.put(seq, onSuccess);
        sendRequest(seq, ability, false);
    }

    public void deleteAbility(Ability ability) {
        deleteAbility(ability, null);
    }

    public void deleteAbility(Ability ability, Runnable onSuccess) {
        int seq = this.seq.getAndUpdate(n -> n + 1);
        if (onSuccess != null) onSuccessHandlers.put(seq, onSuccess);
        sendRequest(seq, ability, true);
    }

    private void sendRequest(int seq, Ability ability, boolean delete) {
        PacketByteBuf requestBuf = PacketByteBufs.create();
        requestBuf.writeInt(seq);
        requestBuf.writeBoolean(delete);
        requestBuf.writeByte(ability.ordinal());
        ClientPlayNetworking.send(BetterCreativity.REQUEST_ABILITY, requestBuf);
    }

    private void onSuccess(int seq) {
        Runnable runnable = onSuccessHandlers.get(seq);
        if (runnable != null) {
            runnable.run();
            onSuccessHandlers.put(seq, null);
        }
    }
}
