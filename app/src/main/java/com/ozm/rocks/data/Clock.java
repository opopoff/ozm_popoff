package com.ozm.rocks.data;

public interface Clock {
    long millis();

    long nanos();

    long unixTime();

    Clock REAL = new Clock() {
        @Override
        public long millis() {
            return System.currentTimeMillis();
        }

        @Override
        public long nanos() {
            return System.nanoTime();
        }

        @Override
        public long unixTime() {
            return millis() / 1000L;
        }
    };
}
