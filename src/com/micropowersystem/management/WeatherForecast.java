package com.micropowersystem.management;

public class WeatherForecast
{
	public WeatherForecast()
	{
		
	}
	
	public void setWeather(Weather weather)
	{
		this.weather = weather;
	}
	
	// 获取当前时间下的天气预测值
	// TODO 实现获取预测值的功能
	public double getTemperature()
	{
		return weather.getTemperature();
	}
	public double getHumidity()
	{
		return weather.getHumidity();
	}
	public double getCloudness()
	{
		return weather.getCloudness();
	}
	public double getPressure()
	{
		return weather.getPressure();
	}
	public double getUVindex()
	{
		return weather.getUVindex();
	}
	public double getVisibility()
	{
		return weather.getVisibility();
	}

	// 获取指定之间下的天气预测值
	public double getTemperature(long time)
	{
		return weather.getTemperature(time);
	}
	public double getHumidity(long time)
	{
		return weather.getHumidity(time);
	}
	public double getCloudness(long time)
	{
		return weather.getCloudness(time);
	}
	public double getPressure(long time)
	{
		return weather.getPressure(time);
	}
	public double getUVindex(long time)
	{
		return weather.getUVindex(time);
	}
	public double getVisibility(long time)
	{
		return weather.getVisibility(time);
	}

	private Weather weather;
}
