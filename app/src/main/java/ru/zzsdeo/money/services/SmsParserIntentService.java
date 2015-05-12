package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Iterator;

import ru.zzsdeo.money.Constants;


public class SmsParserIntentService extends IntentService {

    public static final String SMS_PARSER_INTENT_SERVICE_NAME = "sms_parser_intent_service";

    public SmsParserIntentService() {
        super(SMS_PARSER_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String sms = intent.getExtras().getString(SmsReceiver.SMS_BODY);
        long dateInMill = intent.getExtras().getLong(SmsReceiver.SMS_DATE_IN_MILL);
        int sign = 1;
        if (sms.startsWith("Telecard")) {
            long accountId = 0;
            float amount = 0;
            float commission = 0;
            String comment = "";
            float dostupno = 0;
            String cardNumber = "";

            String[] parsedSms = sms.split("\\;");
            for (String str : parsedSms) {
                str = str.trim();
                if (str.matches("Card\\d\\d\\d\\d")) {
                    /*AccountCollection ac = new AccountCollection(getApplicationContext(), new String[] {
                            TableAccounts.COLUMN_CARD_NUMBER + " LIKE '" + str.substring(4) + "'",
                            null
                    });
                    if (!ac.isEmpty()) {
                        Iterator<Account> it = ac.values().iterator();
                        Account a = it.next();
                        accountId = a.getAccountId();
                        cardNumber = a.getCardNumber();
                    }*/
                }
                if (!str.endsWith("RUR") & !str.matches("Oplata|Cash\\-in|Snyatie\\snalichnih|Zachislenie|Oplata\\sv\\sI\\-net|Predauth|Perevod|Poluchen\\sperevod|Card\\d\\d\\d\\d|\\d\\d\\.\\d\\d\\.\\d\\d\\s\\d\\d\\:\\d\\d\\:\\d\\d|Telecard")) {
                    comment = str;
                }
                if (str.matches("Oplata|Snyatie\\snalichnih|Oplata\\sv\\sI\\-net|Predauth|Perevod")) {
                    sign = -1;
                }
                if (str.matches("Cash\\-in|Zachislenie|Poluchen\\sperevod")) {
                    sign = 1;
                }
                if (str.matches("([0-9\\.]+(\\sRUR))|(Summa\\s[0-9\\.]+(\\sRUR))")) {
                    amount = Float.parseFloat(str.replaceAll("\\sRUR", "").replaceAll("Summa\\s", "").trim());
                }
                if (str.matches("dostupno:\\s[0-9\\.]+(\\sRUR)")) {
                    dostupno = Float.parseFloat(str.replaceAll("dostupno:\\s|\\sRUR", "").trim());
                }
                /*if (str.matches("ispolzovano:\\s[0-9\\.]+(\\sRUR)")) {
                    //cv.put("indebtedness", Double.parseDouble(str.replaceAll("ispolzovano:\\s|\\sRUR", "").trim()));
                }*/
                if (str.matches("komissiya:\\s[0-9\\.]+(\\sRUR)")) {
                    commission = Float.parseFloat(str.replaceAll("komissiya:\\s|\\sRUR", "").trim());
                }

            }

            if (accountId != 0) {
                if (!cardNumber.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putFloat(cardNumber, dostupno).apply();
                }
                if (sign < 0) amount = -amount;
                /*new TransactionCollection(getApplicationContext()).addTransaction(
                        accountId,
                        dateInMill,
                        amount,
                        -commission,
                        comment,
                        0,
                        0,
                        0
                );

                // Обновление баланса
                TransactionCollection transactionCollection = new TransactionCollection(this,
                        new String[] {
                                TableTransactions.COLUMN_ACCOUNT_ID + "=" + accountId,
                                null
                        });
                float balance = 0;
                for (Transaction transaction : transactionCollection.values()) {
                    balance = balance + transaction.getAmount() + transaction.getCommission();
                }
                new AccountCollection(getApplicationContext()).get(accountId).setBalance(balance);*/
            }

            // посылаем сообщение на обновление списка транзакций

            Intent i = new Intent(ServiceReceiver.BROADCAST_ACTION);
            i.putExtra(ServiceReceiver.ACTION, ServiceReceiver.REFRESH_ALL);
            sendBroadcast(i);
        }
    }
}
