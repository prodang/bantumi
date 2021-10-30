package es.upm.miw.bantumi.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    protected Integer id;

    @ColumnInfo(name = "first_name")
    protected String firstName;
    @ColumnInfo(name = "date_time")
    protected String dateTime;
    protected String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
