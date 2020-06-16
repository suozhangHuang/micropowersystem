package com.micropowersystem.management;

public interface Generator
{
	// ��ȡ�������Ϣ
	public String getInfo();

	// ������������
	public void setWeatherCondition(Weather weather);
	// ��������Ԥ�����
	public void setWeatherForecast(WeatherForecast weatherForecast);
	
	// ��ȡ���������
	int NOMIAL = 0;
	int REALTIME = 1;
	public double getVoltage(int type);
	public double getCurrent(int type);
	public double getPower(int type);
	
	public double getVoltageForecast(long time);
	public double getCurrentForecast(long time);
	public double getPowerForecast(long time);
	
	// ��ȡ��������ܱ���ֵ
	public double getWattHour();
	
	// ����΢����������
//	public void setManager(Manager _manager);
	
	// ����ӿڣ�����ģ��ʵ��ϵͳ�Ŀ���
	public void output();
}
