package com.iptracker.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "ip_searches",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")})
public class IpSearch {
    @PrimaryKey(autoGenerate = true)
    private long searchId;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "ipAddress")
    @NonNull
    private String ipAddress;

    @ColumnInfo(name = "country")
    private String country;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "searchTimestamp")
    private long searchTimestamp;

    @ColumnInfo(name = "isValid")
    private boolean isValid;

    public IpSearch(long userId, @NonNull String ipAddress, String country,
                    String city, long searchTimestamp, boolean isValid) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.country = country;
        this.city = city;
        this.searchTimestamp = searchTimestamp;
        this.isValid = isValid;
    }

    public long getSearchId() { return searchId; }
    public void setSearchId(long searchId) { this.searchId = searchId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    @NonNull
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(@NonNull String ipAddress) { this.ipAddress = ipAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public long getSearchTimestamp() { return searchTimestamp; }
    public void setSearchTimestamp(long searchTimestamp) {
        this.searchTimestamp = searchTimestamp;
    }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
}