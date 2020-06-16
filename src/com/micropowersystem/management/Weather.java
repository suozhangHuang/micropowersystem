package com.micropowersystem.management;

public class Weather extends Thread
{
	public Weather(double temperature,
			double humidity,
			double cloudness,
			double pressure,
			double UVindex,
			double visibility,
			double windSpeed)
	{
		this.temperature = temperature;
		this.humidity = humidity;
		this.cloudness = cloudness;
		this.pressure = pressure;
		this.UVindex = UVindex;
		this.visibility = visibility;
		this.windSpeed = windSpeed;
		
		// �����Զ������߳�
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
		this.windSpeed = 0;
	}
	
	// ��ȡ��ǰʱ���µ�����
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
	public double getWindSpeed()
	{
		return windSpeed;
	}

	// ��ȡָ��ʱ���µ�����ֵ
	// TODO ʵ�ָ���ָ��ʱ���µ�����ֵ�Ĺ���
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
	public double getWindSpeed(long time)
	{
		return windSpeed;
	}

	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		while(true)
		{
			// ��������ʱ�䣬�����µ�ǰʱ�̵�����ֵ
			timestamp += (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			this.temperature = getTemperature(timestamp);
			this.humidity = getHumidity(timestamp);
			this.cloudness = getCloudness(timestamp);
			this.pressure = getPressure(timestamp);
			this.UVindex = getUVindex(timestamp);
			this.visibility = getVisibility(timestamp);
			this.windSpeed = getWindSpeed(timestamp);
			
			// ����һ��ʱ���ٽ���ˢ��
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

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	private double temperature;
	private double humidity;
	private double cloudness;
	private double pressure;
	private double UVindex;
	private double visibility;
	private double windSpeed;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
	
}
