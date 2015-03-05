import java.rmi.*;
import G13Log.*;
import G13Components.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.Math;
import java.util.*;
import java.io.*;

import jango.*;


public class crashClient
{
    static String message = "blank";

    public static void main(String args[])
	{
	    if (System.getSecurityManager() == null) {
		System.setSecurityManager(new DSRMISecurityManager());
	    }

			if(!(args.length == 5 || args.length == 3))
			{
				System.err.println("Error: usage To set crash points $0 RMIRemoteHost RMIPort RMIObjName CrashPoint status");
				System.err.println("Error: usage To view crash points $0 RMIRemoteHost RMIPort RMIObjName ");
				System.exit(1);
			}

		try
		{
		    JangoRemoteServer jrs = (JangoRemoteServer)(LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]))).lookup(args[2]);

				if(args.length == 5)
				{
					System.out.println(jrs.setCrashPoint(args[3], Boolean.parseBoolean(args[4])));
					return;
				}

				System.out.println("Server status is " + jrs.getServerStatus());

				Hashtable <String, Boolean> crashPoints = jrs.getCrashPoints();
				Enumeration<String> keys = crashPoints.keys();

				while(keys.hasMoreElements())
				{
					String cp = keys.nextElement();
					boolean status = crashPoints.get(cp).booleanValue();

					System.out.println((status?"[ON] ":"[OFF]")+cp);
				}

		} 
	    catch (Exception e) 
		{	
		    System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
		}
	}
}
