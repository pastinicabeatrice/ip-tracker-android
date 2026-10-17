package com.iptracker.app.data.dao;

import androidx.room.*;
import com.iptracker.app.data.entities.IpSearch;
import java.util.List;

@Dao
public interface IpSearchDao {
    @Insert
    long insert(IpSearch ipSearch);

    @Update
    void update(IpSearch ipSearch);

    @Delete
    void delete(IpSearch ipSearch);

    @Query("SELECT * FROM ip_searches WHERE userId = :userId ORDER BY searchTimestamp DESC")
    List<IpSearch> getSearchesByUser(long userId);

    @Query("SELECT * FROM ip_searches WHERE country LIKE :country AND isValid = 1")
    List<IpSearch> getSearchesByCountry(String country);

    @Query("SELECT * FROM ip_searches WHERE city LIKE :city AND isValid = 1")
    List<IpSearch> getSearchesByCity(String city);

    @Query("SELECT * FROM ip_searches WHERE searchTimestamp BETWEEN :startTime AND :endTime ORDER BY searchTimestamp DESC")
    List<IpSearch> getSearchesByTimeRange(long startTime, long endTime);
}