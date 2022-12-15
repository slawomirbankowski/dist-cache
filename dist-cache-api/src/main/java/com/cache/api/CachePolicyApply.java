package com.cache.api;

/** apply of single item rule to CacheObject */
public abstract class CachePolicyApply {
    /** apply this rule to CacheObject */
    public abstract void apply(CacheObject co);
}
class CachePolicyApplyEmpty extends CachePolicyApply {
    public CachePolicyApplyEmpty(String value) {
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
    }
}
class CachePolicyApplyPrioritySet extends CachePolicyApply {
    private int value;
    public CachePolicyApplyPrioritySet(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setPriority(value);
    }
}
class CachePolicyApplyPriorityIncrease extends CachePolicyApply {
    private int value;
    public CachePolicyApplyPriorityIncrease(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setPriority(co.getPriority()+value);
    }
}
class CachePolicyApplyPriorityDecrease extends CachePolicyApply {
    private int value;
    public CachePolicyApplyPriorityDecrease(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setPriority(co.getPriority()-value);
    }
}

class CachePolicyApplySizeAdd extends CachePolicyApply {
    private int value;
    public CachePolicyApplySizeAdd(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setSize(co.getSize()+value);
    }
}
class CachePolicyApplySizeMultiply extends CachePolicyApply {
    private int value;
    public CachePolicyApplySizeMultiply(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setSize(co.getSize()*value);
    }
}

class CachePolicyApplyTtlMultiply extends CachePolicyApply {
    private int value;
    public CachePolicyApplyTtlMultiply(String value) {
        this.value = Integer.parseInt(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setTtl((long)(co.getTimeToLive()*value));
    }
}
class CachePolicyApplyTtlDivide extends CachePolicyApply {
    private double value;
    public CachePolicyApplyTtlDivide(String value) {
        this.value = Double.parseDouble(value);
        if (this.value == 0) {
            throw new IllegalArgumentException("TTL divide cannot be zero");
        }
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setTtl((long)(co.getTimeToLive()/value));
    }
}
class CachePolicyApplyTtlAdd extends CachePolicyApply {
    private long value;
    public CachePolicyApplyTtlAdd(String value) {
        this.value = Long.parseLong(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
        co.setTtl((long)(co.getTimeToLive()+value));
    }
}

class CachePolicyApplyMode extends CachePolicyApply {
    private CacheMode.Mode mode;
    public CachePolicyApplyMode(String value) {
        mode = CacheMode.Mode.valueOf(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {

        co.setSize(co.getSize());
    }
}
class CachePolicyApplyStorageInternal extends CachePolicyApply {
    private boolean value;
    public CachePolicyApplyStorageInternal(String value) {
        this.value = Boolean.parseBoolean(value);
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
    }
}
class CachePolicyApplyStorageExternal extends CachePolicyApply {
    public CachePolicyApplyStorageExternal(String value) {
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
    }
}
class CachePolicyApplyStorageSet extends CachePolicyApply {
    public CachePolicyApplyStorageSet(String value) {
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
    }
}
class CachePolicyApplyGroupSet extends CachePolicyApply {

    public CachePolicyApplyGroupSet(String value) {
    }
    /** apply this rule to CacheObject */
    public void apply(CacheObject co) {
    }
}