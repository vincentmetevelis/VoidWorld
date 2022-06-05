package com.vincentmet.voidworld;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;

@Mod(BaseClass.MODID)
public class BaseClass{
	public static final String MODID = "voidworld";
	public static final Path PATH_CONFIG = FMLPaths.CONFIGDIR.get().resolve("voidworld");

    public BaseClass(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
	}
	
	private void setupCommon(final FMLCommonSetupEvent event){
    	event.enqueueWork(() -> Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(MODID, MODID), EmptyWorldChunkGen.CODEC));
		Config.readConfigToMemory(PATH_CONFIG, "config.json");
		PacketHandler.init();
	}

	private void serverStopping(final ServerStoppingEvent event){
		Config.writeConfigToDisk(PATH_CONFIG, "config.json");
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
	public static class ModEventHandler{
		@SubscribeEvent
		public static void registerWorldType(RegistryEvent.Register<ForgeWorldPreset> event){
			event.getRegistry().register(new EmptyWorldType());
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.FORGE)
	public static class ForgeEventHandler{
    	@SubscribeEvent
		public static void onWorldLoad(WorldEvent.Load event){
    		if(event.getWorld() instanceof ServerLevel serverWorld && event.getWorld().dimensionType().effectsLocation().equals(DimensionType.OVERWORLD_EFFECTS)){
				try{
					ParseResults<CommandSourceStack> pr = serverWorld.getServer().getCommands().getDispatcher().parse("gamerule spawnRadius 0", serverWorld.getServer().createCommandSourceStack().withSuppressedOutput());
					serverWorld.getServer().getCommands().getDispatcher().execute(pr);
				}catch(CommandSyntaxException ignored){}
			}
		}

		@SubscribeEvent
		public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
			if(event.getPlayer() instanceof ServerPlayer player){
				if(player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0){
					BlockPos pos = player.blockPosition().below(1);
					for(int x = -Config.SidedConfig.getSpawnBlockRadius(); x <= Config.SidedConfig.getSpawnBlockRadius(); x++){
						for(int z = -Config.SidedConfig.getSpawnBlockRadius(); z <= Config.SidedConfig.getSpawnBlockRadius(); z++){
							if(player.getLevel().getBlockState(pos.offset(x, 0, z)).getBlock().equals(Blocks.AIR)){
								player.getLevel().setBlock(pos.offset(x, 0, z), Config.SidedConfig.getSpawnBlock().defaultBlockState(), 2);
							}
						}
					}
				}
			}
		}
	}

	public static class EmptyWorldType extends ForgeWorldPreset {
		public EmptyWorldType() {
			super((registryAccess, seed, generatorSettings) -> new EmptyWorldChunkGen(registryAccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), new FixedBiomeSource(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY).getOrCreateHolder(Biomes.PLAINS))));
			setRegistryName(new ResourceLocation(BaseClass.MODID, BaseClass.MODID));
		}

		@Override
		public String getTranslationKey() {
			return BaseClass.MODID + ".empty_world_type";
		}
	}
}