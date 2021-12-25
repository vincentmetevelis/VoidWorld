package com.vincentmet.voidworld;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static int messageID = 0;
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(BaseClass.MODID, "network"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static int nextID() {
        return messageID++;
    }

    public static void init() {
        CHANNEL.registerMessage(nextID(), MessageSyncConfig.class, MessageSyncConfig::encode, MessageSyncConfig::decode, MessageSyncConfig::handle);
    }
}