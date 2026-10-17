package com.iptracker.app.ui.models;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class IPDetails implements Parcelable {
    private final String ipAddress;
    private final String hostname;
    private final String provider;
    private final String country;
    private final String region;
    private final String city;
    private final long timestamp;

    public IPDetails(String ipAddress, String hostname, String provider,
                     String country, String region, String city) {
        this.ipAddress = ipAddress != null ? ipAddress : "";
        this.hostname = hostname != null ? hostname : "";
        this.provider = provider != null ? provider : "";
        this.country = country != null ? country : "";
        this.region = region != null ? region : "";
        this.city = city != null ? city : "";
        this.timestamp = System.currentTimeMillis();
    }

    protected IPDetails(Parcel in) {
        ipAddress = in.readString();
        hostname = in.readString();
        provider = in.readString();
        country = in.readString();
        region = in.readString();
        city = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<IPDetails> CREATOR = new Creator<IPDetails>() {
        @Override
        public IPDetails createFromParcel(Parcel in) {
            return new IPDetails(in);
        }

        @Override
        public IPDetails[] newArray(int size) {
            return new IPDetails[size];
        }
    };

    public static IPDetails fromJson(JSONObject jsonResponse) {
        try {
            if (jsonResponse == null) {
                return new IPDetails("", "", "", "", "", "");
            }
            return new IPDetails(
                    jsonResponse.optString("ip", ""),
                    jsonResponse.optString("hostname", ""),
                    jsonResponse.optString("org", ""),
                    jsonResponse.optString("country_name", ""),
                    jsonResponse.optString("region", ""),
                    jsonResponse.optString("city", "")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new IPDetails("", "", "", "", "", "");
        }
    }

    public static IPDetails empty(String ipAddress) {
        return new IPDetails(
                ipAddress != null ? ipAddress : "",
                "",
                "",
                "",
                "",
                ""
        );
    }

    public boolean isValid() {
        return ipAddress != null && !ipAddress.isEmpty() &&
                country != null && !country.isEmpty();
    }

    public String getFormattedDetails() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("IP: ").append(ipAddress != null ? ipAddress : "").append("\n");
            if (hostname != null && !hostname.isEmpty())
                builder.append("Hostname: ").append(hostname).append("\n");
            if (provider != null && !provider.isEmpty())
                builder.append("Provider: ").append(provider).append("\n");
            if (country != null && !country.isEmpty())
                builder.append("Țară: ").append(country).append("\n");
            if (region != null && !region.isEmpty())
                builder.append("Regiune: ").append(region).append("\n");
            if (city != null && !city.isEmpty())
                builder.append("Oraș: ").append(city).append("\n");
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Eroare la formatarea detaliilor";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeString(ipAddress);
            dest.writeString(hostname);
            dest.writeString(provider);
            dest.writeString(country);
            dest.writeString(region);
            dest.writeString(city);
            dest.writeLong(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getIpAddress() {
        return ipAddress != null ? ipAddress : "";
    }

    public String getHostname() {
        return hostname != null ? hostname : "";
    }

    public String getProvider() {
        return provider != null ? provider : "";
    }

    public String getCountry() {
        return country != null ? country : "";
    }

    public String getRegion() {
        return region != null ? region : "";
    }

    public String getCity() {
        return city != null ? city : "";
    }

    public long getTimestamp() {
        return timestamp;
    }
}