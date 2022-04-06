package org.xkmc.biomemanager.init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xkmc.biomemanager.event.GeneralEventHandler;

@Mod(BiomeManager.MODID)
public class BiomeManager {

	public static final String MODID = "biomemanager";
	public static final Logger LOGGER = LogManager.getLogger();

	public BiomeManager() {

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(GeneralEventHandler.class);

	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			//
		});
	}

	public void gatherData(GatherDataEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
	}

}
