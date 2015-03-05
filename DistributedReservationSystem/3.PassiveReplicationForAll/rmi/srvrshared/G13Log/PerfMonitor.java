package G13Log;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PerfMonitor
{
	static boolean perfEnabled;
	static boolean ignoreErrMertric;

	static String processId = "";
	static String hostName = "";
	static String DELIMITER="|";
	static String testId="RGLR";

	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd|HH|mm|ss");
	//dateFormat.format(date)

  private static final long TIME_SEC = 1000000000L;
  private static final long TIME_MIL = 1000000L;
  private static final long TIME_MIC = 1000L;
  private static final long TIME_NAN = 1L;

	public final static int START = 1;
	public final static int END = 2;
	public final static int ERR = 3;


	public final static String REQUEST_NONE="";
	public final static String REQUEST_TXN="TXN";
	public final static String REQUEST_START="START";
	public final static String REQUEST_COMMIT="COMMIT";
	public final static String REQUEST_ABORT="ABORT";

	public final static String REQUEST_LOCK="LOCK";
	public final static String REQUEST_UNLOCK="UNLOCK";

	public final static String REQUEST_DELETE="DELETE";
	public final static String REQUEST_RESERVE="RESERVE";
	public final static String REQUEST_RESERVE_CAR="RESERVE_CAR";
	public final static String REQUEST_RESERVE_HOTEL="RESERVE_HOTEL";
	public final static String REQUEST_RESERVE_FLIGHT="RESERVE_FLIGHT";

	public final static String REQUEST_RESERVE_ITINERARY="RESERVE_ITINERARY";

	static Hashtable<String,Long> oprnHist;
	static long timeResolution = TIME_MIC;

	private final static String PREFIX="PRF";

  public static void setup()
	{
		System.out.println("Initializing the perf monitor.");
		oprnHist = new Hashtable<String,Long>();

	  processId = System.getenv("PROCESS_ID");

		if(processId == null || processId.equals(""))
		  processId = ManagementFactory.getRuntimeMXBean().getName();

		try{hostName=InetAddress.getLocalHost().getHostName();}
		catch(UnknownHostException e) { hostName=System.getenv("HOSTNAME"); }

		try { perfEnabled = Boolean.parseBoolean(System.getenv("PERF_MONITOR_ENABLED")); }
		catch(Exception e) {}

		try { ignoreErrMertric = Boolean.parseBoolean(System.getenv("PERF_IGNORE_ERR_METRIC")); }
		catch(Exception e) {}

		try
		{
		 String tResol = System.getenv("PERF_TIME_RESOLUTION");
		 if(tResol != null)
		 {
		   if(tResol.equals("TIME_SEC")) timeResolution = TIME_SEC;
			 else if(tResol.equals("TIME_MIL")) timeResolution = TIME_MIL;
			 else if(tResol.equals("TIME_MIC")) timeResolution = TIME_MIC;
			 else if(tResol.equals("TIME_NAN")) timeResolution = TIME_NAN;
		 }
		}
		catch(Exception e)
		{}

		testId = System.getenv("TEST_ID");
		if (testId == null || testId.equals(""))
			testId = "RGLR";
	
	}

  private static long getDuration(long startTime, long endTime)
	{ return (endTime-startTime)/timeResolution; }

  private static void printPerfRecord(String key, String status, long duration)
	{
	  System.out.println(PREFIX+DELIMITER+dateFormat.format(new Date())+DELIMITER+hostName+DELIMITER+processId+DELIMITER+testId+DELIMITER+key+DELIMITER+status+DELIMITER+duration);
	}

	public static void recordPerf(int txId, String oprnType, String subOprn, String data, int status)
	{
		if(!perfEnabled) return;
	  recordPerf(txId, 0, oprnType, subOprn, data, status);
	}

	public static void recordPerf(int txId, int rqId, String oprnType, String subOprn, String data, int status)
	{
		if(!perfEnabled) return;

	  String key = txId + DELIMITER + oprnType + DELIMITER + (subOprn==null?"":subOprn) + DELIMITER + (data==null?"":data);

		long startTime; Long l;


		switch (status)
		{
		  case START:
				//Shouldn't happen ideally.
				//if(oprnHist.contains(key)) return;
				oprnHist.put(key, System.nanoTime());
				break;

		  case END:
					l = (Long)oprnHist.get(key);
					if(l == null) return;
					startTime = l;


					printPerfRecord(key, "OK", getDuration(startTime, System.nanoTime()));
				  oprnHist.remove(key);
			  break;


		  case ERR:
			  if(!ignoreErrMertric)
				{
					l = (Long)oprnHist.get(key);
					if(l == null) return;
					startTime = l;
					printPerfRecord(key, "ERR", getDuration(startTime, System.nanoTime()));
				}
				oprnHist.remove(key);
			  break;

			default:
		}
	}

}
