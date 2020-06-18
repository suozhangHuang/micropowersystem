package com.micropowersystem.management;

public class SimulationSetting
{
	// 仿真中的刷新实际间隔时间(ms)
	public static final long REFRESH_INTERVAL = 200;
	// 仿真时间与实际时间的比值
	// 仿真中每经过1000ms，对应系统运行5min
	public static final long TIME_SCALE = 1000;
}
