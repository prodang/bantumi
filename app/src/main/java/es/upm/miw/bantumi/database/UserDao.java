package es.upm.miw.bantumi.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users ORDER BY score_one DESC, score_two DESC LIMIT 10")
    List<User> getBestScores();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(User... users);

    @Delete
    int delete(User user);
}
