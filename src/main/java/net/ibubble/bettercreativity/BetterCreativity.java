package net.ibubble.bettercreativity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.ibubble.bettercreativity.api.AbilityHolder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterCreativity implements ModInitializer {
	public static final String MOD_ID = "bettercreativity";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Identifier PING_PACKET = new Identifier(MOD_ID, "ping");
	public static final Identifier PONG_PACKET = new Identifier(MOD_ID, "pong");
	public static final Identifier REQUEST_ABILITY = new Identifier(MOD_ID, "request_ability");
	public static final Identifier GRANT_ABILITY = new Identifier(MOD_ID, "grant_ability");
	public static final Identifier REVOKE_ABILITY = new Identifier(MOD_ID, "revoke_ability");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(PING_PACKET, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> ServerPlayNetworking.send(player, PONG_PACKET, PacketByteBufs.empty()));
		});
		ServerPlayNetworking.registerGlobalReceiver(REQUEST_ABILITY, (server, player, handler, buf, responseSender) -> {
			int seq = buf.readInt();
			boolean delete = buf.readBoolean();
			Ability ability = Ability.ordinalOf(buf.readByte());
			server.execute(() -> {
				if (ability == null || ability.worksOnClient) return;
				if (player.interactionManager.getGameMode().isSurvivalLike()) return;
				AbilityHolder mPlayer = (AbilityHolder) player;
				if (delete) {
					mPlayer.bc$removeAbility(ability);
					PacketByteBuf responseBuf = PacketByteBufs.create();
					responseBuf.writeInt(seq);
					responseBuf.writeByte(ability.ordinal());
					ServerPlayNetworking.send(player, REVOKE_ABILITY, responseBuf);
				} else {
					mPlayer.bc$addAbility(ability);
					PacketByteBuf responseBuf = PacketByteBufs.create();
					responseBuf.writeInt(seq);
					responseBuf.writeByte(ability.ordinal());
					ServerPlayNetworking.send(player, GRANT_ABILITY, responseBuf);
				}
			});
		});
	}
}
