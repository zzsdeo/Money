package ru.zzsdeo.money.activities;

import android.app.Activity;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class GraphActivity extends Activity {

    private final static String DATE_FORMAT = "dd.MM.yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        ArrayList<ScheduledTransactionCollection.TransactionsHolder> dataHolder =
                new ScheduledTransactionCollection(this).getTransactionsHolderCollection();
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
        graph.getViewport().setMaxX(dataHolder.get(0).dateTime + 5184000000L);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        pointsSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(GraphActivity.this,
                        getString(R.string.date) + "  " + new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dataPoint.getX()) + "\n" +
                                getString(R.string.balance2) + "  " +
                                String.valueOf(
                                        BigDecimal.valueOf(
                                                dataPoint.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        pointsSeries.setSize(7.0f);
        pointsSeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
    }
}