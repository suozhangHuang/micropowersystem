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
	
	//��������������Ҫmanager����  ���������豸���������豸���ƣ���������Щ�豸��Ψһ��ʶ
	public Vector<String> getNames(int TYPE);
	
	//����ָ�����͵��豸��ʵʱ����ֵ��String��Ӧ�����豸�ı�ʶ
	public HashMap<String,TimeSeries> getPowerTimeSeries(int TYPE);
	public HashMap<String,TimeSeries> getVoltageTimeSeries(int TYPE);
	public TimeSeries getPrices(int TYPE);
	
	//
	public TimeSeries getTotalPowerTimeSeries(int TYPE);
	
	public HashMap<String,String> GetInfo(int TYPE); 
	
	
	public HashMap<String,TimeSeries> getForecastPower();
	
	
	public void sendEmail();
	
	
	
	
}
