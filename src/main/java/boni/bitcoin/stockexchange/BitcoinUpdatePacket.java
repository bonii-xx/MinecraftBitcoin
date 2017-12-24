package boni.bitcoin.stockexchange;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import io.netty.buffer.ByteBuf;

public class BitcoinUpdatePacket implements IMessage {

  private int newEnergy;

  public BitcoinUpdatePacket() {
  }

  public BitcoinUpdatePacket(int newEnergy) {
    this.newEnergy = newEnergy;
  }

  public int getNewEnergy() {
    return newEnergy;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    newEnergy = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(newEnergy);
  }
}
