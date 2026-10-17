package com.iptracker.app.ui.models;

public class IPHistoryItem {
    private final String ipAddress;
    private final String location;
    private final long timestamp;
    private final boolean isValid;
    private final float reliability;

    public IPHistoryItem(String ipAddress, String location, long timestamp,
                         boolean isValid, float reliability) {
        this.ipAddress = ipAddress;
        this.location = location;
        this.timestamp = timestamp;
        this.isValid = isValid;
        this.reliability = reliability;
    }


    public String getIpAddress() { return ipAddress; }
    public String getLocation() { return location; }
    public long getTimestamp() { return timestamp; }
    public boolean isValid() { return isValid; }
    public float getReliability() { return reliability; }

    @Override
    public String toString() {
        return "IPHistoryItem{" +
                "ipAddress='" + ipAddress + '\'' +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                ", isValid=" + isValid +
                ", reliability=" + reliability +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPHistoryItem that = (IPHistoryItem) o;

        if (timestamp != that.timestamp) return false;
        if (isValid != that.isValid) return false;
        if (Float.compare(that.reliability, reliability) != 0) return false;
        if (!ipAddress.equals(that.ipAddress)) return false;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (isValid ? 1 : 0);
        result = 31 * result + (reliability != +0.0f ? Float.floatToIntBits(reliability) : 0);
        return result;
    }
}