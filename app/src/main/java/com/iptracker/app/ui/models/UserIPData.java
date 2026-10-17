package com.iptracker.app.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserIPData implements Parcelable {
    private final String ipAddress;
    private final String userName;
    private final long timeStamp;

    public UserIPData(String ipAddress, String userName, long timeStamp) {
        this.ipAddress = ipAddress != null ? ipAddress : "";
        this.userName = userName != null ? userName : "";
        this.timeStamp = timeStamp;
    }

    protected UserIPData(Parcel in) {
        ipAddress = in.readString();
        userName = in.readString();
        timeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ipAddress);
        dest.writeString(userName);
        dest.writeLong(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserIPData> CREATOR = new Creator<UserIPData>() {
        @Override
        public UserIPData createFromParcel(Parcel in) {
            return new UserIPData(in);
        }

        @Override
        public UserIPData[] newArray(int size) {
            return new UserIPData[size];
        }
    };


    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
