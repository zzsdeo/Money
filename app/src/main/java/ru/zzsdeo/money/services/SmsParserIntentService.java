package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.widgets.WidgetReceiver;


public class SmsParserIntentService extends IntentService {

    private static final String SMS_PARSER_INTENT_SERVICE_NAME = "sms_parser_intent_service";

    public SmsParserIntentService() {
        super(SMS_PARSER_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String sms = intent.getExtras().getString(SmsReceiver.SMS_BODY);
        assert sms != null;

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String cardNumber = sharedPreferences.getString(Constants.CARD_NUMBER, "");

        if (sms.matches("\\*" + cardNumber + ".+")) {
            String[] parsedSms = sms.split("\\s");
            if (parsedSms.length > 0) {
                String available = parsedSms[parsedSms.length - 2];
                if (available.equals("Доступно")) {
                    String balance = parsedSms[parsedSms.length - 1];
                    balance = balance.substring(0, balance.length() - 1);

                    sharedPreferences.edit().putString(Constants.BALANCE, balance).apply();

                    // посылаем сообщение на обновление списка транзакций

                    Intent i = new Intent(ServiceReceiver.BROADCAST_ACTION);
                    i.putExtra(ServiceReceiver.ACTION, ServiceReceiver.REFRESH_ALL);
                    sendBroadcast(i);

                    // обновляем виджет

                    ComponentName thisAppWidget = new ComponentName(getApplicationContext(), WidgetReceiver.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

                    Intent update = new Intent(this, WidgetReceiver.class);
                    update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    getApplicationContext().sendBroadcast(update);
                }

            }
        }
    }
}
