package net.ibubble.bettercreativity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.ibubble.bettercreativity.api.AbilityHolder;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class BetterCreativityInteractionManager {
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<Integer, Runnable> onSuccessHandlers = new HashMap<>();

    BetterCreativityInteractionManager() {
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
    }

    public void requestAbility(Ability ability, Runnable onSuccess) {
        int seq = this.seq.getAndUpdate(n -> n + 1);
        onSuccessHandlers.put(seq, onSuccess);
        sendRequest(seq, ability, false);
    }

    public void deleteAbility(Ability ability, Runnable onSuccess) {
        int seq = this.seq.getAndUpdate(n -> n + 1);
        onSuccessHandlers.put(seq, onSuccess);
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
        }
    }
}
