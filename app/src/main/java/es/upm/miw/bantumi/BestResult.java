package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

import es.upm.miw.bantumi.database.AppDatabase;
import es.upm.miw.bantumi.database.User;
import es.upm.miw.bantumi.database.UserDao;

public class BestResult extends AppCompatActivity {

    private AppDatabase db;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);
        this.textView = (TextView) findViewById(R.id.txtResult);
        this.db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,
                "users-db")
                .allowMainThreadQueries().build();
        this.init();
        this.button = findViewById(R.id.btnDelete);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.txtDeleteTitle)
                        .setMessage(R.string.txtDeleteQuestion)
                        .setPositiveButton(R.string.txtDeleteTrue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                        .setNegativeButton(R.string.txtRecoverFalse, null)
                        .show();
            }
        });
    }

    private void init(){
        UserDao userDao = this.db.userDao();
        List<User> users = userDao.getBestScores();
        for (User user: users){
            this.textView.append(user.toString()+"\n");
        }
    }

    private void delete(){
        UserDao userDao = this.db.userDao();
        List<User> users = userDao.getAll();
        for (User user: users){
            userDao.delete(user);
        }
        this.textView.setText("");
    }
}
