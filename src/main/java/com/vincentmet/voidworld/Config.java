package com.vincentmet.voidworld;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static class ServerConfig{
        public static Block SPAWN_BLOCK = Blocks.GRASS_BLOCK;
        public static int SPAWN_BLOCK_RADIUS = 0;
    }

    public static class ServerToClientSyncedConfig{
        public static Block SPAWN_BLOCK = Blocks.GRASS_BLOCK;
        public static int SPAWN_BLOCK_RADIUS = 0;
    }

    public static void processJson(JsonObject json){
        if(json.has("spawn_block")){
            JsonElement jsonElement = json.get("spawn_block");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    setSpawnBlock(ResourceLocation.tryParse(jsonPrimitive.getAsString()));
                }
            }
        }
        if(json.has("spawn_block_radius")){
            JsonElement jsonElement = json.get("spawn_block_radius");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isNumber()){
                    setSpawnBlockRadius(jsonPrimitive.getAsNumber().intValue());
                }
            }
        }
    }

    public static void setSpawnBlock(ResourceLocation resourceLocation){
        if(resourceLocation != null){
            if(ForgeRegistries.BLOCKS.containsKey(resourceLocation)){
                ServerConfig.SPAWN_BLOCK = ForgeRegistries.BLOCKS.getValue(resourceLocation);
            }else{
                ServerConfig.SPAWN_BLOCK = Blocks.GRASS_BLOCK;
            }
        }else{
            ServerConfig.SPAWN_BLOCK = Blocks.GRASS_BLOCK;
        }
    }

    public static void setSpawnBlockRadius(int spawnBlockRadius){
        ServerConfig.SPAWN_BLOCK_RADIUS = Math.max(spawnBlockRadius, 0);
    }

    public static class SidedConfig{
        public static Block getSpawnBlock(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.SPAWN_BLOCK : ServerConfig.SPAWN_BLOCK;
        }

        public static int getSpawnBlockRadius(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.SPAWN_BLOCK_RADIUS : ServerConfig.SPAWN_BLOCK_RADIUS;
        }
    }

    public static JsonObject getJson(){
        JsonObject json = new JsonObject();
        json.addProperty("spawn_block", ServerConfig.SPAWN_BLOCK.getRegistryName().toString());
        json.addProperty("spawn_block_radius", ServerConfig.SPAWN_BLOCK_RADIUS);
        return json;
    }

    public static void readConfigToMemory(Path path, String file){
        processJson(loadConfig(path, file));
        writeConfigToDisk(path, file);
    }

    public static void writeConfigToDisk(Path path, String file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String out = gson.toJson(getJson());
        writeTo(path, file, out);
    }

    private static JsonObject loadConfig(Path path, String filename){
        try {
            StringBuilder res = new StringBuilder();
            Files.readAllLines(path.resolve(filename), StandardCharsets.UTF_8).forEach(res::append);
            return JsonParser.parseString(res.toString()).getAsJsonObject();
        }catch (IOException e) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String out = gson.toJson(new JsonObject());
            writeTo(path, filename, out);
            return loadConfig(path, filename);
        }
    }

    private static void writeTo(Path location, String filename, Object text){
        try{
            if(!location.toFile().exists()){
                location.toFile().mkdirs();
            }
            Files.write(location.resolve(filename), text.toString().getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}