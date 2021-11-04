package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.upm.miw.bantumi.database.AppDatabase;
import es.upm.miw.bantumi.database.User;
import es.upm.miw.bantumi.database.UserDao;
import es.upm.miw.bantumi.model.BantumiViewModel;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    int numInicialSemillas;
    private Button btReset;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        btReset = findViewById(R.id.button);
        crearObservadores();
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReserAlertDialog resetAlertDialog = new ReserAlertDialog();
                resetAlertDialog.show(getSupportFragmentManager(),"ALERT_DIALOG");
            }
        });
        this.db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,
                "users-db")
                .allowMainThreadQueries().build();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero, actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.design_default_color_primary));
                tvJugador2.setTextColor(getColor(R.color.black));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.design_default_color_primary));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAjustes:
                startActivity(new Intent(this, BantumiPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            case R.id.opcReiniciarPartida:
                ReserAlertDialog resetAlertDialog = new ReserAlertDialog();
                resetAlertDialog.show(getSupportFragmentManager(),"ALERT_DIALOG");
                return true;

            case R.id.opcGuardarPartida:
                saveFile();
                showSnack(getString(R.string.txtSave));
                return true;

            case R.id.opcRecuperarPartida:
                recoverFile();
                return true;

            case R.id.opcMejoresResultados:
                startActivity(new Intent(this,BestResult.class));
                return true;

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    private void saveFile(){
        if(isContent()){
            deleteFile();
        }
        save();
    }

    private void deleteFile() {
        try {
            FileOutputStream fos = openFileOutput(getFileName(), Context.MODE_PRIVATE);
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void save(){
        try {
            FileOutputStream fos = openFileOutput(getFileName(), Context.MODE_APPEND);
            String date = this.juegoBantumi.serializa();
            fos.write(date.getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isContent(){
        boolean isContent = false;
        try {
            BufferedReader fin = new BufferedReader(
                    new InputStreamReader(openFileInput(getFileName())));
            if(!fin.readLine().isEmpty()){
                isContent = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isContent;
    }

    private String getFileName() {
        return getResources().getString(R.string.default_FileName);
    }

    private void showSnack(String txt){
        Snackbar.make(
                findViewById(android.R.id.content),
                txt,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private void recoverFile(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.txtRecoverTitle)
                .setMessage(R.string.txtRecoverQuestion)
                .setPositiveButton(R.string.txtRecoverTrue,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                recover();
                            }
                        })
                .setNegativeButton(R.string.txtRecoverFalse, null)
                .show();
    }

    private void recover(){
        boolean isContent = false;
        try{
            String game = "";
            BufferedReader fin = new BufferedReader(
                    new InputStreamReader(openFileInput(getFileName())));
            String linea = fin.readLine();
            while (linea != null) {
                isContent = true;
                game += linea+";";
                linea = fin.readLine();
            }
            this.juegoBantumi.deserializa(game);
            fin.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!isContent){
            showSnack(getString(R.string.txtRecoverNull));
        }else{
            showSnack(getString(R.string.txtRecover));
        }
    }

    /**
     * Acción que se ejecuta al pulsar sobre un hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
        .show();

        saveDB();

        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    private void saveDB(){
        User user = new User();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String namePlayer = sharedPref.getString(
                "name",
                getString(R.string.name_title)
        );
        user.setFirstName(namePlayer);
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        user.setTime(timeStamp);
        user.setScoreOne(this.juegoBantumi.getScore(JuegoBantumi.PLAYER_ONE));
        user.setScoreTwo(this.juegoBantumi.getScore(JuegoBantumi.PLAYER_TWO));
        UserDao userDao = this.db.userDao();
        userDao.insertAll(user);
    }
}