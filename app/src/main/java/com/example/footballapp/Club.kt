package com.example.footballapp

import androidx.room.Entity
import androidx.room.PrimaryKey

//specifies that this class represents an entity in the Room database called Clubs
@Entity(tableName = "Clubs")
data class Club(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val alternate: String,
    val formedYear: String,
    val league: String,
    val stadium: String,
    val keywords: String,
    val stadiumThumb: String?,
    val stadiumLocation: String,
    val stadiumCapacity: String,
    val website: String,
    val teamJersey: String,
    val teamLogo: String
)
