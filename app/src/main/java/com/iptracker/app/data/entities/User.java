package com.iptracker.app.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long userId;

    @ColumnInfo(name = "username")
    @NonNull
    private String username;

    @ColumnInfo(name = "email")
    @NonNull
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "country")
    private String country;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "date_of_birth")
    private long dateOfBirth;

    @ColumnInfo(name = "last_login_timestamp")
    private long lastLoginTimestamp;

    @ColumnInfo(name = "registration_type")
    private String registrationType;

    @Ignore
    public User(String username, String email, long lastLoginTimestamp) {
        this.username = username;
        this.email = email;
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public User(@NonNull String username, @NonNull String email, String password,
                String firstName, String lastName, String country, String city,
                String phone, long dateOfBirth, long lastLoginTimestamp,
                String registrationType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.registrationType = registrationType;
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public long getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(long dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public long getLastLoginTimestamp() { return lastLoginTimestamp; }
    public void setLastLoginTimestamp(long lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public String getRegistrationType() { return registrationType; }
    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }
}