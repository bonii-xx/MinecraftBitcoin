package boni.bitcoin;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {

  @Override
  public void registerModels() {
    registerBlockItem(BitCoinMod.bitCoinOre);
    registerBlockItem(BitCoinMod.bitCoinBlock);
    registerItem(BitCoinMod.blockChain);
    registerItem(BitCoinMod.bitCoin);
  }

  private void registerBlockItem(Block block) {
    registerItem(Item.getItemFromBlock(block));
  }

  private void registerItem(Item item) {
    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
  }
}
