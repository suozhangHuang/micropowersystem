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
	
	// ʵʱ���ݼ���
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
	
	// ʵʱ���ݸ���
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
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
	
	// ��վ����
	private double voltageBase;
	private double powerBase;
	private final double minWindSpeed = 4;
	private final double maxWindSpeed = 10;
	private final double curtailingSpeed = 20;
	
	// ��վ����
	private double energyMeter = 0;
	
	// ������Ϣ
	private Weather weather;
	private WeatherForecast weatherForecast;
	private long timestamp;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
}
