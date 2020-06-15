package com.micropowersystem.management;

public class Weather extends Thread
{
	public Weather(double temperature,
			double humidity,
			double cloudness,
			double pressure,
			double UVindex,
			double visibility)
	{
		this.temperature = temperature;
		this.humidity = humidity;
		this.cloudness = cloudness;
		this.pressure = pressure;
		this.UVindex = UVindex;
		this.visibility = visibility;
		
		// 启动自动更新线程
		this.start();
	}
	
	public Weather()
	{
		this.temperature = 0;
		this.humidity = 0;
		this.cloudness = 0;
		this.pressure = 0;
		this.UVindex = 0;
		this.visibility = 0;
	}
	
	// 获取当前时间下的天气
	public double getTemperature()
	{
		return temperature;
	}
	public double getHumidity()
	{
		return humidity;
	}
	public double getCloudness()
	{
		return cloudness;
	}
	public double getPressure()
	{
		return pressure;
	}
	public double getUVindex()
	{
		return UVindex;
	}
	public double getVisibility()
	{
		return visibility;
	}

	// 获取指定时间下的天气值
	// TODO 实现更新指定时间下的天气值的功能
	public double getTemperature(long time)
	{
		return temperature;
	}
	public double getHumidity(long time)
	{
		return humidity;
	}
	public double getCloudness(long time)
	{
		return cloudness;
	}
	public double getPressure(long time)
	{
		return pressure;
	}
	public double getUVindex(long time)
	{
		return UVindex;
	}
	public double getVisibility(long time)
	{
		return visibility;
	}

	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		while(true)
		{
			// 计算休眠时间，并更新当前时刻的天气值
			timestamp += (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			this.temperature = getTemperature(timestamp);
			this.humidity = getHumidity(timestamp);
			this.cloudness = getCloudness(timestamp);
			this.pressure = getPressure(timestamp);
			this.UVindex = getUVindex(timestamp);
			this.visibility = getVisibility(timestamp);
			
			// 休眠一段时间再进行刷新
			timestampStart = System.currentTimeMillis();
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	// 仿真中的系统时间，从1970年1月1日 0:00 开始计算经过的ms数
	private long timestamp = 0;
	private double temperature;
	private double humidity;
	private double cloudness;
	private double pressure;
	private double UVindex;
	private double visibility;

	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = 1000;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = 300;
	
}
