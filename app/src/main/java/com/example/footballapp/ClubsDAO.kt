package com.example.footballapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//specifies that this interface is a DAO
@Dao
interface ClubsDAO {
    //inset clubs into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //takes a list of clubs
    fun insertclubs(clubs: List<Club>)

    //search for clubs in the database based on a search query
    @Query("SELECT * FROM Clubs WHERE LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(league) LIKE '%' || LOWER(:searchQuery) || '%'")
    //returns a list of matching clubs
    fun searchclubswithquery(searchQuery: String): List<Club>
}