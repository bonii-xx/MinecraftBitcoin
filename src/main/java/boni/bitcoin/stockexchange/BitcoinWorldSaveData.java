package boni.bitcoin.stockexchange;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import boni.bitcoin.BitCoinMod;

public class BitcoinWorldSaveData extends WorldSavedData {

  private static final String KEY = "value";
  private int value;

  public BitcoinWorldSaveData(String name) {
    super(name);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    value = nbt.getInteger("value");
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    nbt.setInteger(KEY, value);
    return nbt;
  }

  public static BitcoinWorldSaveData get(World world) {
    String identifier = "bitcoin";
    MapStorage storage = world.getPerWorldStorage();
    BitcoinWorldSaveData data = (BitcoinWorldSaveData) storage.getOrLoadData(BitcoinWorldSaveData.class, identifier);
    if(data == null) {
      data = new BitcoinWorldSaveData("bullAndBear");
      data.setValue(BitCoinMod.bullAndBear.getCurrentValue());
      storage.setData(identifier, data);
    }
    return data;
  }
}
