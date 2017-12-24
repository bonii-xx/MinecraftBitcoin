package boni.bitcoin.stockexchange;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import boni.bitcoin.BitCoinMod;

public class BitcoinNetwork {
  private SimpleNetworkWrapper network;

  public BitcoinNetwork() {
    network = NetworkRegistry.INSTANCE.newSimpleChannel(BitCoinMod.MOD_ID);
    network.registerMessage(BitcoinNetworkHandler.class, BitcoinUpdatePacket.class, 1, Side.CLIENT);
  }

  public void sendBitcoinUpdate() {
    network.sendToAll(new BitcoinUpdatePacket(BitCoinMod.bullAndBear.getCurrentValue()));
  }
}
