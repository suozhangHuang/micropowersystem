package com.micropowersystem.management;

public interface Generator
{
	// 获取发电机信息
	public String getInfo();

	// 设置天气对象
	public void setWeatherCondition(Weather weather);
	
	// 获取发电机参数
	int NOMIAL = 0;
	int REALTIME = 1;
	public double getVoltage(int type);
	public double getCurrent(int type);
	public double getPower(int type);
	
	
	// 读取发电机电能表数值
	public double getWattHour();
	
	// 设置微电网管理器
//	public void setManager(Manager _manager);
	
	// 物理接口，用于模拟实际系统的控制
	public void output();
}
