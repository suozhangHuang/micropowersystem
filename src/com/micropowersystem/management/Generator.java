package com.micropowersystem.management;

public interface Generator
{
	// ��ȡ�������Ϣ
	public String getInfo();

	// ������������
	public void setWeatherCondition(Weather weather);
	
	// ��ȡ���������
	int NOMIAL = 0;
	int REALTIME = 1;
	public double getVoltage(int type);
	public double getCurrent(int type);
	public double getPower(int type);
	
	
	// ��ȡ��������ܱ���ֵ
	public double getWattHour();
	
	// ����΢����������
//	public void setManager(Manager _manager);
	
	// ����ӿڣ�����ģ��ʵ��ϵͳ�Ŀ���
	public void output();
}
