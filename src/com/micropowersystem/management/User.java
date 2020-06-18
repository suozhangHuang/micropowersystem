package com.micropowersystem.management;

import java.sql.Date;
import java.util.Calendar;

public class User extends Thread
{
	public User()
	{
		
	}
	
	public void setType(int type)
	{
		synchronized(this)
		{
			this.type = type;
		}
	}
	
	public String getInfo()
	{
		String info = "";
		switch(type)
		{

		case OFFICE:
			info = String.format("ģ��칫�������õ����������ʱ������õ�\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case FAMILY:
			info = String.format("ģ���ͥ�õ���������ϳ�����߷�\nҹ��ֻ���ļ��ж�������\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case FACTORY:
			// ģ�������͹������õ����
			// �ڹ���ʱ���и߸���
			info = String.format("ģ�������͹������õ�������ڹ���ʱ���и߸���\nƽ�����õ���:%f", this.averagePower);
			break;
			
		case DEFAULT:
			// ����
			info = String.format("����\nƽ�����õ���:0");
			break;
		}
		return info;
	}
	
	/* �����û�ƽ�����ʣ�ע�����ڷ��ļ���ƽ������ */
	public void setAveragePower(double averagePower)
	{
		synchronized(this)
		{
			this.averagePower = averagePower;
		}
	}
	
	public double getWattHour()
	{
		return currentEnergy;
	}
	
	public double getPower()
	{
		return currentPower;
	}
	
	public void userStart()
	{
		if(!started)
		{
			started = true;
			this.start();
		}
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

			// �����û����ͺ�ʱ��������û��õ繦��
			synchronized(this)
			{
				// ʵ���û��õ�����Ϣ����
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(timestamp));
				
				// ��λ��ΪkW
				switch(this.type)
				{
				case OFFICE:
					// ģ��칫�������õ����������ʱ������õ�
					// ȡ50W/m^2, 2000m^2
					// �ļ�ʱ80W/m^2 
					if(calendar.get(Calendar.HOUR) >= 8 && calendar.get(Calendar.HOUR) <= 18)
					{
						if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
							currentPower = 160*averagePower/1140;
						else
							currentPower = 100*averagePower/1140;
					}
					else
					{
						currentPower = 10*averagePower/1140;
					}
					break;
					
				case FAMILY:
					// ģ���ͥ�õ���������ϳ�����߷�
					// ҹ��ֻ���ļ��ж�������
					if(calendar.get(Calendar.HOUR) >= 18 && calendar.get(Calendar.HOUR) <= 23)
					{
						currentPower = 10*averagePower/78;
					}
					else
					{
						currentPower = 1*averagePower/78;
					}
					if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
					{
						if(calendar.get(Calendar.HOUR) >= 18 || calendar.get(Calendar.HOUR) <= 8)
							currentPower += 5*averagePower/78;
					}
					break;
					
				case FACTORY:
					// ģ�������͹������õ����
					// �ڹ���ʱ���и߸���
					if(calendar.get(Calendar.HOUR) >= 8 && calendar.get(Calendar.HOUR) <= 18)
					{
						if(calendar.get(Calendar.MONTH) >= 5 && calendar.get(Calendar.MONTH) <= 10)
							currentPower = 750*averagePower/7140;
						else
							currentPower = 700*averagePower/7140;
					}
					else
					{
						currentPower = 10*averagePower/7140;
					}
					break;
					
				case DEFAULT:
					// ����
					currentPower = 0;
					break;
				}
				
				currentEnergy += currentPower * timeDelta;
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

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	
	// �û�����
	public final int OFFICE = 0;
	public final int FAMILY = 1;
	public final int FACTORY = 2;
	public final int DEFAULT = 3;
	private int type = FAMILY;
	
	// �û�ƽ���õ���
	private double averagePower = 0;
	
	// �û���ǰʹ�ù���
	private double currentPower = 0;
	// �û���ǰ������
	private double currentEnergy = 0;
	
	// ��־�Զ������Ƿ�ʼ
	private boolean started = false;

	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = 1000;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = 300;
	
}
