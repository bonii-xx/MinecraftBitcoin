package boni.bitcoin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class BitcoinOreGen implements IWorldGenerator {

  private final WorldGenMinable worldGenMinable;

  public BitcoinOreGen() {
    this.worldGenMinable = new WorldGenMinable(BitCoinMod.bitCoinOre.getDefaultState(), 4);
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    for(int i = 4; i < 129; i *= 2) {
      BlockPos pos = new BlockPos(chunkX * 16, i, chunkZ * 16);
      worldGenMinable.generate(world, random, pos);
    }
  }
}
