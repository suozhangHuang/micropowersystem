package com.micropowersystem.management;

public class Weather extends Thread
{
	public Weather(double temperature,
			double humidity,
			double cloudness,
			double pressure,
			double Radiancy,
			double visibility,
			double windSpeed)
	{
		this.temperature = temperature;
		this.humidity = humidity;
		this.cloudness = cloudness;
		this.pressure = pressure;
		this.Radiancy = Radiancy;
		this.visibility = visibility;
		this.windSpeed = windSpeed;
	}
	
	public Weather()
	{
		this.temperature = 0;
		this.humidity = 0;
		this.cloudness = 0;
		this.pressure = 0;
		this.Radiancy = 0;
		this.visibility = 0;
		this.windSpeed = 0;
	}
	
	public void setType(int type1,int type2,int type3) {
		TYPE1 = type1;
		TYPE2 = type2;
		TYPE3 = type3;
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
	public double getRadiancy()
	{
		return Radiancy;
	}
	public double getVisibility()
	{
		return visibility;
	}
	public double getWindSpeed()
	{
		return windSpeed;
	}

	// 获取指定时间下的天气值
	// TODO 实现更新指定时间下的天气值的功能
	public double getTemperature(long time)
	{
		double timeInHour = (double)time/1000/60/60;
		double temperature = 0;
		
		if(timeInHour>=0 && timeInHour<=4)
			temperature = 29;
		if(timeInHour>4 && timeInHour<=7)
			temperature = 28;
		if(timeInHour>7 && timeInHour<=9)
			temperature = 31;
		if(timeInHour>9 && timeInHour<=15)
			temperature = 35;
		if(timeInHour>15 && timeInHour<=16)
			temperature = 36;
		if(timeInHour>16 && timeInHour<=18)
			temperature = 35;
		if(timeInHour>18 && timeInHour<=21)
			temperature = -1*(timeInHour-18)+35;
		if(timeInHour>21 && timeInHour<=24)
			temperature = 31;
		if(TYPE1 == Weather.HOT)
			return temperature;
		else
			return  temperature-20;
		

	}
	public double getHumidity(long time)
	{
		return humidity;
	}
	public double getCloudness(long time)
	{
		double timeInHour = time*1.0/1000/60/60;
		double cloudness = 0;
		
		if(timeInHour>=0 && timeInHour<=4)
			cloudness = 0.9;
		if(timeInHour>4 && timeInHour<=7)
			cloudness = 0.95;
		if(timeInHour>7 && timeInHour<=9)
			cloudness = 0.9;
		if(timeInHour>9 && timeInHour<=15)
			cloudness = 0.85;
		if(timeInHour>15 && timeInHour<=16)
			cloudness = 0.7;
		if(timeInHour>16 && timeInHour<=18)
			cloudness = 0.95;
		if(timeInHour>18 && timeInHour<=21)
			cloudness = 0.9;
		if(timeInHour>21 && timeInHour<=24)
			cloudness = 0.8;
		if(TYPE3 == Weather.CLOUDY)
			return cloudness;
		else
			return cloudness*0.5-0.2;
	}
	public double getPressure(long time)
	{
		return pressure;
	}
	public double getRadiancy(long time)
	{
		double timeInHour = time*1.0/1000/60/60;
		double theta = 3.1415926/14*(timeInHour-6);
		if((timeInHour>=0&&timeInHour<=6)||(timeInHour>20&&timeInHour<=24))
			return 0;
		if((timeInHour>6&&timeInHour<=7)||(timeInHour>19&&timeInHour<=20))
			return Math.pow(Math.sin(theta),2)*1200;
		if((timeInHour>7&&timeInHour<=10)||(timeInHour>16&&timeInHour<=19))
			return 1.3901*Math.pow(Math.sin(theta),2)*1200-0.0868*Math.sin(theta)*1200;
		if((timeInHour>10&&timeInHour<=16))
			return Math.sin(theta)*1200;
		return 0;
	}
	public double getVisibility(long time)
	{
		return visibility;
	}
	public double getWindSpeed(long time)
	{
		double timeInHour = time*1.0/1000/60/60;
		double ws = 0;
		if(timeInHour>=0&&timeInHour<4)
			ws = 5;
		if(timeInHour>=4&&timeInHour<8)
			ws = -0.05*(timeInHour-4)+5;
		if(timeInHour>=8&&timeInHour<14)
			ws = (timeInHour-8)/6+4.8;
		if(timeInHour>=14&&timeInHour<=24)
			ws = -1*(timeInHour-14)*1.8/10+5.8;
		if(TYPE2 == Weather.NOTWINDY) {
			return ws;
		}else {
			return ws+10;
		}
		
	}

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
			
			
			
			this.temperature = getTemperature((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.humidity = getHumidity((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.cloudness = getCloudness((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.pressure = getPressure((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.Radiancy = getRadiancy((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.visibility = getVisibility((timestamp+8*60*60*1000)%(24*60*60*1000));
			this.windSpeed = getWindSpeed((timestamp+8*60*60*1000)%(24*60*60*1000));
			
			// 休眠一段时间再进行刷新
			
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
	private double Radiancy;
	private double visibility;
	private double windSpeed;
	
	private int TYPE1=0;
	private int TYPE2=0;
	private int TYPE3=0;
	
	// 集中典型的天气(分别对应TYPE1T,TYPE2,TYPE3)
	public static int HOT = 0;
	public static int COLD = 1;
	
	public static int NOTWINDY = 0;
	public static int WINDY = 1;
	
	public static int NOTCLOUDY = 0;
	public static int CLOUDY = 1;
	
	
	// 仿真中的刷新实际间隔时间(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
	
}
