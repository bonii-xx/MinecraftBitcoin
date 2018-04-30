package boni.bitcoin;

import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;

public class ThermalExpansion {

  public static void addCompactionRecipe(int energy) {
    CompactorManager.addRecipe(energy, new ItemStack(BitCoinMod.blockChain), new ItemStack(BitCoinMod.bitCoin), CompactorManager.Mode.COIN);
  }

  public static void addBitcoinRecipe(int energy) {
    NumismaticManager.addFuel(new ItemStack(BitCoinMod.bitCoin), energy);
  }

  public static void removeBitcoinRecipe() {
    NumismaticManager.removeFuel(new ItemStack(BitCoinMod.bitCoin));
  }

  public static void updateBitcoinRecipe(int energyNew) {
    removeBitcoinRecipe();
    addBitcoinRecipe(energyNew);
  }
}
