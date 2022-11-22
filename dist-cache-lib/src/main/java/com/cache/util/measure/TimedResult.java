package com.cache.util.measure;

public class TimedResult<T> {
  private final T result;
  private final long elapsedNs;

  public TimedResult(T result, long elapsedNs) {
    this.result = result;
    this.elapsedNs = elapsedNs;
  }

  public T getResult() {
    return result;
  }

  public long getElapsedNs() {
    return elapsedNs;
  }

  public long getElapsedMs() {
    return elapsedNs / 1_000_000;
  }

  @Override
  public String toString() {
    return "TimedResult{" +
        "result=" + result +
        ", elapsedNs=" + elapsedNs +
        '}';
  }
}
