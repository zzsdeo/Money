package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.widgets.WidgetReceiver;


public class SmsParserIntentService extends IntentService {

    private static final String SMS_PARSER_INTENT_SERVICE_NAME = "sms_parser_intent_service";

    public SmsParserIntentService() {
        super(SMS_PARSER_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String sms = intent.getExtras().getString(SmsReceiver.SMS_BODY);
        if (sms.startsWith("Telecard")) {
            String dostupno = "";
            String cardNumber = "";

            String[] parsedSms = sms.split(";");
            for (String str : parsedSms) {
                str = str.trim();
                if (str.matches("Card\\d\\d\\d\\d")) {
                    cardNumber = str.substring(4);
                }
                if (str.matches("dostupno:\\s[0-9\\.]+(\\sRUR)")) {
                    dostupno = str.replaceAll("dostupno:\\s|\\sRUR", "").trim();
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            if (!cardNumber.isEmpty() && cardNumber.equals(sharedPreferences.getString(Constants.CARD_NUMBER, ""))) {
                sharedPreferences.edit().putString(Constants.BALANCE, dostupno).apply();

                // посылаем сообщение на обновление списка транзакций

                Intent i = new Intent(ServiceReceiver.BROADCAST_ACTION);
                i.putExtra(ServiceReceiver.ACTION, ServiceReceiver.REFRESH_ALL);
                sendBroadcast(i);

                // обновляем виджет

                ComponentName thisAppWidget = new ComponentName(getApplicationContext(), WidgetReceiver.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

                Intent update = new Intent();
                update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getApplicationContext().sendBroadcast(update);
            }
        }
    }
}
