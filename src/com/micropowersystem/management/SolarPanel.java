package com.micropowersystem.management;

import java.util.Properties;

public class SolarPanel extends Thread implements Generator{
	
	public SolarPanel(Properties properties) {
		
	}
	
	@Override
	public String getInfo()
	{
		return null;
	}

	@Override
	public void setWeatherCondition(Weather weather)
	{
		this.weather = weather;
	}

	
	@Override
	public double getVoltage(int type)
	{
		if(type == NOMIAL)
		{
			return this.voltageBase;
		}
		if(type == REALTIME)
		{
			return getVoltage(weather.getTemperature(),weather.getRadiancy(),weather.getCloudness());
		}
		return 0;
	}

	@Override
	public double getCurrent(int type)
	{
		if(type == NOMIAL)
		{
			return powerBase/voltageBase;
		}
		if(type == REALTIME)
		{
			return getCurrent(weather.getTemperature(),weather.getRadiancy(),weather.getCloudness());
		}
		return 0;
	}

	@Override
	public double getPower(int type)
	{
		if(type == NOMIAL)
		{
			return powerBase;
		}
		if(type == REALTIME)
		{
			return getPower(weather.getTemperature(),weather.getRadiancy(),weather.getCloudness());
		}
		return 0;
	}

	

	@Override
	public double getWattHour()
	{
		synchronized(this)
		{
			return energyMeter;
		}
	}

	@Override
	public void output()
	{
		//do something
		System.out.printf("SOLAR PANEL:output power!\n");
	}
	
	// 实时数据计算
	private double getVoltage(double temparature, double radiancy,double cloudness)
	{
		return voltageBase;
	}
	private double getCurrent(double temparature, double radiancy,double cloudness)
	{
		return getPower(temparature,radiancy,cloudness)/getVoltage(temparature,radiancy,cloudness);
	}
	private double getPower(double temparature, double radiancy,double cloudness)
	{
		
		double eta = nominalEfficiency*(1-beta*(temparature-25));
		return eta*panelArea*getSurfaceRadiancy(radiancy,cloudness);
	}
	private double getSurfaceRadiancy(double radiancy,double cloudness) {
		if(cloudness>=0&&cloudness<0.25)
			return radiancy*1;
		if(cloudness>=0.25&&cloudness<0.5)
			return radiancy*0.875;
		if(cloudness>=0.5&&cloudness<0.75)
			return radiancy*0.75;
		if(cloudness>=0.75&&cloudness<1)
			return radiancy*0.5;
		return 0;
	}
	// 实时数据更新
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// 计算休眠时间，并更新当前时刻的电价
			long currentTime = System.currentTimeMillis();
			timeDelta = (currentTime - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (currentTime - timestampStart)*TIME_SCALE;

			this.energyMeter += getPower(weather.getTemperature(),weather.getRadiancy(),weather.getCloudness()) * timeDelta/1000;
			
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// 电站参数
	private double voltageBase;
	private double powerBase;
	private double panelArea;
	private double nominalEfficiency;
	private final double beta = 0.004;
	
	// 电站数据
	private double energyMeter = 0;
	
	// 环境信息
	private Weather weather;
	private long timestamp;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;

}