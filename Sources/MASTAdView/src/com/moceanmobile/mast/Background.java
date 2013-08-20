package com.moceanmobile.mast;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Background
{
	// Executor used to handle asynchronous tasks.
	private static final ScheduledThreadPoolExecutor executor;
	
	static
	{
		executor = new ScheduledThreadPoolExecutor(1);
	}
	
	public static ScheduledThreadPoolExecutor getExecutor()
	{
		return executor;
	}
}
