package com.example.haris.mysqllogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Haris on 12/11/2018.
 */

public class PromoDialog extends AppCompatDialogFragment {
    private EditText promoEdit;
    private String promo;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getActivity().getLayoutInflater();
        View view = inflate.inflate(R.layout.promo_dialog,null);
        promoEdit = (EditText) view.findViewById(R.id.promoEdit);
        alertDialogBuilder.setTitle(getResources().getString(R.string.promo));

        // setting cancelation
        alertDialogBuilder.setCancelable(false); // this method define that a user can canel it or not (he has to select one option or not) . Try cominting it

        //setting message to be displayed
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setMessage(getResources().getString(R.string.enterPromo));

        // setting if user click Yes (Positive button)
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.enter) /* The message to be displed on the dialog box rigth button*/
                ,new DialogInterface.OnClickListener()// defines what happens when user click yes
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        promo = promoEdit.getText().toString();
                    }
                });

        // setting if user clicks no (negative button) . On the left
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel)/* mesage on left button of dialog box*/
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel(); // close dialog box
                    }
                });
        return alertDialogBuilder.create(); // creating actual dialog box the first one was just builder
    }

    public String getPromo() {
        return promo;
    }
}
