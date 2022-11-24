package com.cache.storage;

import com.cache.api.CacheConfig;
import com.cache.api.CacheObject;
import com.cache.api.CacheObjectInfo;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InternalWithTtlAndPriority extends CacheStorageBase {
  private static final Logger log = LoggerFactory.getLogger(InternalWithTtlAndPriority.class);

  private static final float QUEUE_OVERLOAD = 1.1f;

  private final int maxObjectCount;
  private final int maxItemCount;

  private final AtomicInteger objCount = new AtomicInteger(0);
  private final AtomicInteger itemCount = new AtomicInteger(0);

  private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

  private Map<String, CacheObject> byKey;
  private NavigableMap<Integer, LinkedList<String>> byPriority;

  public InternalWithTtlAndPriority(StorageInitializeParameter p) {
    super(p);
    this.maxObjectCount = p.cacheCfg.getPropertyAsInt(CacheConfig.CACHE_MAX_LOCAL_OBJECTS, CacheConfig.CACHE_MAX_LOCAL_OBJECTS_VALUE);
    //if (maxObjectCount <= 0) throw new IllegalArgumentException("maxSize must be positive");
    this.maxItemCount = p.cacheCfg.getPropertyAsInt(CacheConfig.CACHE_MAX_LOCAL_ITEMS, CacheConfig.CACHE_MAX_LOCAL_ITEMS_VALUE);
    this.byPriority = new TreeMap<>();
    this.byKey = new HashMap<>();
  }

  @Override
  public Optional<CacheObject> getObject(String key) {
    return withReadLock(() -> Optional.ofNullable(byKey.get(key)));
  }

  @Override
  public Optional<CacheObject> setObject(CacheObject o) {
    var itemCount = CacheUtils.itemCount(o);

    withWriteLock(() -> {
      if (!willFit(itemCount)) removeOverLimit(itemCount);

      byKey.put(o.getKey(), o);
      byPriority.computeIfAbsent(o.getPriority(), __ -> new LinkedList<>()).addLast(o.getKey());

      this.itemCount.addAndGet(itemCount);
      objCount.incrementAndGet();
    });

    return Optional.of(o);
  }

  @Override
  public void removeObjectsByKeys(List<String> keys) {
    withWriteLock(() -> {
      var counters = new int[]{0, 0};
      keys.forEach(k -> {
        counters[0] += 1;
        counters[1] += CacheUtils.itemCount(byKey.get(k));
        byKey.remove(k);
      });
      objCount.addAndGet(-counters[0]);
      itemCount.addAndGet(-counters[1]);
    });
  }

  @Override
  public void removeObjectByKey(String key) {
    byKey.remove(key);
  }

  @Override
  public int getObjectsCount() {
    return objCount.get();
  }

  @Override
  public int getItemsCount() {
    return itemCount.get();
  }

  @Override
  public Set<String> getKeys(String containsStr) {
    rwLock.readLock().lock();
    var res = Collections.unmodifiableSet(byKey.keySet());
    rwLock.readLock().unlock();
    return res;
  }

  @Override
  public List<CacheObjectInfo> getValues(String containsStr) {
    return withReadLock(() ->
        byKey.keySet().stream()
            .filter(k -> k.contains(containsStr))
            .map(byKey::get)
            .map(CacheObject::getInfo)
            .collect(Collectors.toUnmodifiableList())
    );
  }

  @Override
  public int clearCache(int clearMode) {
    return withWriteLock(() -> {
      var sum = byKey.size() + byPriority.size();
      byKey = new HashMap<>();
      byPriority = new TreeMap<>();
      return sum;
    });
  }

  @Override
  public int clearCacheContains(String str) {
    return withWriteLock(() ->
        byKey.keySet().stream()
            .filter(k -> k.contains(str))
            .map(k -> {
              byKey.remove(k);
              return 1;
            })
            .reduce(0, Integer::sum)
    );
  }

  @Override
  public void onTimeClean(long checkSeq) {
    log.debug("Running mark and sweep");
    var candidates = withReadLock(() ->
        byKey.values().stream().filter(CacheObject::isOutdated).map(CacheObject::getKey).collect(Collectors.toSet()));
    log.debug("Collected {} candidates for removal", candidates.size());

    candidates.forEach(c -> {
      log.debug("Attempt to remove candidate {}", c);
      withWriteLock(() -> {
        var co = byKey.get(c);
        if (co != null && co.isOutdated()) {
          log.debug("Removing outdated object {}", c);
          removeObjectByKey(co.getKey());
        } else {
          log.debug("Candidate {} not found, or it has been renewed in the meantime", c);
        }
      });
    });
  }

  @Override
  public boolean isInternal() {
    return true;
  }

  private boolean willFit(int itemCount) {
    return objCount.get() < maxObjectCount && (this.itemCount.get() + itemCount) < maxItemCount;
  }

  private void removeOverLimit(int itemCount) {
    withWriteLock(() -> {
      if (itemCount == 1) {
        var keys = byPriority.firstEntry().getValue();
        var keyToRemove = keys.pollLast();
        while (keys.peekLast() != null && keys.peekLast().equals(keyToRemove)) keys.pollLast();
        byKey.remove(keyToRemove);
      } else {
        var keysToRemove = new HashSet<String>();

        var removedSoFar = 0;
        while (removedSoFar < itemCount) {
          var currentEntry = byPriority.firstEntry();
          if (currentEntry == null) break;
          var currentKey = currentEntry.getKey();
          var currentObjects = currentEntry.getValue();
          while (removedSoFar < itemCount) {
            var keyToRemove = currentObjects.pollLast();
            if (keyToRemove == null) {
              byPriority.remove(currentKey);
              break;
            } else {
              var itemsRemoved = CacheUtils.itemCount(byKey.get(keyToRemove));
              removedSoFar += itemsRemoved;
              keysToRemove.add(keyToRemove);
            }
          }
        }
        removeObjectsByKeys(List.copyOf(keysToRemove));
        objCount.addAndGet(-keysToRemove.size());
        this.itemCount.addAndGet(-removedSoFar);
      }
    });
  }

  private <T> T withReadLock(Supplier<T> op) {
    rwLock.readLock().lock();
    var res = op.get();
    rwLock.readLock().unlock();
    return res;
  }

  private void withReadLock(Runnable op) {
    withReadLock(() -> {
      op.run();
      return null;
    });
  }

  private <T> T withWriteLock(Supplier<T> op) {
    rwLock.writeLock().lock();
    var res = op.get();
    rwLock.writeLock().unlock();
    return res;
  }

  private void withWriteLock(Runnable op) {
    withWriteLock(() -> {
      op.run();
      return null;
    });
  }
}