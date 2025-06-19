package it.qbr.testapisudoku.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data class represents a game entity in the database.
@Entity(tableName = "Game")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dataOra: Long,
    val vinta: Boolean,
    val tempo: Int,
    val difficolta: String,
    val errori: Int,
    val initialBoard: String,
    val solutionBoard: String,
    val finalBard: String  // tabella quando l'utente finisce la partita (vince, perde, abbandona)
)

