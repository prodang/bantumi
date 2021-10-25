package es.upm.miw.bantumi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ReserAlertDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle(R.string.txtResetTitle)
                .setMessage(R.string.txtResetQuestion)
                .setPositiveButton(R.string.txtResetTrue,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);
                            }
                        }
                )
                .setNegativeButton(R.string.txtResetFalse,null);

        return builder.create();
    }
}
