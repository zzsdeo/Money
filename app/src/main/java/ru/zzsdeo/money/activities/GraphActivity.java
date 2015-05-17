package ru.zzsdeo.money.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class GraphActivity extends Activity {

    private final static String DATE_FORMAT = "dd.MM.yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        ArrayList<TransactionsHolder> dataHolder = getSortedTransactions(new ScheduledTransactionCollection(this));
        int size = dataHolder.size();
        DataPoint[] dataPoints = new DataPoint[size];
        for (int i = 0; i < size; i++) {
            dataPoints[i] = new DataPoint(new Date(dataHolder.get(i).dateTime), dataHolder.get(i).getBalance());
        }
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(dataPoints);
        PointsGraphSeries<DataPoint> pointsSeries = new PointsGraphSeries<>(dataPoints);
        graph.addSeries(lineSeries);
        graph.addSeries(pointsSeries);

        // конфигурация графика

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getViewport().setMinX(dataHolder.get(0).dateTime);
        graph.getViewport().setMaxX(dataHolder.get(0).dateTime + 5184000000l);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        pointsSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(GraphActivity.this,
                        getString(R.string.date) + " " + new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dataPoint.getX()) + "\n" +
                                getString(R.string.balance2) + " " + String.valueOf(dataPoint.getY()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        pointsSeries.setSize(7.0f);
        pointsSeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
    }

    private static class TransactionsHolder {
        public final ScheduledTransaction scheduledTransaction;
        public final long dateTime;
        private float balance;

        public TransactionsHolder(long dateTime, ScheduledTransaction scheduledTransaction) {
            this.dateTime = dateTime;
            this.scheduledTransaction = scheduledTransaction;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }

        public float getBalance() {
            return balance;
        }
    }

    private ArrayList<TransactionsHolder> getSortedTransactions(ScheduledTransactionCollection mTransactionCollection) {
        ArrayList<TransactionsHolder> mTransactions = new ArrayList<>();
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        long endOfTimeSetting = mSharedPreferences.getInt(Constants.NUMBER_OF_MONTHS, Constants.DEFAULT_NUM_OF_MONTHS) * 2592000000l;
        for (ScheduledTransaction st : mTransactionCollection) {
            Calendar now = Calendar.getInstance();
            long endOfTime = now.getTimeInMillis() + endOfTimeSetting;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(st.getDateInMill());
            switch (st.getRepeatingTypeId()) {
                case 0: // один раз
                    mTransactions.add(new TransactionsHolder(st.getDateInMill(), st));
                    break;
                case 1: // каждый день
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 2: // каждый будний день
                    do {
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        }
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 3: // каждое определенное число
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 4: // каждый определенный день недели
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 5: // каждый последний день месяца
                    do {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
            }
        }

        // сортируем по дате

        Collections.sort(mTransactions, new Comparator<TransactionsHolder>() {
            @Override
            public int compare(TransactionsHolder lhs, TransactionsHolder rhs) {
                if (lhs.dateTime > rhs.dateTime) {
                    return 1;
                } else if (lhs.dateTime < rhs.dateTime) {
                    return -1;
                } else {
                    if (lhs.scheduledTransaction.getScheduledTransactionId() > rhs.scheduledTransaction.getScheduledTransactionId()) {
                        return 1;
                    } else if (lhs.scheduledTransaction.getScheduledTransactionId() < rhs.scheduledTransaction.getScheduledTransactionId()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        // рассчитываем балансы

        String stringBalance = mSharedPreferences.getString(Constants.BALANCE, "");
        float balance = 0;
        if (stringBalance != null) {
            if (!stringBalance.isEmpty()) balance = Float.parseFloat(stringBalance);
        }
        for (TransactionsHolder transactionsHolder : mTransactions) {
            balance = balance + transactionsHolder.scheduledTransaction.getAmount();
            transactionsHolder.setBalance(balance);
        }

        return mTransactions;
    }

}