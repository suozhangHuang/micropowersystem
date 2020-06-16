package com.micropowersystem.management;

public class WindTurbine extends Thread implements Generator
{

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
			return getVoltage(weather.getWindSpeed(timestamp));
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
			return getCurrent(weather.getWindSpeed(timestamp));
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
			return getPower(weather.getWindSpeed(timestamp));
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
		System.out.printf("WIND TURBINE:output power!\n");
	}
	
	// 实时数据计算
	private double getVoltage(double windSpeed)
	{
		return voltageBase;
	}
	private double getCurrent(double windSpeed)
	{
		return getPower(windSpeed)/getVoltage(windSpeed);
	}
	private double getPower(double windSpeed)
	{
		if(windSpeed < minWindSpeed)
		{
			return 0;
		}
		if(windSpeed > curtailingSpeed)
		{
			return 0;
		}
		if(windSpeed > maxWindSpeed)
		{
			return powerBase;
		}
		
		return powerBase * (windSpeed - minWindSpeed)/(maxWindSpeed - minWindSpeed);
	}
	
	// 实时数据更新
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// 计算休眠时间，并更新当前时刻的电价
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			this.energyMeter += getPower(weather.getWindSpeed()) * timeDelta;
			
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
	private final double minWindSpeed = 4;
	private final double maxWindSpeed = 10;
	private final double curtailingSpeed = 20;
	
	// 电站数据
	private double energyMeter = 0;
	
	// 环境信息
	private Weather weather;
	private WeatherForecast weatherForecast;
	private long timestamp;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;
}
