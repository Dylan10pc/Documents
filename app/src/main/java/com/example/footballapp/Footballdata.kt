package com.example.footballapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.footballapp.footballleague
import com.example.footballapp.footballleagueDAO

//defines the as a room database with the specified entities and version
@Database(entities = [footballleague::class, Club::class], version = 1)
abstract class footballdata : RoomDatabase() {
    //declares an abstract function to retrieve the DAO interface for football leagues
    abstract fun footballLeagueDao(): footballleagueDAO
    //declares an abstract function to retrieve the DAO interface for clubs
    abstract fun ClubsDAO(): ClubsDAO
}