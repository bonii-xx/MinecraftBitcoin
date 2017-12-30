package boni.bitcoin;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.util.Loader;

import java.io.File;

import cofh.core.gui.CreativeTabCore;
import cofh.core.init.CoreProps;
import cofh.thermalfoundation.ThermalFoundation;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemWrench;

/** Cofh Worldgen integration, see WorldProps and TFProps */
public class BitcoinProps {

  private BitcoinProps() {

  }

  public static void preInit() {
    addWorldGeneration();
  }

  private static void addWorldGeneration() {

    File worldGenFile;
    String worldGenPath = "assets/" + BitCoinMod.MOD_ID + "/world/";

    String worldGenBitcoin = "101_bitcoin_ores.json";

    worldGenFile = new File(CoreProps.configDir, "/cofh/world/" + worldGenBitcoin);
    if (!worldGenFile.exists()) {
      try {
        worldGenFile.createNewFile();
        FileUtils.copyInputStreamToFile(Loader.getResource(worldGenPath + worldGenBitcoin, null).openStream(), worldGenFile);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
}
