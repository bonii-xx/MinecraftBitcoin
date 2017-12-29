package boni.bitcoin.stockexchange;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import boni.bitcoin.BitCoinMod;

public class BitcoinWorldSaveData extends WorldSavedData {

  private static final String DATA_IDENTIFIER = "bitcoin";
  private static final String KEY = "value";
  private int value;

  public BitcoinWorldSaveData(String name) {
    super(name);
  }

  public BitcoinWorldSaveData() {
    this(DATA_IDENTIFIER);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    value = nbt.getInteger(KEY);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    nbt.setInteger(KEY, value);
    return nbt;
  }

  public static BitcoinWorldSaveData get(World world) {
    MapStorage storage = world.getPerWorldStorage();
    BitcoinWorldSaveData data = (BitcoinWorldSaveData) storage.getOrLoadData(BitcoinWorldSaveData.class, DATA_IDENTIFIER);
    if(data == null) {
      data = new BitcoinWorldSaveData();
      data.setValue(BitCoinMod.bullAndBear.getCurrentValue());
      storage.setData(DATA_IDENTIFIER, data);
    }
    return data;
  }
}
