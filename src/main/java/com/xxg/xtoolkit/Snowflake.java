package com.xxg.xtoolkit;

public class Snowflake {

    /**
     * configured machine id - 10 bits - gives us up to 1024 machines
     */
    private static final int BITS_MACHINE_ID = 10;

    /**
     * sequence number - 12 bits - rolls over every 4096 per machine (with protection to avoid rollover in the same ms)
     */
    private static final int BITS_SEQUENCE_NUMBER = 12;


    private static final long MAX_MACHINE_ID = (1L << BITS_MACHINE_ID) - 1;
    private static final long MAX_SEQUENCE_NUMBER = (1L << BITS_SEQUENCE_NUMBER) - 1;

    /**
     * Default Custom Epoch (January 1, 2015 Midnight UTC = 2020-01-01T00:00:00Z)
     */
    private static final long DEFAULT_CUSTOM_EPOCH = 1577836800000L;

    private final long machineId;
    private final long customEpoch;

    private long millisecond = -1L;
    private long sequence = 0L;

    /**
     * Create Snowflake with a machineId and custom epoch
     */
    public Snowflake(long machineId, long customEpoch) {
        if(machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(String.format("MachineId must be between %d and %d", 0, MAX_MACHINE_ID));
        }
        this.machineId = machineId;
        this.customEpoch = customEpoch;
    }

    /**
     * Create Snowflake with a nodeId
     */
    public Snowflake(long machineId) {
        this(machineId, DEFAULT_CUSTOM_EPOCH);
    }

    public synchronized long nextId() {
        long currentMilliseconds = currentMilliseconds();

        if (currentMilliseconds < millisecond) {
            throw new IllegalStateException("Invalid System Clock!");
        } else if (currentMilliseconds == millisecond) {
            sequence = (sequence + 1) & MAX_SEQUENCE_NUMBER;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                millisecond = waitNextMillis(currentMilliseconds);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        millisecond = currentMilliseconds;

        long id = currentMilliseconds << (BITS_MACHINE_ID + BITS_SEQUENCE_NUMBER)
                | (machineId << BITS_SEQUENCE_NUMBER)
                | sequence;

        return id;
    }

    /**
     * Get current timestamp in milliseconds, adjust for the custom epoch.
     */
    private long currentMilliseconds() {
        return System.currentTimeMillis() - customEpoch;
    }

    /**
     * Block and wait till next millisecond
     */
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == millisecond) {
            currentTimestamp = currentMilliseconds();
        }
        return currentTimestamp;
    }
}
