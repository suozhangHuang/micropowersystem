package com.micropowersystem.management;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.jfree.data.time.*;

public class Manager extends Thread implements Management
{
	public Manager()
	{
		totalPower.put(GENERATOR, new TimeSeries("GENERATOR"));
		totalPower.put(STORAGE, new TimeSeries("STORAGE"));
		totalPower.put(USER, new TimeSeries("USER"));
		totalPower.put(POWERSYSTEM, new TimeSeries("POWERSYSTEM"));
	}
	
	public void setWeatherForecast(WeatherForecast weatherForecast)
	{
		this.weatherForecast = weatherForecast;
		this.predictor.setWeatherForecast(weatherForecast);
	}
	
	public void setPowerSystem(PowerSystem powerSystem)
	{
		String name = "PowerSystem";
		this.powerSystem = powerSystem;
		powerSystemPower.put(name, new TimeSeries(name));
		powerSystemVoltage.put(name, new TimeSeries(name));
		powerSystemInfo.put(name, powerSystem.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addGenerator(Generator generator)
	{
		String name;
		if(generator instanceof WindTurbine)
		{
			name = String.format("WindTurbine%d", generators.size());
			predictor.addWindPowerCapacity(generator.getPower(Generator.NOMIAL));
		}
		else if(generator instanceof SolarPanel)
		{
			name = String.format("SolarPanel%d", generators.size());
			predictor.addSolarPanelArea(((SolarPanel)generator).getPanelArea());
		}
		else
		{
			name = String.format("Generator%d", generators.size());
		}
		generators.put(name, generator);
		generatorPower.put(name, new TimeSeries(name));
		generatorVoltage.put(name, new TimeSeries(name));
		generatorInfo.put(name, generator.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addUser(User user)
	{
		String name = null;
		switch(user.getType())
		{
		case User.OFFICE:
			name = String.format("Office%d", users.size());
			break;
		case User.FAMILY:
			name = String.format("Family%d", users.size());
			break;
		case User.FACTORY:
			name = String.format("Factory%d", users.size());
			break;
		default:
			name = String.format("User%d", users.size());
			break;
		}
		users.put(name, user);
		userPower.put(name, new TimeSeries(name));
		userVoltage.put(name, new TimeSeries(name));
		userInfo.put(name, user.getInfo());
		energyMeters.put(name, 0.0);
	}
	
	public void addStorage(Storage storage)
	{
		String name = String.format("Storage%d", storages.size());
		storages.put(name, storage);
		storagePower.put(name, new TimeSeries(name));
		storageVoltage.put(name, new TimeSeries(name));
		storageEnergy.put(name, new TimeSeries(name));
		storageInfo.put(name, storage.getInfo());
		energyMeters.put(name, 0.0);
		
		maxStorageInputPower += storage.getMaxInputPower();
		maxStorageOutputPower += storage.getMaxOutputPower();
		maxStorageEnergy += storage.getCapacity();
	}
	
	public void setDataHandler(DataHandler dataHandler)
	{
		this.dataHandler = dataHandler;
	}
	
	@Override
	public boolean getManagerStatus()
	{
		return initialized;
	}

	@Override
	public Vector<String> getNames(int TYPE)
	{
		Vector<String> names = new Vector<String>();
		switch(TYPE)
		{
		case GENERATOR:
			for(String gName:this.generators.keySet())
			{
				names.add(gName);
			}
			return names;
		case STORAGE:
			for(String sName:this.storages.keySet())
			{
				names.add(sName);
			}
			return names;
		case USER:
			for(String uName:this.users.keySet())
			{
				names.add(uName);
			}
			return names;
		case POWERSYSTEM:
			names.add("PowerSystem");
			return names;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getPowerTimeSeries(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorPower;
		case STORAGE:
			return this.storagePower;
		case USER:
			return this.userPower;
		case POWERSYSTEM:
			return this.powerSystemPower;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getVoltageTimeSeries(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorVoltage;
		case STORAGE:
			return this.storageVoltage;
		case USER:
			return this.userVoltage;
		case POWERSYSTEM:
			return this.powerSystemVoltage;
		}
		return null;
	}

	@Override
	public TimeSeries getPrices(int TYPE)
	{
		if(TYPE == USER)
			return this.sellingPrice;
		if(TYPE == POWERSYSTEM)
			return this.buyingPrice;
		return null;
	}

	@Override
	public TimeSeries getTotalPowerTimeSeries(int TYPE)
	{
		return totalPower.get(TYPE);
	}

	@Override
	public HashMap<String, String> GetInfo(int TYPE)
	{
		switch(TYPE)
		{
		case GENERATOR:
			return this.generatorInfo;
		case STORAGE:
			return this.storageInfo;
		case USER:
			return this.userInfo;
		case POWERSYSTEM:
			return this.powerSystemInfo;
		}
		return null;
	}

	@Override
	public HashMap<String, TimeSeries> getForecastPower()
	{
		return prediction;
	}
	
	@Override
	public HashMap<String, TimeSeries> getStorageEnergy()
	{
		return this.storageEnergy;
	}

	@Override
	public boolean sendEmail(String receiveMailAccount)
	{
		try
		{
			this.email.setReceiverAccount(receiveMailAccount); 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = null;
			synchronized(this)
			{
				dateString = formatter.format(timestamp);
			}
			String title = String.format("΢����ϵͳ��Ϣ %s", dateString);
			
			String body = String.format("����΢����ϵͳ����Ϣ:\n    �𾴵��û����ã����Ѿ�����%.2f�ȣ�����%.2fԪ��", 
					accumulatedOutputEnergy/3600 - accumulatedInputEnergy/3600,
					accumulatedIncome-accumulatedCost);
			
			this.email.sendEmail(title, body);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true; 
	}
	
	// ͣ��Ԥ������
	// �������ʱ��������õ�ʱ���ǺϷ��ģ���ô����ֱ�Ӹ���
	public void outageWarning(Date begin, Date end)
	{
		Date currentTime = new Date(timestamp);
		if(currentTime.before(begin) && end.after(begin))
		{
			outageExpected = true;
			this.begin = begin;
			this.end = end;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			System.out.printf("���յ�ͣ��Ԥ����Ԥ��ͣ��ʱ��Ϊ\n%s - %s\n", sdf.format(begin), sdf.format(end));
		}
	}
	
	public void outageWarningCancel()
	{
		outageExpected = false;
	}
	
	public void run()
	{
		long timestampStart = System.currentTimeMillis();
		long timeDelta = 0;
		while(true)
		{
			// ��������ʱ��
			timeDelta = (System.currentTimeMillis() - timestampStart)*TIME_SCALE - timestamp;
			timestamp = (System.currentTimeMillis() - timestampStart)*TIME_SCALE;
			
			// ��õ�ǰ��ʱ��
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(timestamp));
			RegularTimePeriod regularTimePeriod = new FixedMillisecond(timestamp);

			/************************ģ��ͣ��*************************/
			
			// ��ǰ12h����Ԥ��
			if(!this.outageExpected)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				try
				{
					if(timestamp > sdf.parse("1970.01.02 12:00:00").getTime() 
							&& timestamp < sdf.parse("1970.01.02 23:59:59").getTime())
					{
						this.outageWarning(sdf.parse("1970.01.03 00:00:00"), sdf.parse("1970.01.03 23:59:59"));
					}
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			
			if(calendar.get(Calendar.DAY_OF_YEAR)==3)
			{
				this.powerSystem.setCondition(false);
			}
			else
			{
				this.powerSystem.setCondition(true);
			}
			/************************ģ��ͣ��*************************/
			
			// ��������ʱ���ڵĵ��ܱ仯
			// ������ĵ������������ʾ
			double totalEnergyGenerator = 0;
			double totalPowerGenerator = 0;
			for(String name:generators.keySet())
			{
				double generated = generators.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, generators.get(name).getWattHour());

				generatorVoltage.get(name).add(regularTimePeriod, generators.get(name).getVoltage(Generator.REALTIME));
				generatorPower.get(name).add(regularTimePeriod, generators.get(name).getPower(Generator.REALTIME));
				totalPowerGenerator += generators.get(name).getPower(Generator.REALTIME);
				totalEnergyGenerator += generated;
			}
			totalPower.get(GENERATOR).add(regularTimePeriod, totalPowerGenerator);
			
			// �û��ĵ�������������ʾ
			double totalEnergyUser = 0;
			double totalPowerUser = 0;
			for(String name:users.keySet())
			{
				double consumed = users.get(name).getWattHour() - energyMeters.get(name);
				energyMeters.put(name, users.get(name).getWattHour());

				userVoltage.get(name).add(regularTimePeriod, 220);
				userPower.get(name).add(regularTimePeriod, users.get(name).getPower());
				totalPowerUser += users.get(name).getPower();
				totalEnergyUser += consumed;
			}
			totalPower.get(USER).add(regularTimePeriod, totalPowerUser);
			
			// ����װ�õĵ�������������ʾ
			double totalEnergyStorage = 0;
			double totalPowerStorage = 0;
			currentStorageEnergy = 0;
			for(String name:storages.keySet())
			{
				double input = storages.get(name).getCurrentEnergy() - energyMeters.get(name);
				energyMeters.put(name, storages.get(name).getCurrentEnergy());
				
				storageEnergy.get(name).add(regularTimePeriod, storages.get(name).getCurrentEnergy());
				storageVoltage.get(name).add(regularTimePeriod, 220);
				storagePower.get(name).add(regularTimePeriod, storages.get(name).getInputPower());
				totalPowerStorage += storages.get(name).getInputPower();
				totalEnergyStorage += input;
				currentStorageEnergy += storages.get(name).getCurrentEnergy();
			}
			totalPower.get(STORAGE).add(regularTimePeriod, totalPowerStorage);
			
			// ����ӵ���������������������Ӧ�ĵ��
			double totalEnergyPowerSystem;
			double totalPowerPowerSystem;
			if(powerSystem.getCondition())
			{
				totalEnergyPowerSystem = totalEnergyStorage + totalEnergyUser - totalEnergyGenerator;
				totalPowerPowerSystem = totalPowerStorage + totalPowerUser - totalPowerGenerator;
				powerSystemVoltage.get("PowerSystem").add(regularTimePeriod, 220);
				powerSystemPower.get("PowerSystem").add(regularTimePeriod, totalPowerPowerSystem);
				if(totalEnergyPowerSystem>0)
				{
					accumulatedInputEnergy += totalEnergyPowerSystem;
					accumulatedCost += totalEnergyPowerSystem * powerSystem.getBuyingPrice();
				}
				else
				{
					accumulatedOutputEnergy += -totalEnergyPowerSystem;
					accumulatedIncome += -totalEnergyPowerSystem * powerSystem.getSellingPrice();
				}
				
				// ��dataHandler���͵�ǰ���ʽ���Ϣ
				if(this.dataHandler != null)
				{
					ArrayList<Double> prices = new ArrayList<Double>();
					prices.add(accumulatedIncome);
					prices.add(accumulatedCost);
					prices.add(accumulatedOutputEnergy/3600);
					prices.add(accumulatedInputEnergy/3600);
					dataHandler.OnDataChanged(prices);
				}
				totalPower.get(POWERSYSTEM).add(regularTimePeriod, totalPowerPowerSystem);
			}
			else
			{
				totalEnergyPowerSystem = 0;
				totalPowerPowerSystem = 0;
				powerSystemVoltage.get("PowerSystem").add(regularTimePeriod, 0);
				powerSystemPower.get("PowerSystem").add(regularTimePeriod, totalPowerPowerSystem);
				totalPower.get(POWERSYSTEM).add(regularTimePeriod, totalPowerPowerSystem);
			}
			
			// ͳ�Ƶ����Ϣ
			buyingPrice.add(regularTimePeriod, powerSystem.getBuyingPrice());
			sellingPrice.add(regularTimePeriod, powerSystem.getSellingPrice());
			
			if(pointCount < maxPointCount)
			{
				avgBuyingPrice = (avgBuyingPrice * pointCount + powerSystem.getBuyingPrice())/(pointCount + 1);
				avgSellingPrice = (avgSellingPrice * pointCount + powerSystem.getSellingPrice())/(pointCount + 1);
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
				pointCount++;
			}
			else
			{
				avgBuyingPrice += (powerSystem.getBuyingPrice() - buyingPriceList.removeLast())/maxPointCount;
				buyingPriceList.addFirst(powerSystem.getBuyingPrice());
				avgSellingPrice += (powerSystem.getSellingPrice() - sellingPriceList.removeLast())/maxPointCount;
				sellingPriceList.addFirst(powerSystem.getSellingPrice());
			}
			
			// ���ͣ��Ԥ��ʱ���Ƿ��Ѿ���ȥ
			if(this.outageExpected)
			{
				if(this.end.before(new Date(timestamp)))
					this.outageExpected = false;
			}
			
			// ������һʱ��εĴ���װ�ò���
			// ��Ҫ����Ƿ�ͣ�磬�Լ��Ƿ���ͣ��Ԥ��
			// ���û��ͣ��
			if(powerSystem.getCondition())
			{
				// ����û�д���ͣ��״̬
				for(String name:users.keySet())
				{
					users.get(name).setBlackedOut(false);
				}
				
				// ���û��ͣ��Ԥ�������������д���
				if(!this.outageExpected)
				{
					// �۸����ƽ��
					if(avgSellingPrice > powerSystem.getBuyingPrice()*STOP_CONSUMING_RATIO)
					{
						// ����۸�Զ����ƽ������ô�����
						if(avgSellingPrice > powerSystem.getBuyingPrice()*BUY_RATIO)
						{
							setStorageInputPower(maxStorageInputPower);
						}
						// ����ֹͣ����װ�õĹ���
						else
						{
							setStorageInputPower(0);
						}
					}
					// �۸����ƽ��
					if(avgBuyingPrice < powerSystem.getSellingPrice()*START_CONSUMING_RATIO)
					{
						// ����۸�Զ����ƽ������ô������
						if(avgBuyingPrice < powerSystem.getSellingPrice()*SELL_RATIO)
						{
							setStorageInputPower(-maxStorageOutputPower);
						}
						// ����ʹ�ô���װ�ù���
						else
						{
							setStorageInputPower(-totalPowerUser + totalPowerGenerator);
						}
					}
				}
				// �������ͣ�緢��������Сʱ���ϣ���ô������Դ���װ�õĵ��ܽ�������
				else if(this.begin.getTime() - timestamp > 6*60*60*1000)
				{
					// �۸����ƽ��
					if(avgSellingPrice > powerSystem.getBuyingPrice()*STOP_CONSUMING_RATIO)
					{
						// ����۸�Զ����ƽ������ô�����
						if(avgSellingPrice > powerSystem.getBuyingPrice()*BUY_RATIO)
						{
							setStorageInputPower(maxStorageInputPower);
						}
						// ����ֹͣ����װ�õĹ���
						else
						{
							setStorageInputPower(0);
						}
					}
					// �۸����ƽ��
					if(avgBuyingPrice < powerSystem.getSellingPrice()*START_CONSUMING_RATIO)
					{
						// ֻ����ʹ�ô���װ�ù��磬��ֹ����
						setStorageInputPower(-totalPowerUser + totalPowerGenerator);
					}
				}
				// �����ͣ��Ԥ������ô��ͣ�翪ʼǰ��Сʱ�ڣ���Ҫ�Դ���װ�ý��г���
				// ȷ����ͣ�翪ʼ֮ǰ������װ�ô���������״̬
				else
				{
					setStorageInputPower(maxStorageInputPower);
				}
			}
			// ����ͣ������
			else
			{
				// ����ͣ�����⣬�����������ɴ���װ���ṩ
				setStorageInputPower(-totalPowerUser + totalPowerGenerator);
				
				// �������ֵ����30%����ͣ�������õ�
				// �������ֵ����30%����ͣ�칫�ҹ���
				// �������ֵ����5%����ͣ��ͥ����
				for(String name:users.keySet())
				{
					if(users.get(name).getType() == User.FACTORY)
					{
						if(maxStorageEnergy*0.3 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else if(maxStorageEnergy*0.95 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
					else if(users.get(name).getType() == User.OFFICE)
					{
						if(maxStorageEnergy*0.3 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else if(maxStorageEnergy*0.95 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
					else if(users.get(name).getType() == User.FAMILY)
					{
						if(maxStorageEnergy*0.05 > currentStorageEnergy)
							users.get(name).setBlackedOut(true);
						else  if(maxStorageEnergy*0.3 < currentStorageEnergy)
							users.get(name).setBlackedOut(false);
					}
				}
			}
			
			// Ԥ�ⷢ�����
			if(prediction.size() == 0)
			{
				prediction.put("WindTurbine", new TimeSeries("WindTurbine"));
				prediction.put("SolarPanel", new TimeSeries("SolarPanel"));
				long time0 = timestamp % (1000*60*60);
				for(int i=1; i<48; i++)
				{
					long time = time0 + i*1000*60*60;
					double wind = this.predictor.getWindPowerPrediction(time);
					double solar = this.predictor.getSolarPanelPrediction(time);
					prediction.get("WindTurbine").add(new FixedMillisecond(time), wind);
					prediction.get("SolarPanel").add(new FixedMillisecond(time), solar);
					
					System.out.printf("PREDICTION:%f %f\n", wind, solar);
				}
				nextPredictingTime = time0 + 1000*60*60;
			}
			else if(nextPredictingTime <= timestamp)
			{
				long time = nextPredictingTime + 48*1000*60*60;
				double wind = this.predictor.getWindPowerPrediction(time);
				double solar = this.predictor.getSolarPanelPrediction(time);
				prediction.get("WindTurbine").add(new FixedMillisecond(time), wind);
				prediction.get("SolarPanel").add(new FixedMillisecond(time), solar);
				
				System.out.printf("PREDICTION:%f %f\n", wind, solar);
				nextPredictingTime += 1000*60*60;
			}
			
			// ��ǳ�ʼ�����
			initialized = true;
			
			// ���߲��ȴ���һ�εĴ���
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// �Դ���װ�ù��ʽ����趨��ʹ�������빦��Ϊָ��ֵ
	private void setStorageInputPower(double inputPower)
	{
		for(String name:storages.keySet())
		{
			Storage storage = storages.get(name);
			// �趨�����빦�ʳ������ֵ����ȫ�������豸���趨�����ֵ
			if(inputPower>maxStorageInputPower)
			{
				storage.setInputPower(storage.getMaxInputPower());
			}
			// �趨��������ʳ������ֵ����ȫ�������豸���趨�����ֵ
			else if(inputPower < -maxStorageOutputPower)
			{
				storage.setInputPower(-storage.getMaxOutputPower());
			}
			// �趨�Ĺ��ʺ�����������빦������ֵ������������豸�Ĺ���
			else
			{
				if(inputPower>0)
				{
					storage.setInputPower(inputPower * storage.getMaxInputPower()/maxStorageInputPower);
				}
				else
				{
					storage.setInputPower(inputPower * storage.getMaxOutputPower()/maxStorageOutputPower);
				}
			}
		}
	}
	
	// ����Ԥ���뷢��Ԥ��
	private WeatherForecast weatherForecast = null;
	private Predictor predictor = new Predictor();
	
	// ��¼������豸
	private PowerSystem powerSystem;
	private HashMap<String, Generator> generators = new HashMap<String, Generator>();
	private HashMap<String, User> users = new HashMap<String, User>();
	private HashMap<String, Storage> storages = new HashMap<String, Storage>();
	
	// ��¼��һ���豸�ĵ�����
	private HashMap<String, Double> energyMeters = new HashMap<String, Double>();

	// �豸����״����Ϣ
	private HashMap<String, TimeSeries> powerSystemVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> generatorVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> userVoltage = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> storageVoltage = new HashMap<String, TimeSeries>();
	
	private HashMap<String, TimeSeries> powerSystemPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> generatorPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> userPower = new HashMap<String, TimeSeries>();
	private HashMap<String, TimeSeries> storagePower = new HashMap<String, TimeSeries>();
	
	private HashMap<String, TimeSeries> storageEnergy = new HashMap<String, TimeSeries>();
	private HashMap<Integer, TimeSeries> totalPower = new HashMap<Integer, TimeSeries>();
	
	// �����Ϣ
	private TimeSeries sellingPrice = new TimeSeries("sellingPrice");
	private TimeSeries buyingPrice = new TimeSeries("buyingPrice");
	
	// �ƶ�����ʱʹ��60��������Ĺ���ƽ��ֵ
	private double avgBuyingPrice = 0;
	private double avgSellingPrice = 0;
	private LinkedList<Double> buyingPriceList = new LinkedList<Double>();
	private LinkedList<Double> sellingPriceList = new LinkedList<Double>();
	private int pointCount = 0;
	private final int maxPointCount = 1000;
	
	// ͳ�ƴ����豸�������
	private double maxStorageInputPower = 0;
	private double maxStorageOutputPower = 0;
	private double maxStorageEnergy = 0;
	private double currentStorageEnergy = 0;
	
	// ָ������ʱʹ�õĲ��� 
	// ��avgSellingPrice > buyingPrice * STOP_CONSUMING_RATIO ʱֹͣʹ�ô���װ�õĴ���
	private double STOP_CONSUMING_RATIO = 1.0;
	// ��avgBuyingPrice < sellingPrice * START_CONSUMING_RATIO ʱ��ʼʹ�ô���װ�õĴ���
	private double START_CONSUMING_RATIO = 1.0;
	// ��avgSellingPrice > buyingPrice * BUY_RATIO ʱ��ʼ�ӵ������
	private double BUY_RATIO = 0.9;
	// ��avgBuyingPrice < sellingPrice * SELL_RATIO ʱ���������
	private double SELL_RATIO = 0.8;
	
	// �豸��info
	private HashMap<String, String> powerSystemInfo = new HashMap<String, String>();
	private HashMap<String, String> generatorInfo = new HashMap<String, String>();
	private HashMap<String, String> userInfo = new HashMap<String, String>();
	private HashMap<String, String> storageInfo = new HashMap<String, String>();
	
	// �������ݵ�Ԥ��ֵ
	private HashMap<String, TimeSeries> prediction = new HashMap<String, TimeSeries>();
	private long nextPredictingTime = 0;
	
	// ��ʼ����ɱ��
	private boolean initialized = false;

	// �����е�ϵͳʱ�䣬��1970��1��1�� 0:00 ��ʼ���㾭����ms��
	private long timestamp = 0;
	// �����е�ˢ��ʵ�ʼ��ʱ��(ms)
	private final long REFRESH_INTERVAL = SimulationSetting.REFRESH_INTERVAL*3;
	// ����ʱ����ʵ��ʱ��ı�ֵ
	// ������ÿ����1000ms����Ӧϵͳ����5min
	private final long TIME_SCALE = SimulationSetting.TIME_SCALE;
	
	// ���ۼ�������ص���Ϣ
	private DataHandler dataHandler = null;
	private double accumulatedIncome = 0;
	private double accumulatedCost = 0;
	private double accumulatedInputEnergy = 0;
	private double accumulatedOutputEnergy = 0;
	
	// �����ʼ�����
	private Email email = new Email();
	
	// ͣ��Ԥ��ʹ�õı���
	private boolean outageExpected = false;
	private Date begin = null;
	private Date end = null;

}
