package ru.zzsdeo.money.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class DatabaseContentProvider extends ContentProvider {

    // database
    private DatabaseHelper database;

    // used for the UriMacher
    private static final int ACCOUNTS_ITEMS = 10;
    private static final int ACCOUNTS_ITEM_ID = 20;
    private static final int TRANSACTIONS_ITEMS = 30;
    private static final int TRANSACTIONS_ITEM_ID = 40;
    private static final int SCHEDULED_TRANSACTIONS_ITEMS = 50;
    private static final int SCHEDULED_TRANSACTIONS_ITEM_ID = 60;
    private static final int CATEGORIES_ITEMS = 70;
    private static final int CATEGORIES_ITEM_ID = 80;

    private static final String AUTHORITY = "ru.zzsdeo.money.contentprovider";

    private static final String ACCOUNTS_PATH = TableAccounts.TABLE_NAME;
    private static final String TRANSACTIONS_PATH = TableTransactions.TABLE_NAME;
    private static final String SCHEDULED_TRANSACTIONS_PATH = TableScheduledTransactions.TABLE_NAME;
    private static final String CATEGORIES_PATH = TableCategories.TABLE_NAME;

    public static final Uri CONTENT_URI_ACCOUNTS = Uri.parse("content://" + AUTHORITY
            + "/" + ACCOUNTS_PATH);
    public static final Uri CONTENT_URI_TRANSACTIONS = Uri.parse("content://" + AUTHORITY
            + "/" + TRANSACTIONS_PATH);
    public static final Uri CONTENT_URI_SCHEDULED_TRANSACTIONS = Uri.parse("content://" + AUTHORITY
            + "/" + SCHEDULED_TRANSACTIONS_PATH);
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY
            + "/" + CATEGORIES_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/items";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/item";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, ACCOUNTS_PATH, ACCOUNTS_ITEMS);
        sURIMatcher.addURI(AUTHORITY, ACCOUNTS_PATH + "/#", ACCOUNTS_ITEM_ID);
        sURIMatcher.addURI(AUTHORITY, TRANSACTIONS_PATH, TRANSACTIONS_ITEMS);
        sURIMatcher.addURI(AUTHORITY, TRANSACTIONS_PATH + "/#", TRANSACTIONS_ITEM_ID);
        sURIMatcher.addURI(AUTHORITY, SCHEDULED_TRANSACTIONS_PATH, SCHEDULED_TRANSACTIONS_ITEMS);
        sURIMatcher.addURI(AUTHORITY, SCHEDULED_TRANSACTIONS_PATH + "/#", SCHEDULED_TRANSACTIONS_ITEM_ID);
        sURIMatcher.addURI(AUTHORITY, CATEGORIES_PATH, CATEGORIES_ITEMS);
        sURIMatcher.addURI(AUTHORITY, CATEGORIES_PATH + "/#", CATEGORIES_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ACCOUNTS_ITEMS:
                queryBuilder.setTables(TableAccounts.TABLE_NAME);
                break;
            case TRANSACTIONS_ITEMS:
                queryBuilder.setTables(TableTransactions.TABLE_NAME);
                break;
            case SCHEDULED_TRANSACTIONS_ITEMS:
                queryBuilder.setTables(TableScheduledTransactions.TABLE_NAME);
                break;
            case CATEGORIES_ITEMS:
                queryBuilder.setTables(TableCategories.TABLE_NAME);
                break;
            case ACCOUNTS_ITEM_ID:
                // Set the table
                queryBuilder.setTables(TableAccounts.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(TableAccounts.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case TRANSACTIONS_ITEM_ID:
                // Set the table
                queryBuilder.setTables(TableTransactions.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(TableTransactions.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case SCHEDULED_TRANSACTIONS_ITEM_ID:
                // Set the table
                queryBuilder.setTables(TableScheduledTransactions.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(TableScheduledTransactions.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case CATEGORIES_ITEM_ID:
                // Set the table
                queryBuilder.setTables(TableCategories.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(TableCategories.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        String path;
        switch (uriType) {
            case ACCOUNTS_ITEMS:
                id = sqlDB.insert(TableAccounts.TABLE_NAME, null, values);
                path = ACCOUNTS_PATH;
                break;
            case TRANSACTIONS_ITEMS:
                id = sqlDB.insert(TableTransactions.TABLE_NAME, null, values);
                path = TRANSACTIONS_PATH;
                break;
            case SCHEDULED_TRANSACTIONS_ITEMS:
                id = sqlDB.insert(TableScheduledTransactions.TABLE_NAME, null, values);
                path = SCHEDULED_TRANSACTIONS_PATH;
                break;
            case CATEGORIES_ITEMS:
                id = sqlDB.insert(TableCategories.TABLE_NAME, null, values);
                path = CATEGORIES_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case ACCOUNTS_ITEMS:
                rowsDeleted = sqlDB.delete(TableAccounts.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ACCOUNTS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TableAccounts.TABLE_NAME,
                            TableAccounts.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(TableAccounts.TABLE_NAME,
                            TableAccounts.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case TRANSACTIONS_ITEMS:
                rowsDeleted = sqlDB.delete(TableTransactions.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TRANSACTIONS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TableTransactions.TABLE_NAME,
                            TableTransactions.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(TableTransactions.TABLE_NAME,
                            TableTransactions.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case SCHEDULED_TRANSACTIONS_ITEMS:
                rowsDeleted = sqlDB.delete(TableScheduledTransactions.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SCHEDULED_TRANSACTIONS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TableScheduledTransactions.TABLE_NAME,
                            TableScheduledTransactions.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(TableScheduledTransactions.TABLE_NAME,
                            TableScheduledTransactions.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case CATEGORIES_ITEMS:
                rowsDeleted = sqlDB.delete(TableCategories.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CATEGORIES_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TableCategories.TABLE_NAME,
                            TableCategories.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(TableCategories.TABLE_NAME,
                            TableCategories.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case ACCOUNTS_ITEMS:
                rowsUpdated = sqlDB.update(TableAccounts.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ACCOUNTS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TableAccounts.TABLE_NAME,
                            values,
                            TableAccounts.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(TableAccounts.TABLE_NAME,
                            values,
                            TableAccounts.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case TRANSACTIONS_ITEMS:
                rowsUpdated = sqlDB.update(TableTransactions.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TRANSACTIONS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TableTransactions.TABLE_NAME,
                            values,
                            TableTransactions.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(TableTransactions.TABLE_NAME,
                            values,
                            TableTransactions.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case SCHEDULED_TRANSACTIONS_ITEMS:
                rowsUpdated = sqlDB.update(TableScheduledTransactions.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SCHEDULED_TRANSACTIONS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TableScheduledTransactions.TABLE_NAME,
                            values,
                            TableScheduledTransactions.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(TableScheduledTransactions.TABLE_NAME,
                            values,
                            TableScheduledTransactions.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case CATEGORIES_ITEMS:
                rowsUpdated = sqlDB.update(TableCategories.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CATEGORIES_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TableCategories.TABLE_NAME,
                            values,
                            TableCategories.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(TableCategories.TABLE_NAME,
                            values,
                            TableCategories.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                TableAccounts.COLUMN_ID,
                TableAccounts.COLUMN_NAME,
                TableAccounts.COLUMN_CARD_NUMBER,
                TableAccounts.COLUMN_BALANCE,
                TableCategories.COLUMN_ID,
                TableCategories.COLUMN_NAME,
                TableCategories.COLUMN_BUDGET,
                TableScheduledTransactions.COLUMN_ID,
                TableScheduledTransactions.COLUMN_ACCOUNT_ID,
                TableScheduledTransactions.COLUMN_DATE_IN_MILL,
                TableScheduledTransactions.COLUMN_AMOUNT,
                TableScheduledTransactions.COLUMN_COMMISSION,
                TableScheduledTransactions.COLUMN_COMMENT,
                TableScheduledTransactions.COLUMN_DESTINATION_ACCOUNT_ID,
                TableScheduledTransactions.COLUMN_NEED_APPROVE,
                TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID,
                TableScheduledTransactions.COLUMN_CATEGORY_ID,
                TableScheduledTransactions.COLUMN_LINKED_TRANSACTION_ID,
                TableTransactions.COLUMN_ID,
                TableTransactions.COLUMN_ACCOUNT_ID,
                TableTransactions.COLUMN_DATE_IN_MILL,
                TableTransactions.COLUMN_AMOUNT,
                TableTransactions.COLUMN_COMMISSION,
                TableTransactions.COLUMN_COMMENT,
                TableTransactions.COLUMN_DESTINATION_ACCOUNT_ID,
                TableTransactions.COLUMN_CATEGORY_ID,
                TableTransactions.COLUMN_LINKED_TRANSACTION_ID
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
