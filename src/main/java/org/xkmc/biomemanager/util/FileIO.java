package org.xkmc.biomemanager.util;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.xkmc.biomemanager.init.BiomeManager;

import java.io.File;

public class FileIO {

	public static File getFile(ResourceLocation name) {
		String s = File.separator;
		String dirpath = FMLPaths.CONFIGDIR.get().toString();
		String filepath = BiomeManager.MODID + s + name.getNamespace() + s + name.getPath() + ".json";
		return new File(dirpath + s + filepath);
	}

	public static void checkFile(File file) {
		if (!file.exists()) {
			ExceptionHandler.run(() -> {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				file.createNewFile();
			});
		}
	}

}