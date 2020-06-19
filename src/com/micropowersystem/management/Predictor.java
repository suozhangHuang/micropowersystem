package com.micropowersystem.management;

import java.util.Date;

public class Predictor
{
	public Predictor()
	{
		
	}
	
	public void addWindPowerCapacity(double newCapacity)
	{
		this.windPowerCapacity += newCapacity;
	}
	
	public void addSolarPanelArea(double newArea)
	{
		this.solarPanelArea += newArea;
	}
	
	public void setWeatherForecast(WeatherForecast weatherForecast)
	{
		this.weatherForecast = weatherForecast;
	}
	
	public double getSolarPanelPrediction(long time)
	{
		if(weatherForecast != null)
		{
			return getSolarPanelPower(weatherForecast.getTemperature(time),
					weatherForecast.getRadiancy(time),
					weatherForecast.getCloudness(time));
		}
		else
			return 0;
	}
	
	public double getWindPowerPrediction(long time)
	{
		if(weatherForecast != null)
			return getWindTurbinePower(weatherForecast.getWindSpeed(time));
		else
			return 0;
	}

	private WeatherForecast weatherForecast = null;
	private double windPowerCapacity = 0;
	private double solarPanelArea = 0;
	
	/************************̫����Ԥ��*****************************/
	// ̫����Ԥ��ʹ�õ�Ĭ������
	private double nominalEfficiency = 0.158;
	private double beta = 0.004;
	
	private double getSolarPanelPower(double temperature, double radiancy,double cloudness)
	{
		// Ԥ��ʱʹ�õ�����
		double eta = nominalEfficiency*(1-beta*(temperature-25));
		return eta*solarPanelArea*getSurfaceRadiancy(radiancy,cloudness) /1000;
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
	
	/**************************����Ԥ��******************************/
	// ����Ԥ��ʱʹ�õ�Ĭ�ϲ���
	private double minWindSpeed = 4;
	private double nomialWindSpeed = 12;
	private double curtailingSpeed = 25;
	private double getWindTurbinePower(double windSpeed)
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
			return windPowerCapacity;
		}

		return windPowerCapacity * Math.sin((windSpeed - minWindSpeed) / (nomialWindSpeed - minWindSpeed) * Math.PI/2);
	}
	
}
