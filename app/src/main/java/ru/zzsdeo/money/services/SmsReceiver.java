package ru.zzsdeo.money.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BODY = "sms_body";
    public static final String SMS_DATE_IN_MILL = "sms_date_in_mill";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        long dateInMill = Long.MAX_VALUE;

        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            sb.append(sms[n].getMessageBody());
            dateInMill = sms[n].getTimestampMillis();
        }

        Bundle smsBundle = new Bundle();
        smsBundle.putString(SMS_BODY, sb.toString());
        smsBundle.putLong(SMS_DATE_IN_MILL, dateInMill);
        context.startService(new Intent(context, SmsParserIntentService.class).putExtras(smsBundle));
    }
}
