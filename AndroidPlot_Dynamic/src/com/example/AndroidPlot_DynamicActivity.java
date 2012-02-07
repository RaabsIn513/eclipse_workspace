package com.example;


import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import android.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
 
public class AndroidPlot_DynamicActivity extends Activity {
 
    private XYPlot dynamicPlot;
    private XYPlot staticPlot;
    private MyPlotUpdater plotUpdater;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
 
        // android boilerplate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);
 
        plotUpdater = new MyPlotUpdater(dynamicPlot);
 
        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
 
        // getInstance and position datasets:
        SampleDynamicXYDatasource data = new SampleDynamicXYDatasource();
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Sine 1");
        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Sine 2");
 
        // create a series using a temporary formatter, with a transparent fill applied immediately
        dynamicPlot.addSeries(sine1Series, new LineAndPointFormatter(Color.rgb(0, 0, 0), null, Color.rgb(0, 80, 0),  
                Color.argb(200, 50, 80, 50)));
 
        // create a series using an instantiated formatter, with some transparency applied after creation:
        LineAndPointFormatter form1 = new LineAndPointFormatter(Color.rgb(0, 0, 200), null, Color.rgb(0, 0, 80));
        form1.getFillPaint().setAlpha(220);
        dynamicPlot.addSeries(sine2Series, form1);
        dynamicPlot.setGridPadding(5, 0, 5, 0);
 
        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);
 
        dynamicPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
        dynamicPlot.setDomainStepValue(sine1Series.size());
 
        // thin out domain/range tick labels so they dont overlap each other:
        dynamicPlot.setTicksPerDomainLabel(5);
        dynamicPlot.setTicksPerRangeLabel(3);
        dynamicPlot.disableAllMarkup();
 
        // freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);
 
        // kick off the data generating thread:
        new Thread(data).start();
    }
	
	// redraws a plot whenever an update is received:
    private class MyPlotUpdater implements Observer {
        Plot plot;
        public MyPlotUpdater(Plot plot) {
            this.plot = plot;
        }
        @Override
        public void update(Observable o, Object arg) {
            try {
                plot.postRedraw();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
 
}