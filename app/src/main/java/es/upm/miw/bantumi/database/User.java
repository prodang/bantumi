package es.upm.miw.bantumi.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    protected Integer id;

    @ColumnInfo(name = "first_name")
    protected String firstName;
    @ColumnInfo(name = "date_time")
    protected String dateTime;
    @ColumnInfo(name = "score_one")
    protected Integer scoreOne;
    @ColumnInfo(name = "score_two")
    protected Integer scoreTwo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTime() {
        return dateTime;
    }

    public void setTime(String time) {
        this.dateTime = time;
    }

    public Integer getScoreOne() {
        return scoreOne;
    }

    public void setScoreOne(Integer scoreOne) {
        this.scoreOne = scoreOne;
    }

    public Integer getScoreTwo() {
        return scoreTwo;
    }

    public void setScoreTwo(Integer scoreTwo) {
        this.scoreTwo = scoreTwo;
    }

    @Override
    public String toString() {
        return firstName + ". "+
                dateTime  + ". "+
                "Player 1: "+scoreOne + ". "+
                "Player 2: "+scoreTwo+ ".";
    }
}
