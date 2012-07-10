package com.orange.labs.hep.android.common;

/**
 * This class gathers all the periods used by the different asynchronous tasks to regulate it's sleep time
 * */
public class Periods {

	public static final long CALL_MONITORING_PERIOD = 60 * 1000;
	public static final long PHONE_MONITORING_PERIOD = 60 * 1000;
	public static final long SMS_MONITORING_PERIOD = 60 * 1000;
	public static final long EXPLICIT_MONITORING_PERIOD = 60 * 1000;
	
}
