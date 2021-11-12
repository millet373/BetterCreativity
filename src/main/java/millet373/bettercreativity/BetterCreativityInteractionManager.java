package millet373.bettercreativity;

import millet373.bettercreativity.api.AbilityHolder;
import millet373.bettercreativity.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import millet373.bettercreativity.config.ConfigObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class BetterCreativityInteractionManager {
    private final MinecraftClient client;
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<Integer, Runnable> onSuccessHandlers = new HashMap<>();

    BetterCreativityInteractionManager() {
        client = MinecraftClient.getInstance();
    }

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
            if (client.player == null || client.interactionManager == null) return;
            if (client.interactionManager.getCurrentGameMode().isSurvivalLike()) return;
            AbilityHolder player = (AbilityHolder) client.player;
            for (Ability ability : Ability.values()) {
                KeyBinding keyBinding = config.getAbilityKeyBinding(ability);
                if (keyBinding.isUnbound()) continue;
                while (keyBinding.wasPressed()) {
                    if (BetterCreativityClient.isClientMode() && !ability.worksOnClient) break;
                    if (player.bc$hasAbility(ability)) {
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
        assert client.player != null && client.interactionManager != null;
        if (ability.worksOnClient) {
            if (client.interactionManager.getCurrentGameMode().isSurvivalLike()) return;
            ((AbilityHolder) client.player).bc$addAbility(ability);
            if (onSuccess != null) onSuccess.run();
            return;
        } else if (BetterCreativityClient.isClientMode()) {
            return;
        }
        int seq = this.seq.getAndUpdate(n -> n + 1);
        if (onSuccess != null) onSuccessHandlers.put(seq, onSuccess);
        sendRequest(seq, ability, false);
    }

    public void deleteAbility(Ability ability) {
        deleteAbility(ability, null);
    }

    public void deleteAbility(Ability ability, Runnable onSuccess) {
        assert client.player != null && client.interactionManager != null;
        if (ability.worksOnClient) {
            if (client.interactionManager.getCurrentGameMode().isSurvivalLike()) return;
            ((AbilityHolder) client.player).bc$removeAbility(ability);
            if (onSuccess != null) onSuccess.run();
            return;
        }
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
