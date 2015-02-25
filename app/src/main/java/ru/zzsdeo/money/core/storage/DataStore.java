package ru.zzsdeo.money.core.storage;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.zzsdeo.money.core.TransactionCollection;

public class DataStore {

    private static final String DATA_STORE_SUB_DIR = "data";

    public static final String TRANSACTION_COLLECTION_FILE_NAME = "transactions";
    public static final String ACCOUNT_COLLECTION_FILE_NAME = "accounts";

    private Context context;

    public DataStore(Context context) {
        this.context = context;
    }

    public void saveData(Object object, String filename) {
        try {
            File dir = new File(context.getFilesDir(), File.separator + DATA_STORE_SUB_DIR);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object loadData(String filename) {
        try {
            File dir = new File(context.getFilesDir(), File.separator + DATA_STORE_SUB_DIR);
            if (!dir.exists()) return null;
            File file = new File(dir, filename);
            if (!file.exists()) return null;
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
