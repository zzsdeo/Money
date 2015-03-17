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
    public static final int YOU_MUST_ADD_ACCOUNT = 20;

    private Bundle bundle;
    private DialogListener dialogListener;

    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int dialogType);
        public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id);

        public void onDialogNegativeClick(DialogFragment dialog, int dialogType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle == null) throw new NullPointerException("must call setArguments() in calling activity with DIALOG_TYPE as a key and int as a value");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (bundle.getInt(DIALOG_TYPE)) {
            case DELETE_ACCOUNT:
                builder.setTitle("Вы уверены?");
                builder.setMessage("Это приведет к удаленнию всех транзакций связанных с данным счетом.");
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(Dialogs.this, DELETE_ACCOUNT);
                    }
                });
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, DELETE_ACCOUNT, bundle.getLong(ID));
                    }
                });
                return builder.create();
            case YOU_MUST_ADD_ACCOUNT:
                builder.setMessage("Для работы приложения необходимо создать хотя бы один счет.");
                builder.setCancelable(false);
                builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(Dialogs.this, YOU_MUST_ADD_ACCOUNT);
                    }
                });
                return builder.create();
            default:
                return super.onCreateDialog(savedInstanceState);
        }
    }
}
