package boni.bitcoin.stockexchange;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;

import boni.bitcoin.BitCoinMod;

@Mod.EventBusSubscriber
public class BullAndBear {

  private static final Random RANDOM = new Random();
  private final static int UPDATE_RATE = 20 * 3; // every 3 seconds
  private final static int LOCAL_UPDATE_RATE = 20 * 60 * 3; // every 3 minutes

  // configuration
  private final int minValue;
  private final int maxValue;
  private final int localVariance;
  private final Consumer<Integer> updateMethod;

  // keeping track when the next change should be
  // we track the next instead of the last one so we can add some variance easier
  private long nextTick;
  private long nextLocalTick;

  private int currentValue;
  private int currentValueStep;
  private int nextLocalValue;

  // the longer term target, which the local target fluctuates around.
  private Queue<Integer> nextTargetValues = new LinkedList<>();
  private BitcoinWorldSaveData data = null;

  public BullAndBear(int minValue, int maxValue, Consumer<Integer> updateMethod) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.updateMethod = updateMethod;
    this.localVariance = (maxValue - minValue) / 2;
    init(minValue + (maxValue - minValue) / 2, 0);
  }

  public void init(int value, long currentTime) {
    this.currentValue = value;
    this.nextLocalValue = currentValue;
    this.nextTick = currentTime + UPDATE_RATE;
    this.nextLocalTick = currentTime + UPDATE_RATE;
    this.currentValueStep = 0;

    this.nextTargetValues.clear();
    calculateNextTargetValues();
    calculateNextLocalValue(currentTime);
  }

  public void onServerStarting(FMLServerStartingEvent serverStartingEvent) {
    World world = serverStartingEvent.getServer().getEntityWorld();
    data = BitcoinWorldSaveData.get(world);
    init(data.getValue(), world.getTotalWorldTime());
  }

  public void OnServerStopped(FMLServerStoppedEvent serverStoppedEvent) {
    data = null;
  }

  @SubscribeEvent
  public void onWorldTick(TickEvent.WorldTickEvent worldTickEvent) {
    if(worldTickEvent.phase == TickEvent.Phase.END && !worldTickEvent.world.isRemote && data != null) {
      long currentTime = worldTickEvent.world.getTotalWorldTime();
      modifyValue(currentTime);
    }
  }

  public int getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(int currentValue) {
    this.currentValue = currentValue;
    updateMethod.accept(currentValue);
  }

  private void modifyValue(long currentTime) {
    if(nextTick < currentTime) {
      if(nextLocalTick < currentTime) {
        calculateNextLocalValue(currentTime);
      }
      else {
        updateCurrentValue(currentTime);
      }
      BitCoinMod.bitcoinNetwork.sendBitcoinUpdate();
      data.setValue(currentValue);
      data.setDirty(true);
    }
  }

  // Debug
  public void simulateCurve(int amountOfGlobalStepsToSimulate) {
    int globalStepCount = 0;
    long clock = 0;
    // initialize
    modifyValue(clock);

    int lastLocalCount = nextTargetValues.size();
    int lastCurrentValue = currentValue;

    while(globalStepCount < amountOfGlobalStepsToSimulate) {
      clock++;
      modifyValue(clock);

      if(nextTargetValues.size() > lastLocalCount) {
        globalStepCount++;
      }
      lastLocalCount = nextTargetValues.size();

      if(currentValue != lastCurrentValue) {
        System.out.println("" + clock + "\t" + currentValue);
        lastCurrentValue = currentValue;
      }
    }
  }

  private void updateCurrentValue(long currentTime) {
    int newValue = currentValue + currentValueStep;
    // continous updates are linear
    nextTick = currentTime + UPDATE_RATE;

    this.setCurrentValue(newValue);
  }

  private void calculateNextLocalValue(long currentTime) {
    this.setCurrentValue(nextLocalValue);

    int targetValue = nextTargetValues.poll();
    // local updates have 20% variance in time
    int variance = LOCAL_UPDATE_RATE / 5;
    nextLocalTick = currentTime + LOCAL_UPDATE_RATE + getMeanRandom(variance);
    // add some spikyness
    nextLocalValue = targetValue + getMeanRandom(localVariance);
    long timeTillNextLocalTick = nextLocalTick - currentTime;
    currentValueStep = (int) ((nextLocalValue - currentValue) / (timeTillNextLocalTick / UPDATE_RATE));

    if(nextTargetValues.isEmpty()) {
      calculateNextTargetValues();
    }

  }

  private void calculateNextTargetValues() {
    final double growthFactor = 1d / 2d;
    double stepValue = currentValue;
    int nextGlobalValue = minValue + RANDOM.nextInt(maxValue - minValue);
    int steps = 2 + RANDOM.nextInt(4);

    for(int i = 0; i < steps; i++) {
      double dif = nextGlobalValue - stepValue;
      double nextValue = stepValue + dif * growthFactor;
      nextTargetValues.add(Math.round((float) nextValue));
      stepValue = nextValue;
    }
    nextTargetValues.add(nextGlobalValue);
  }

  private int getMeanRandom(int variance) {
    return RANDOM.nextInt(variance) - variance / 2;
  }
}
