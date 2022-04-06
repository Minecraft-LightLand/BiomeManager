package org.xkmc.biomemanager.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.xkmc.biomemanager.util.ExceptionHandler;
import org.xkmc.biomemanager.util.FileIO;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GeneralEventHandler {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public static final Map<ResourceLocation, BiomeConfig> CONFIGS = new HashMap<>();

	public static class BiomeConfig {

		public static final Codec<BiomeConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
				ResourceLocation.CODEC.fieldOf("name").forGetter(e -> e.name),
				Codec.simpleMap(Codec.STRING, Codec.list(MobSpawnInfo.Spawners.CODEC),
								Keyable.forStrings(() -> Arrays.stream(EntityClassification.values()).map(Enum::name)))
						.fieldOf("spawns").forGetter(e -> e.spawns),
				Codec.simpleMap(Codec.STRING, Codec.list(ConfiguredFeature.CODEC),
								Keyable.forStrings(() -> Arrays.stream(GenerationStage.Decoration.values()).map(Enum::name)))
						.fieldOf("decorations").forGetter(e -> e.decorations)
		).apply(i, BiomeConfig::new));

		public ResourceLocation name;
		public Map<String, List<MobSpawnInfo.Spawners>> spawns;
		public Map<String, List<Supplier<ConfiguredFeature<?, ?>>>> decorations;

		public BiomeConfig(ResourceLocation name, Map<String, List<MobSpawnInfo.Spawners>> spawns, Map<String, List<Supplier<ConfiguredFeature<?, ?>>>> decorations) {
			this.name = name;
			this.spawns = spawns;
			this.decorations = decorations;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBiomeRegister(BiomeLoadingEvent event) {
		if (CONFIGS.containsKey(event.getName())) {
			BiomeConfig config = CONFIGS.get(event.getName());
			for (EntityClassification type : EntityClassification.values()) {
				List<MobSpawnInfo.Spawners> list = event.getSpawns().getSpawner(type);
				list.clear();
				list.addAll(config.spawns.get(type.name()));
			}
			for (GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
				List<Supplier<ConfiguredFeature<?, ?>>> list = event.getGeneration().getFeatures(stage);
				list.clear();
				list.addAll(config.decorations.get(stage.name()));
			}
		} else {
			serializeToJson(event.getName(), event);
		}
	}

	private static void serializeToJson(ResourceLocation name, BiomeLoadingEvent event) {
		Map<String, List<MobSpawnInfo.Spawners>> spawns = new HashMap<>();
		for (EntityClassification type : EntityClassification.values()) {
			spawns.put(type.name(), event.getSpawns().getSpawner(type));
		}
		Map<String, List<Supplier<ConfiguredFeature<?, ?>>>> decorations = new HashMap<>();
		for (GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
			decorations.put(stage.name(), event.getGeneration().getFeatures(stage));
		}
		BiomeConfig config = new BiomeConfig(name, spawns, decorations);
		DataResult<JsonElement> result = BiomeConfig.CODEC.encodeStart(JsonOps.INSTANCE, config);
		result.result().ifPresent(e -> {
			File file = FileIO.getFile(name);
			FileIO.checkFile(file);
			ExceptionHandler.run(() -> {
				String str = GSON.toJson(e);
				Writer w = Files.newBufferedWriter(file.toPath());
				w.write(str);
				w.close();
			});
		});
	}

}
