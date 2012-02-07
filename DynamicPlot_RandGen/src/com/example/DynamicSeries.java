package com.example;

import com.androidplot.series.XYSeries;

public class DynamicSeries implements XYSeries {

	private dataGen dataSource;
	private String title;
	
	public DynamicSeries( dataGen dataSource, String title )
	{
		this.dataSource = dataSource;
		this.title = title;
	}
	
	@Override
	public String getTitle()
	{
		return this.title;
	}
	
	@Override
	public int size()
	{
		return dataSource.getItemCount();
	}

	@Override
	public Number getX(int index)
	{
		return dataSource.getX();
	}
	
	@Override
	public Number getY(int index)
	{
		return dataSource.getY();
	}
}
