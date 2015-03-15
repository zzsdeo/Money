package ru.zzsdeo.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class Dialogs extends DialogFragment {

    public static final String DIALOGS_TAG = "dialogs_tag";

    public static final String DIALOG_TYPE = "dialog_type";
    public static final String ID = "id";

    public static final int DELETE_ACCOUNT = 10;

    private Bundle bundle;
    private DialogListener dialogListener;

    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, long id);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (bundle.getInt(DIALOG_TYPE)) {
            case DELETE_ACCOUNT:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Вы уверены?");
                builder.setMessage("Это приведет к удаленнию всех транзакций связанных с данным счетом.");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, bundle.getLong(ID));
                    }
                });
                return builder.create();
            default:
                return super.onCreateDialog(savedInstanceState);
        }
    }
}
