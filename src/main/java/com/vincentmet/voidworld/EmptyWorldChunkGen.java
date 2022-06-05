package com.vincentmet.voidworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EmptyWorldChunkGen extends ChunkGenerator {
    public static final Codec<EmptyWorldChunkGen> CODEC = RecordCodecBuilder.create((instance) -> {
        return commonCodec(instance).and(BiomeSource.CODEC.fieldOf("biome_source").forGetter((gen) -> gen.biomeSource)).apply(instance, instance.stable(EmptyWorldChunkGen::new));
    });

    public EmptyWorldChunkGen(Registry<StructureSet> structureSetRegistry, BiomeSource biomeProvider) {
        super(structureSetRegistry, Optional.empty(), biomeProvider);
        //super(biomeProvider, biomeProvider.new StructureSettings(Optional.empty(), new HashMap<>()));
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long newSeed) {
        return this;
    }

    @Override
    public Climate.Sampler climateSampler() {
        return Climate.empty();
    }

    @Override
    public int getGenDepth() {
        return 512;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return -256;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int y, Heightmap.Types heightmapType, LevelHeightAccessor levelHeightAccessor) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int p_156150_, int p_156151_, LevelHeightAccessor levelHeightAccessor) {
        return new NoiseColumn(0, new BlockState[]{Blocks.AIR.defaultBlockState()});
    }

    @Override
    public void addDebugScreenInfo(List<String> lines, BlockPos pos) {/*NOOP*/}

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {/*NOOP*/}

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {/*NOOP*/}

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long seed, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {/*NOOP*/}

    @Override
    public void createReferences(WorldGenLevel world, StructureFeatureManager structureManager, ChunkAccess chunk) {/*NOOP*/}
}