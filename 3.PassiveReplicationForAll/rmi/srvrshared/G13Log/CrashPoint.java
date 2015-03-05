package G13Log;

import java.util.Hashtable;

public class CrashPoint
{

	Hashtable<String, Boolean> validCrashPoints;

	public CrashPoint()
	{
		validCrashPoints = new Hashtable<String, Boolean>(50);
	}

	public void addCrashPoint(String point)
	{ validCrashPoints.put(point, false); }

	public String setCrashPoint(String point, boolean status)
	{
		if(point == null) return "Err crash point cannot be null";

		if(point.equals("terminate_now") && status) terminate();

		Boolean b = validCrashPoints.get(point);

		if(b == null)
			return "Error: " + point + " Is not a valid crashpoint for this object";
		Trace.info("Crash point requested at " + point);
		validCrashPoints.put(point, status);

		return "Crash point set at " + point;
	}

	public Hashtable<String, Boolean> getCrashPoints() { return validCrashPoints; }

	public void crashCheck(String point)
	{
		Boolean b = validCrashPoints.get(point);

		if(b == null)
		{
			Trace.info("ERROR: crash point " + point + " is not valid");
			return;
		}

		if(b.booleanValue())
		{ 
			Trace.info("Crash point " + point + " is set, process terminating");
			terminate();
		}
	}

	protected void terminate()
	{ Trace.info("Crash"); System.exit(0); }

}
