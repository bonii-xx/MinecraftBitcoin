package boni.bitcoin.stockexchange;

import net.minecraftforge.fml.common.Mod;
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
  private final static int UPDATE_RATE = 20*3; // every 3 seconds
  private final static int LOCAL_UPDATE_RATE = 20*60*3; // every 3 minutes

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

  public BullAndBear(int minValue, int maxValue, Consumer<Integer> updateMethod) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.updateMethod = updateMethod;
    this.localVariance = (maxValue - minValue)/2;
    this.currentValue = minValue + (maxValue-minValue)/2;

    calculateNextTargetValues();
    this.nextLocalValue = currentValue;
    calculateNextLocalValue(0);
  }

  @SubscribeEvent
  public void onWorldTick(TickEvent.WorldTickEvent worldTickEvent) {
    if(worldTickEvent.phase == TickEvent.Phase.END && !worldTickEvent.world.isRemote) {
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
      } else {
        updateCurrentValue(currentTime);
      }
      BitCoinMod.bitcoinNetwork.sendBitcoinUpdate();
    }
  }

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
        System.out.println(""+clock+"\t"+currentValue);
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
    int variance = LOCAL_UPDATE_RATE/5;
    nextLocalTick = currentTime + LOCAL_UPDATE_RATE + getMeanRandom(variance);
    // add some spikyness
    nextLocalValue = targetValue + getMeanRandom(localVariance);
    long timeTillNextLocalTick = nextLocalTick - currentTime;
    currentValueStep = (int)((nextLocalValue - currentValue) / (timeTillNextLocalTick/ UPDATE_RATE));

    if(nextTargetValues.isEmpty()) {
      calculateNextTargetValues();
    }

  }

  private void calculateNextTargetValues() {
    final double growthFactor = 1d/2d;
    double stepValue = currentValue;
    int nextGlobalValue = minValue + RANDOM.nextInt(maxValue - minValue);
    int steps = 4 + RANDOM.nextInt(2);

    for(int i = 0; i < steps; i++) {
      double dif = nextGlobalValue - stepValue;
      double nextValue = stepValue + dif * growthFactor;
      nextTargetValues.add(Math.round((float)nextValue));
      stepValue = nextValue;
    }
    nextTargetValues.add(nextGlobalValue);
  }

  private int getMeanRandom(int variance) {
    return RANDOM.nextInt(variance) - variance/2;
  }
}
