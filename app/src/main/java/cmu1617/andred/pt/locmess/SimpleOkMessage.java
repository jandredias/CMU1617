package cmu1617.andred.pt.locmess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 23/04/2017.
 */

public class SimpleOkMessage {

    private static List<AlertDialog> listOpen = new ArrayList<>();
    private final AlertDialog _dialog;


    SimpleOkMessage(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
               .setCancelable(false)
//               .setPositiveButton("OK",null);
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listOpen.remove(_dialog);
                    }
        });

        while (listOpen.size() > 0) {
            listOpen.get(0).dismiss();
            listOpen.remove(0);
        }
        _dialog = builder.create();
        _dialog.show();
        listOpen.add(_dialog);

    }
}
