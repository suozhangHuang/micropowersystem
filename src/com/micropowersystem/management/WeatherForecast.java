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
		return weather.getTemperature()+Math.random()*0.2-0.1;
	}
	public double getHumidity()
	{
		return weather.getHumidity();
	}
	public double getCloudness()
	{
		double cloudness = weather.getCloudness();
		cloudness = Math.random()*0.1-0.05;
		if(cloudness>=1)
			cloudness = 0.99;
		if(cloudness<0)
			cloudness = 0;
		return cloudness;
	}
	public double getPressure()
	{
		return weather.getPressure();
	}
	public double getRadiancy()
	{
		return weather.getRadiancy();
	}
	public double getVisibility()
	{
		return weather.getVisibility();
	}
	public double getWindSpeed()
	{
		return weather.getWindSpeed()+Math.random()*2-1;
	}
	
	// 获取指定之间下的天气预测值
	public double getTemperature(long time)
	{
		return weather.getTemperature(time)+Math.random()*0.2-0.1;
	}
	public double getHumidity(long time)
	{
		return weather.getHumidity(time);
	}
	public double getCloudness(long time)
	{
		double cloudness = weather.getCloudness(time);
		cloudness = Math.random()*0.1-0.05;
		if(cloudness>=1)
			cloudness = 0.99;
		if(cloudness<0)
			cloudness = 0;
		return cloudness;
	}
	public double getPressure(long time)
	{
		return weather.getPressure(time);
	}
	public double getRadiancy(long time)
	{
		return weather.getRadiancy(time);
	}
	public double getVisibility(long time)
	{
		return weather.getVisibility(time);
	}
	public double getWindSpeed(long time)
	{
		return weather.getWindSpeed(time)+Math.random()*2-1;
	}

	private Weather weather;
}
