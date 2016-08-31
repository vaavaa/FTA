package com.asiawaters.fta.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.asiawaters.fta.FTA;
import com.asiawaters.fta.MainActivity;
import com.asiawaters.fta.R;

public class InfoCardDialogFragment extends DialogFragment {
    AlertDialog.Builder builder;
    FTA fta;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fta = ((com.asiawaters.fta.FTA) getActivity().getApplicationContext());
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        String message = fta.getUser();
        builder.setTitle(R.string.infoCardTitle)
                .setIcon(R.drawable.person_icon)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                fta.setUser("");
                fta.setPerson(null);
                fta.setTaskMember(null);
                fta.setTaskGuid(null);
                fta.setListMembers(null);
                fta.setList_values(null);
                ((MainActivity)getActivity()).startLogingActivity();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
