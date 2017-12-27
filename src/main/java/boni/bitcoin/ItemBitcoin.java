package boni.bitcoin;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nullable;

public class ItemBitcoin extends Item {

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add(I18n.format("bitcoin.tooltip") + " " + BitCoinMod.bullAndBear.getCurrentValue() + " RF");
  }
}
