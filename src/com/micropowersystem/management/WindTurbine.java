package com.micropowersystem.management;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class WindTurbine extends Thread implements Generator
{
	public WindTurbine(String infoFileName) throws IOException
	{
		Properties properties = new Properties();
		properties.load(new FileInputStream(infoFileName));

		name = properties.getProperty("name");
		voltageBase = Double.parseDouble(properties.getProperty("voltageBase"));
		powerBase = Double.parseDouble(properties.getProperty("powerBase"));
		minWindSpeed = Double.parseDouble(properties.getProperty("minWindSpeed"));
		nomialWindSpeed = Double.parseDouble(properties.getProperty("nomialWindSpeed"));
		curtailingSpeed = Double.parseDouble(properties.getProperty("curtailingSpeed"));

		bladeRadius = Double.parseDouble(properties.getProperty("bladeRadius"));

	}

	@Override
	public String getInfo()
	{
		return String.format("name %s\nvoltageBase %f\npowerBase %f\nminWindSpeed %f\nnomialWindSpeed %f\ncurtailingSpeed %f\nbladeRadius %f\n",
				name,
				voltageBase,
				powerBase,
				minWindSpeed,
				nomialWindSpeed,
				curtailingSpeed,
				bladeRadius);
	}

	@Override
	public void setWeatherCondition(Weather weather)
	{
		this.weather = weather;
	}

	@Override
	public void setWeatherForecast(WeatherForecast weatherForecast)
	{
		this.weatherForecast = weatherForecast;
	}

	@Override
	public double getVoltage(int type)
	{
		if (type == NOMIAL)
		{
			return this.voltageBase;
		}
		if (type == REALTIME)
		{
			return getVoltage(weather.getWindSpeed(timestamp));
		}
		return 0;
	}

	@Override
	public double getCurrent(int type)
	{
		if (type == NOMIAL)
		{
			return powerBase / voltageBase;
		}
		if (type == REALTIME)
		{
			return getCurrent(weather.getWindSpeed(timestamp));
		}
		return 0;
	}

	@Override
	public double getPower(int type)
	{
		if (type == NOMIAL)
		{
			return powerBase;
		}
		if (type == REALTIME)
		{
			return getPower(weather.getWindSpeed(timestamp));
		}
		return 0;
	}

	@Override
	public double getVoltageForecast(long time)
	{
		return getVoltage(weatherForecast.getWindSpeed(time));
	}

	@Override
	public double getCurrentForecast(long time)
	{
		return getCurrent(weatherForecast.getWindSpeed(time));
	}

	@Override
	public double getPowerForecast(long time)
	{
		return getPower(weatherForecast.getWindSpeed(time));
	}

	@Override
	public double getWattHour()
	{
		synchronized (this)
		{
			return energyMeter;
		}
	}

	@Override
	public void output()
	{
		// do something
		System.out.printf("WIND TURBINE:output power!\n");
	}

	// ʵʱ���ݼ���
	private double getVoltage(double windSpeed)
	{
		return voltageBase;
	}

	private double getCurrent(double windSpeed)
	{
		return getPower(windSpeed) / getVoltage(windSpeed);
	}

	private double getPower(double windSpeed)
	{
		if (windSpeed < minWindSpeed)
		{
			return 0;
		}
		if (windSpeed > curtailingSpeed)
		{
			return 0;
		}
		if (windSpeed > nomialWindSpeed)
		{
			return powerBase;
		}

		return powerBase * (windSpeed - minWindSpeed) / (nomialWindSpeed - minWindSpeed);
	}

	// ʵʱ���ݸ���
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while (true)
		{
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
			timeDelta = (System.currentTimeMillis() - timestampStart) * TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart) * TIME_SCALE;

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
	private String name;
	private double voltageBase;
	private double powerBase;
	private double minWindSpeed;
	private double nomialWindSpeed;
	private double curtailingSpeed;

	private double bladeRadius;

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
