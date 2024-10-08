package com.example.footballapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

//specifies that this interface is a DAO
@Dao
interface footballleagueDAO {
    //insert leagues into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //takes a list of football leagues
    fun insertleagues(footballLeagues: List<footballleague>)
}