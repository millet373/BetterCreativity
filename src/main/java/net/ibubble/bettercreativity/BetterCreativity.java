package net.ibubble.bettercreativity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterCreativity implements ModInitializer {
	public static final String MOD_ID = "bettercreativity";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Identifier PING_PACKET = new Identifier(MOD_ID, "ping");
	public static final Identifier PONG_PACKET = new Identifier(MOD_ID, "pong");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(PING_PACKET, ((server, player, handler, buf, responseSender) -> {
			ServerPlayNetworking.send(player, PONG_PACKET, PacketByteBufs.empty());
		}));
	}
}
