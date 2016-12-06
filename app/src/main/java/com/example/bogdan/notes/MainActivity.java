package com.example.spb.kbv.education;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckpointChart extends PieChart {

    private HashMap<String, Integer> checkPoints () {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Отменено", 5);
        map.put("Завершено", 10);
        map.put("Начато", 2);
        return map;
    }

    public CheckpointChart(Context context, AttributeSet args) {
        super(context, args);
        initialzeChart();
    }

    private void initialzeChart() {
        this.setRotationEnabled(false);

        this.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getContext(), "Touch Event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        this.setCenterTextSize(10f);
        this.setHoleRadius(60f);

        this.setData(populatePieData());
        this.setDescription(null);
        this.setDrawEntryLabels(false);

        Legend chartLegend = this.getLegend();
        chartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chartLegend.setOrientation(Legend.LegendOrientation.VERTICAL);
        /*chartLegend.setDrawInside(false);*/
        /*chartLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);*/
        chartLegend.setForm(Legend.LegendForm.CIRCLE);
    }


    private PieData populatePieData() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        int numberOfCheckpoints = 0;
        for (Map.Entry entry : checkPoints().entrySet()) {
            int value = (Integer) entry.getValue();
            if (value > 0) {
                entries.add(new PieEntry(value, value + " " + entry.getKey().toString()));
                numberOfCheckpoints += (Integer) entry.getValue();
            }
        }

        this.setCenterText(String.valueOf(numberOfCheckpoints));

        PieDataSet pieData = new PieDataSet(entries, null);
        pieData.setColors(ColorTemplate.MATERIAL_COLORS);
        pieData.setSliceSpace(2f);
        pieData.setSelectionShift(0);
        pieData.setValueFormatter(new EmptyValueFormatter());

        return new PieData(pieData);
    }

    private class EmptyValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }
}
