package com.cache.util.measure;

import com.cache.util.ESupplier;

import java.util.Map;

public class Measure {
  public static <T> TimedResult<T> timed(ESupplier<T> op) throws Exception {
    var t0 = System.nanoTime();
    var res = op.get();
    var diff = System.nanoTime() - t0;
    return new TimedResult<>(res, diff);
  }

  public static <T> TimedResult<T> timedUnsafe(ESupplier<T> op) {
    try {
      return timed(op);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
