package com.btrack.btrack_app;

public class DistanceCalculator {

    private static BluetoothDevice bDevice;
    private static final int UNKNOWN_DISTANCE = -1;

    public DistanceCalculator(BluetoothDevice device) {
        bDevice = device;
    }

    public double getDistance() {
        if (bDevice.getSignalStrength() == 0) {
            return UNKNOWN_DISTANCE;
        }

        double ratio = bDevice.getSignalStrength() * 1.0 / bDevice.getSignalPower();

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        }

        return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
    }
}
