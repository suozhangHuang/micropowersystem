package com.micropowersystem.management;

import java.util.HashMap;
import java.util.Vector;

import org.jfree.data.time.TimeSeries;

public interface Management {
	
	final int GENERATOR = 0;
	final int STORAGE = 1;
	final int USER = 2;
	final int POWERSYSTEM = 3;
	
	public boolean getManagerStatus();
	
	//以下三个函数需要manager返回  各个类型设备所包含的设备名称，名称是这些设备的唯一标识
	public Vector<String> getNames(int TYPE);
	
	//返回指定类型的设备的实时功率值，String对应的是设备的标识
	public HashMap<String,TimeSeries> getPowerTimeSeries(int TYPE);
	public HashMap<String,TimeSeries> getVoltageTimeSeries(int TYPE);
	public TimeSeries getPrices(int TYPE);
	
	//
	public TimeSeries getTotalPowerTimeSeries(int TYPE);
	
	public HashMap<String,String> GetInfo(int TYPE); 
	
	
	public HashMap<String,TimeSeries> getForecastPower();
	
	
	public void sendEmail();
	
	
	
	
}
