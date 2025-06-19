package it.qbr.testapisudoku.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// This DAO interface defines methods for accessing the Game database.
@Dao
interface GameDao {
    // Inserts a new game into the database.
    @Insert
    suspend fun inserisci(game: Game)

    // Retrieves all games from the database, ordered by the date and time in descending order.
    @Query("SELECT * FROM game ORDER BY dataOra DESC")
    suspend fun tutteLePartite(): List<Game>
}