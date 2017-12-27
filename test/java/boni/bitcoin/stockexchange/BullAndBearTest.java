package boni.bitcoin.stockexchange;

import org.junit.Test;

public class BullAndBearTest {

  @Test
  public void testCurve() {
    BullAndBear bullAndBear = new BullAndBear(200000, 500000, i -> {});
    bullAndBear.simulateCurve(10);
  }
}