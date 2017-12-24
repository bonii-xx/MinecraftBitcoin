package boni.bitcoin;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThermalExpansion {

  private final Method addCompactorRecipe;
  private final Object compactorMintMode;
  private final Method addNumismaticRecipe;
  private final Method removeNumismaticRecipe;

  public ThermalExpansion() {
    try {
      Class<?> compactorManager = Class.forName("cofh.thermalexpansion.util.managers.machine.CompactorManager");
      Class<?> compactorMode = Class.forName("cofh.thermalexpansion.util.managers.machine.CompactorManager$Mode");

      compactorMintMode = compactorMode.getEnumConstants()[2];
      addCompactorRecipe = compactorManager.getDeclaredMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, compactorMode);


      Class<?> numismaticManager = Class.forName("cofh.thermalexpansion.util.managers.dynamo.NumismaticManager");
      addNumismaticRecipe = numismaticManager.getDeclaredMethod("addFuel", ItemStack.class, int.class);
      removeNumismaticRecipe = numismaticManager.getDeclaredMethod("removeFuel", ItemStack.class);
    } catch(NoSuchMethodException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void addCompactionRecipe(int energy) {
    try {
      addCompactorRecipe.invoke(null, energy, new ItemStack(BitCoinMod.blockChain), new ItemStack(BitCoinMod.bitCoin), compactorMintMode);
    } catch(IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public void addBitcoinRecipe(int energy) {
    try {
      addNumismaticRecipe.invoke(null, new ItemStack(BitCoinMod.bitCoin), energy);
    } catch(IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public void removeBitcoinRecipe() {
    try {
      removeNumismaticRecipe.invoke(null, new ItemStack(BitCoinMod.bitCoin));
    } catch(IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateBitcoinRecipe(int energyNew) {
    removeBitcoinRecipe();
    addBitcoinRecipe(energyNew);
  }
}
