package boni.bitcoin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod(modid = BitCoinMod.MOD_ID,
    name = "BitCoin",
    version = BitCoinMod.modVersion,
    dependencies = "required-after:thermalexpansion",
    acceptedMinecraftVersions = "[1.12, 1.13)")
@Mod.EventBusSubscriber
public class BitCoinMod {
  public static final String MOD_ID = "bitcoin";
  public static final String modVersion = "${version}";

  private static final int BITCOIN_MINT_ENERGY = 10000;
  private static final int BITCOIN_ENERGY_OUTPUT = 500000;

  static final Block bitCoinOre = getBitcoinOre();
  static final Block bitCoinBlock = getBitcoinBlock();
  static final Item blockChain = getBlockChain();
  static final Item bitCoin = getBitcoinItem();

  @SidedProxy(clientSide = "boni.bitcoin.ClientProxy", serverSide = "boni.bitcoin.CommonProxy")
  private static CommonProxy proxy;

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

    // melt ore into block
    GameRegistry.addSmelting(bitCoinOre, new ItemStack(bitCoinBlock), 0);
    // craft block into blockchain - recipe json
    // compact blockchain into bitcoin
    registerCompactionRecipes();
    // burn bitcoin for energy
    registerNumismaticDynamoFuel();

    registerWorldgen();
    registerOredict();
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().register(bitCoinOre);
    event.getRegistry().register(bitCoinBlock);
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(new ItemBlock(bitCoinOre).setRegistryName(bitCoinOre.getRegistryName()));
    event.getRegistry().register(new ItemBlock(bitCoinBlock).setRegistryName(bitCoinBlock.getRegistryName()));
    event.getRegistry().register(blockChain);
    event.getRegistry().register(bitCoin);
  }

  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  private void registerWorldgen() {
    GameRegistry.registerWorldGenerator(new BitcoinOreGen(), 10);
  }

  private void registerOredict() {
    OreDictionary.registerOre("oreBitcoin", bitCoinOre);
    OreDictionary.registerOre("blockBitcoin", bitCoinBlock);
    OreDictionary.registerOre("bitcoin", bitCoin);
  }

  private void registerCompactionRecipes()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<?> compactorManager = Class.forName("cofh.thermalexpansion.util.managers.machine.CompactorManager");
    Class<?> compactorMode = Class.forName("cofh.thermalexpansion.util.managers.machine.CompactorManager$Mode");

    Method method = compactorManager.getDeclaredMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, compactorMode);

    method.invoke(null, BITCOIN_MINT_ENERGY, new ItemStack(blockChain), new ItemStack(bitCoin), compactorMode.getEnumConstants()[2]);
  }

  private void registerNumismaticDynamoFuel()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<?> numismaticManager = Class.forName("cofh.thermalexpansion.util.managers.dynamo.NumismaticManager");
    Method method = numismaticManager.getDeclaredMethod("addFuel", ItemStack.class, int.class);

    method.invoke(null, new ItemStack(bitCoin), BITCOIN_ENERGY_OUTPUT);
  }

  private static Block getBitcoinOre() {
    Block block = new BlockOre();
    block.setRegistryName("bitcoin_ore");
    block.setUnlocalizedName("oreBitcoin");
    block.setHardness(4);
    block.setResistance(6);
    block.setHarvestLevel("pickaxe", 2);
    return block;
  }

  private static Block getBitcoinBlock() {
    Block block = new Block(Material.IRON);
    block.setRegistryName("bitcoin_block");
    block.setUnlocalizedName("blockBitcoin");
    block.setHardness(4);
    block.setResistance(6);
    return block;
  }

  private static Item getBlockChain() {
    Item item = new Item();
    item.setRegistryName("block_chain");
    item.setUnlocalizedName("blockchain");
    return item;
  }

  private static Item getBitcoinItem() {
    Item item = new Item();
    item.setRegistryName("bitcoin");
    item.setUnlocalizedName("bitcoin");
    return item;
  }
}
