package com.btrack.btrack_app;

/**
 * Created by alexandru.panturu on 9/9/17.
 */

public class BluetoothDevice {
    protected String name;
    protected String address;
    protected Integer state;
    protected String uuid;
    protected Integer signalStrength;
    protected Integer signalPower;
    protected Integer distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Integer getSignalPower() {
        return signalPower;
    }

    public void setSignalPower(Integer signalPower) {
        this.signalPower = signalPower;
    }

    public double getDistance() {
        DistanceCalculator distanceCalculator = new DistanceCalculator(this);

        return distanceCalculator.getDistance();
    }

    public String getDistanceZone() {
        double accuracy = getDistance();

        if (accuracy == -1.0) {
            return "Unknown";
        } else if (accuracy < 1) {
            return "Immediate";
        } else if (accuracy < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }
}
