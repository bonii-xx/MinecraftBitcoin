package boni.bitcoin.stockexchange;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import boni.bitcoin.BitCoinMod;

public class BitcoinNetworkHandler implements IMessageHandler<BitcoinUpdatePacket, IMessage> {

  @Override
  public IMessage onMessage(BitcoinUpdatePacket message, MessageContext ctx) {
    // we can do this asynchronous since we only read from the value in drawing and don't care if it's incorrect
    // for one render
    BitCoinMod.bullAndBear.setCurrentValue(message.getNewEnergy());
    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Updated bitcoin to " + message.getNewEnergy()));
    return null;
  }
}
