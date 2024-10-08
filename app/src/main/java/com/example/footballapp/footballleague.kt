package com.example.footballapp

import androidx.room.Entity
import androidx.room.PrimaryKey

//specifies that this class represents an entity in the Room database called football_leagues
@Entity(tableName = "football_leagues")
data class footballleague(
    @PrimaryKey val idLeague: String,
    val strLeague: String,
    val strSport: String,
    val strLeagueAlternate: String
)
