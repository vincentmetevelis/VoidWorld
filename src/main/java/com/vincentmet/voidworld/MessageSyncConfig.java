package com.vincentmet.voidworld;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class MessageSyncConfig {
    public ResourceLocation spawn_block;

    public MessageSyncConfig(ResourceLocation spawn_block){
        this.spawn_block = spawn_block;
    }

    public static void encode(MessageSyncConfig packet, FriendlyByteBuf buffer){
        buffer.writeUtf(packet.spawn_block.toString());
    }

    public static MessageSyncConfig decode(FriendlyByteBuf buffer) {
        String spawn_block_str = buffer.readUtf();
        if(!ResourceLocation.isValidResourceLocation(spawn_block_str)){
            spawn_block_str = Blocks.GRASS_BLOCK.toString();
        }
        return new MessageSyncConfig(new ResourceLocation(spawn_block_str));
    }

    public static void handle(final MessageSyncConfig message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Config.setSpawnBlock(message.spawn_block);
        });
        ctx.get().setPacketHandled(true);
    }
}