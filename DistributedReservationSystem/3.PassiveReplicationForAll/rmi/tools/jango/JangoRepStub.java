package jango;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;

import java.util.Random;
import java.util.Vector;
import java.util.Hashtable;

import java.util.StringTokenizer;

import java.lang.management.ManagementFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;


import jango.*;

public abstract class JangoRepStub <R>// implements Remote
{
  public final static int JANGO_MAX_HASH_BUCKET_SIZE = 1024;

	protected int maxHashBucketSize;
	protected int maxRetries;

	protected String processId;

	protected String rmiHosts[];
	protected String srvrNames[];
	protected int rmiPorts[];

	protected R srvrHashBucket[];
	protected JangoRemoteServer srvrHashBucket_jrs[];
	protected ReentrantReadWriteLock hashBucketLock;

  public JangoRepStub(String rmiHosts[], String srvrNames[], int rmiPorts[])
	{ this(rmiHosts, srvrNames, rmiPorts, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

  public JangoRepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize)
	{ this(rmiHosts, srvrNames, rmiPorts, maxHashBucketSize, -1); }

  public JangoRepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize, int maxRetries)
	{
	  this.maxHashBucketSize = maxHashBucketSize;
	  this.maxRetries = (maxRetries == -1) ? rmiHosts.length : maxRetries;
	  this.rmiHosts = rmiHosts;
	  this.srvrNames = srvrNames;
	  this.rmiPorts = rmiPorts;

		srvrHashBucket = (R[])new Object[maxHashBucketSize];
		srvrHashBucket_jrs = new JangoRemoteServer[maxHashBucketSize];
		hashBucketLock = new ReentrantReadWriteLock();

		buildHashBuckets();

		processId = ManagementFactory.getRuntimeMXBean().getName();
	}

	public JangoRepStub(int numReplics, String replicConfigs)
	{ this(numReplics, replicConfigs, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

	public JangoRepStub(int numReplics, String replicConfigs, int maxHashBucketSize)
	{ this(numReplics, replicConfigs, maxHashBucketSize, -1); }

	public JangoRepStub(int numReplics, String replicConfigs, int maxHashBucketSize, int maxRetries)
	{
	  this.maxHashBucketSize = maxHashBucketSize;
	  this.maxRetries = (maxRetries == -1) ? numReplics : maxRetries;

	  rmiHosts = new String[numReplics];
		srvrNames = new String[numReplics];
		rmiPorts = new int[numReplics];
		int rCount=0;
		
		StringTokenizer replics = new StringTokenizer(replicConfigs, "(),", false);
		while(replics.hasMoreTokens())  //Parse each replica config
		{
			StringTokenizer rParams = new StringTokenizer(replics.nextToken(), "|");
			while(rParams.hasMoreTokens()) // individual param of a replica
			{
				rmiHosts[rCount] = rParams.nextToken();
				rmiPorts[rCount] = Integer.parseInt(rParams.nextToken());
				srvrNames[rCount] = rParams.nextToken();

				rCount++;
			}
		}

		srvrHashBucket = (R[])new Object[maxHashBucketSize];
		srvrHashBucket_jrs = new JangoRemoteServer[maxHashBucketSize];
		hashBucketLock = new ReentrantReadWriteLock();

		buildHashBuckets();

		processId = ManagementFactory.getRuntimeMXBean().getName();
	}


	protected void buildHashBuckets()
	{
		R srvrHashBucketSecondary[] = (R[])new Object[maxHashBucketSize];
		JangoRemoteServer srvrHashBucketSecondary_jrs[] = new JangoRemoteServer[maxHashBucketSize];

		//Find out how many virtual nodes we should generate per actual server.
		int numVirtualNodes = (maxHashBucketSize/rmiHosts.length + 1);

		int tries = 0;
		//Keep track of the number of serves we are able to connect.
		int numAvailableServers = 0;
		do
		{
			numAvailableServers = 0;
			Vector<JangoRemoteServer> passiveList = new Vector<JangoRemoteServer>(rmiHosts.length);
	
			for(int i=0; i<rmiHosts.length; i++)
			{
			  String rmiHost = rmiHosts[i];
				String srvrName = srvrNames[i];
				int rmiPort = rmiPorts[i];
	
				try
				{
				  JangoRemoteServer jrs = (JangoRemoteServer)LocateRegistry.getRegistry(rmiHost, rmiPort).lookup(srvrName+"_jango");
	
					//Lookup server status here, if it's not a master, we don't connect.
					int srvStat = jrs.getServerStatus();

					if(srvStat == JangoRemoteServer.JANGO_SERVER_PASSIVE)
						passiveList.add(jrs);

					if(srvStat != JangoRemoteServer.JANGO_SERVER_MASTER)
					  continue;
	
				  R srvrIntf = (R)LocateRegistry.getRegistry(rmiHost, rmiPort).lookup(srvrName);
	
					numAvailableServers++;
	
					//Load the virtual nodes into the actual hash table.
					for(int v:getVirtualNodeNums(numVirtualNodes, rmiHost, srvrName, rmiPort))
					{
						//System.out.println(rmiHost + ":"+rmiPort+":"+srvrName+": is allocated to virtual node :" + v);
					  srvrHashBucketSecondary[v] = srvrIntf;
					  srvrHashBucketSecondary_jrs[v] = jrs;
					}
	
				}
				catch(Exception e)
				{ 
				  System.out.println("Error:" + rmiHost + ":"+rmiPort+":"+srvrName+":"+" couldn't be connected."); 
					e.printStackTrace(); 
				}
			}

			//We have no master, but have some passives, so alert them.
			if (numAvailableServers == 0 && passiveList.size() > 0)
			{
				for(int i=0; i<passiveList.size(); i++)
				{
					try
					{
				  	if(passiveList.elementAt(i).switchToMaster())
							break;
					}
					catch(Exception e) 
					{ System.out.println("Error while asking passive to switch to master"); e.printStackTrace(); }
				}
			}

		} while (++tries < maxRetries && numAvailableServers == 0);

		//This is a catastrophe, let nature take its turn.
		if(numAvailableServers == 0)
		{
		  return;
		}

		fillHashBucket(srvrHashBucketSecondary, srvrHashBucketSecondary_jrs);

		//Swap in the new hashbucket.
		srvrHashBucket = srvrHashBucketSecondary;
		srvrHashBucket_jrs = srvrHashBucketSecondary_jrs;
	  
	}

	protected int[] getVirtualNodeNums(int numVirtualNodes, String rmiHost, String srvrName, int rmiPort)
	{
		//Create a unique repeatable random seed for each of the remote servers
	  Random rand = new Random( (rmiHost+"|"+srvrName+"|"+rmiPort).hashCode() );
		int virtualNodeNums[] = new int[numVirtualNodes];

		for(int i=0; i<numVirtualNodes; i++)
		{
		  virtualNodeNums[i] = Math.abs(rand.nextInt())%maxHashBucketSize;
		}

		return virtualNodeNums;
	}

	protected void fillHashBucket(R hashBucket[], JangoRemoteServer hashBucket_jrs[])
	{
		R prevSrvr=null;
		JangoRemoteServer prevSrvr_jrs=null;
		//Start "forward filling" any virtual nodes not occupied.
	  for(int i=0; i<hashBucket.length; i++)
		{
		  if(hashBucket[i] == null)
			{ 
				if(prevSrvr != null)
				{
					hashBucket[i] = prevSrvr; 
					hashBucket_jrs[i] = prevSrvr_jrs; 
				}
			}
			else
			{ 
				prevSrvr = hashBucket[i]; 
				prevSrvr_jrs = hashBucket_jrs[i]; 
			}
		}

		// "rollover" to the begining of hash table where there are any
		// un occupied virtual nodes.
	  for(int i=0; i<hashBucket.length; i++)
		{
		  if(hashBucket[i] == null)
			{ 
				hashBucket[i] = prevSrvr; 
				hashBucket_jrs[i] = prevSrvr_jrs; 
			}
			else
			{ break; }
		}

	}

	protected void fixHashBucket(int srvrIdx)
	{
		hashBucketLock.writeLock().lock();

		//Check if the error persists. Another thread could have fixed it by now.
		try 
		{ 
			//TODO: May be there's an efficient way of doing this verification without probing the server ?
			if(srvrHashBucket_jrs[srvrIdx].getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER) 
			{
				hashBucketLock.writeLock().unlock();
			  return; 
			}
		}
		catch(Exception e) {}

		buildHashBuckets();
		hashBucketLock.writeLock().unlock();
	}

	protected R getSrvr(int srvrIdx)
	{ return (R)(srvrHashBucket[srvrIdx]); }

	protected <N extends Number> int hash(N num)
	{ return Math.abs(7919*num.intValue() % maxHashBucketSize) ; }

	protected <O extends Object> int hash(O o)
	{ return Math.abs(o.hashCode()%maxHashBucketSize) ; }

	//implement an empty hash
	protected int hash()
	{ return Math.abs((processId + "" + Thread.currentThread().getName()).hashCode() % maxHashBucketSize); }

}
