package com.micropowersystem.management;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Storage extends Thread
{
	public Storage(String infoFileName) throws IOException
	{
		Properties properties = new Properties();
		properties.load(new FileInputStream(infoFileName));

		capacity = Double.parseDouble(properties.getProperty("capacity"));
		currentEnergy = 0;
		inputPower = 0;
		maxInputPower = Double.parseDouble(properties.getProperty("maxInputPower"));
		maxOutputPower= Double.parseDouble(properties.getProperty("maxOutputPower"));
		
		timestamp = 0;
	}
	
	public String getInfo()
	{
		return String.format("���ܴ���װ��\n����� %f\n������빦�� %f\n����������%f", this.capacity, this.maxInputPower, this.maxOutputPower);
	}
	
	public void setCapacity(double capacity)
	{
		synchronized(this)
		{
			this.capacity = capacity;
		}
	}
	
	public void setMaximumPower(double maxInputPower, double maxOutputPower)
	{
		this.maxInputPower = maxInputPower;
		this.maxOutputPower = maxOutputPower;
	}
	
	public double getMaxInputPower()
	{
		return maxInputPower;
	}
	
	public double getMaxOutputPower()
	{
		return maxOutputPower;
	}
	
	public double getCurrentEnergy()
	{
		synchronized(this)
		{
			return currentEnergy;
		}
	}
	
	public void setInputPower(double inputPower)
	{
		synchronized(this)
		{
			if(inputPower > maxInputPower)
				this.inputPower = maxInputPower;
			else if(inputPower < -maxOutputPower)
				this.inputPower = -maxOutputPower;
			else
				this.inputPower = inputPower;
		}
	}
	
	public double getInputPower()
	{
		return this.inputPower;
	}
	
	// ģ����ʵ����ģ�͵Ĺ�����û��ʵ��ʵ��
	public void storage()
	{
		
	}
	
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// ��������ʱ�䣬�����µ�ǰʱ�̵ĵ��
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;

			synchronized(this)
			{
				currentEnergy += inputPower * timeDelta/1000;
				if(currentEnergy > capacity)
				{
					currentEnergy = capacity;
				}
				if(currentEnergy < 0)
				{
					currentEnergy = 0;
				}
			}
			
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
	}
	
	private double maxInputPower;
	private double maxOutputPower;
	
	private double capacity;
	private double currentEnergy;
	private double inputPower;
	private long timestamp;
	
	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
}