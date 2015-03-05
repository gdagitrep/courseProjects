package jango;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.Random;
import java.util.Vector;
import java.util.Hashtable;

import java.util.StringTokenizer;

import java.lang.management.ManagementFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;


import jango.*;

public abstract class JangoRep <R extends JangoRemoteServer, T extends Remote> implements JangoRemoteServer
{
	public final static int JANGO_REPLIC_CFG_UNKNOWN = 0;
	public final static int JANGO_REPLIC_CFG_SINGLE_MASTER = 1;
	public final static int JANGO_REPLIC_CFG_ALL_MASTER = 2;

	protected int replicCfg = JANGO_REPLIC_CFG_UNKNOWN;
	protected int replicStatus = JangoRemoteServer.JANGO_SERVER_UNA;

	protected String rmiHosts[];
	protected String srvrNames[];
	protected int rmiPorts[];

	protected Hashtable<String, R> registeredReplics;

	protected int numReplics;

	protected String me_rmiHost;
	protected String me_srvrName;
	protected int me_rmiPort;
	protected T me_obj;
	protected int masterRepIdx = -1;

	//protected Vector<R> replics;
	protected Hashtable<Integer, R> replics;
	protected ReentrantReadWriteLock replicMasterLock;

  public JangoRep(String rmiHosts[], String srvrNames[], int rmiPorts[], String me_rmiHost, String me_srvrName, int me_rmiPort, int replicCfg)
	{
	  this.rmiHosts = rmiHosts;
	  this.srvrNames = srvrNames;
	  this.rmiPorts = rmiPorts;

		this.numReplics = rmiHosts.length;

	  this.me_rmiHost = me_rmiHost;
	  this.me_srvrName = me_srvrName;
	  this.me_rmiPort = me_rmiPort;

		this.replicCfg = replicCfg;

		this.me_obj = null;

		//replics = (R[])new JangoRemoteServer[numReplics];
		//replics = new Vector<R>(numReplics);
		replics = new Hashtable<Integer, R>(numReplics);
		registeredReplics = new Hashtable<String, R>(numReplics);

		replicMasterLock = new ReentrantReadWriteLock();

		//initialize();
	}

	public JangoRep(int numReplics, String replicConfigs, String me_rmiHost, String me_srvrName, int me_rmiPort, int replicCfg)
	{
	  this.rmiHosts = new String[numReplics];
		this.srvrNames = new String[numReplics];
		this.rmiPorts = new int[numReplics];

		this.numReplics = numReplics;

	  this.me_rmiHost = me_rmiHost;
	  this.me_srvrName = me_srvrName;
	  this.me_rmiPort = me_rmiPort;

		this.replicCfg = replicCfg;

		this.me_obj = null;

		int rCount=0;
		
		StringTokenizer replicsSt = new StringTokenizer(replicConfigs, "(),", false);
		while(replicsSt.hasMoreTokens())  //Parse each replica config
		{
			StringTokenizer rParams = new StringTokenizer(replicsSt.nextToken(), "|");
			while(rParams.hasMoreTokens()) // individual param of a replica
			{
				rmiHosts[rCount] = rParams.nextToken();
				rmiPorts[rCount] = Integer.parseInt(rParams.nextToken());
				srvrNames[rCount] = rParams.nextToken();

				rCount++;
			}
		}

		//replics = (R[])new JangoRemoteServer[numReplics];
		//replics = new Vector<R>(numReplics);
		replics = new Hashtable<Integer, R>(numReplics);
		registeredReplics = new Hashtable<String, R>(numReplics);

		replicMasterLock = new ReentrantReadWriteLock();

		//initialize();
	}

	protected void initialize(R me_objr, T me_obj)
	{
		lockReplicExclusive();

		this.me_obj = me_obj;
		Registry registry = null;
		
		try
		{
			registry = LocateRegistry.getRegistry(me_rmiHost, me_rmiPort);
			//registry.rebind(me_srvrName+"_jango_rep", (R)me_obj);
			registry.rebind(me_srvrName+"_jango_rep", me_objr);

			System.out.println("binding on _jango_rep");
		}
		catch(Exception e)
		{ e.printStackTrace(); }

		for(int numTries=0; numTries<numReplics; numTries++)
		{
			try
			{
				int numNbrs = buildReplics();
				System.out.println("Found " + numNbrs + " neighbours.");

				if(numNbrs == 0) //If we don't detect other replics, we start as maser, from fresh.
				{
					startAsMaster(true);
					replicStatus = JangoRemoteServer.JANGO_SERVER_MASTER;

					System.out.println("Starting as master as there are no others...");

					try 
					{ 
						registry.rebind(me_srvrName+"_jango",(JangoRemoteServer)me_obj);
						registry.rebind(me_srvrName, me_obj); 
					}
					catch(Exception e) { e.printStackTrace(); }

					unlockReplicExclusive();
					return;
				}

				R master = getAMaster();
				if(master != null) //If a master is available, load data from it.
				{
					loadDataFromMaster(master);
					registerWithAllReplics();

					replicStatus = (replicCfg == JANGO_REPLIC_CFG_ALL_MASTER ? JangoRemoteServer.JANGO_SERVER_MASTER : JangoRemoteServer.JANGO_SERVER_PASSIVE );

					System.out.println("Loaded data from master..");

					try 
					{ 
						registry.rebind(me_srvrName+"_jango",(JangoRemoteServer)me_obj);
						registry.rebind(me_srvrName, me_obj); 

						unlockReplicExclusive();
						return;
					}
					catch(Exception e) { e.printStackTrace(); }
				}
				else //We have no masters, but there are replics who are not in master mode, turn them to master.
					alertNoMastersAvailable();
			}
			catch(Exception e)
			{
				System.out.println("Error: " + e);
				e.printStackTrace();
			}
		}

		unlockReplicExclusive();
	}

	protected int buildReplics()
	{
		int numNbrs = 0;
		for(int i=0; i<numReplics; i++)
		{
			String rmiHost = rmiHosts[i];
			String srvrName = srvrNames[i];
			int rmiPort = rmiPorts[i];

			//Don't bind to ourselves.
		  if(rmiHost.equals(me_rmiHost) && srvrName.equals(me_srvrName) && rmiPort == me_rmiPort)
				continue;

			try
			{
				//TODO: recreate only if current one is null or status check fails.
				//Also register replic.
				R replicIntf = (R)LocateRegistry.getRegistry(rmiHost, rmiPort).lookup(srvrName+"_jango_rep");
				int srvrStat = replicIntf.getServerStatus();
				if(srvrStat == JangoRemoteServer.JANGO_SERVER_MASTER || srvrStat == JangoRemoteServer.JANGO_SERVER_PASSIVE)
				{
					registeredReplics.put(rmiHost+"|"+rmiPort+"|"+srvrName, replicIntf);
					replics.put(i, replicIntf);
					//replics[i] = replicIntf;
					numNbrs++;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error:" + rmiHost + ":"+rmiPort+":"+srvrName+":"+" couldn't be connected."); 
				e.printStackTrace(); 
			}

		}

		return numNbrs;
	}

	protected void registerWithAllReplics()
	{
		for (int i=0; i<numReplics; i++)
		{
			//if(replics[i] != null)
			if(replics.get(i) != null && i != masterRepIdx)
			{
				try { replics.get(i).registerReplic(me_rmiHost, me_rmiPort, me_srvrName); }
				catch(RemoteException e) { System.out.println("Err while registering with replic:"); e.printStackTrace(); }
			}
		}
	}


	protected R getAMaster()
	{
		//We will start probing from a random replic always.
		//int start_rep = (new Random()).nextInt(numReplics);

		System.out.println("Trying to locate a master...");

		for(int i=0; i<numReplics; i++)
		{
		  //R rep = replics[ (i+start_rep)%numReplics ];
		  R rep = replics.get(i);

			try
			{
				if (rep != null)
				{
				  int rstat = rep.getServerStatus();
					System.out.println("Replica " + i + " returned status " + rstat);
					if( rstat == JangoRemoteServer.JANGO_SERVER_MASTER )
					{
						masterRepIdx = i;
						return rep;
					}
				}
			}
			catch(Exception e)
			{ System.out.println("Error Trying to locate a replica"); e.printStackTrace(); }
		}
	  
		return null;
	}

	protected void alertNoMastersAvailable()
	{
		for(int i=0; i<numReplics; i++)
		{
		  R rep = replics.get(i);
			try
			{
				if(rep == null) continue;
				
				rep.switchToMaster();
			}
			catch(Exception e)
			{ }
		}
	}

	protected void lockReplicForNormalWork()
	{ replicMasterLock.readLock().lock();  }

	protected void unlockReplicForNormalWork()
	{ replicMasterLock.readLock().unlock();  }

	protected void lockReplicExclusive()
	{ replicMasterLock.writeLock().lock();  }

	protected void unlockReplicExclusive()
	{ replicMasterLock.writeLock().unlock();  }

	public int getServerStatus() throws RemoteException
	{ 
		//System.out.println("Returning my status as " + replicStatus);
		return replicStatus; 
	}

	protected abstract void loadDataFromMaster(R repMaster) throws RemoteException;

	protected abstract void startAsMaster(boolean clean);

	//Called externally. Relevant only when there's one master allowed.
	//Replic is already up and running.
	public boolean switchToMaster() throws RemoteException
	{
		System.out.println("Request received to switch to a master");

		//If I am already a master, no further processing required.
		if(getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER)
		{
			System.out.println("Already a master ...");
			return true;
		}

		lockReplicExclusive();
		//Check once again after the lock.
		//If I am already a master, no further processing required.
		if(getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER)
		{
			unlockReplicExclusive();
			System.out.println("Switched to master in between..");
			return true;
		}

		int numNbrs = buildReplics();
		System.out.println("Found " + numNbrs + " neighbours.");
		if(numNbrs == 0) //If I am the only one, I should become a master.
		{
			replicStatus = JangoRemoteServer.JANGO_SERVER_MASTER;
			masterRepIdx = -1;
			unlockReplicExclusive();
			System.out.println("Switching to master, none is around...");
			return true;
		}

		R master = getAMaster(); //see if some other master is around.
		if(master != null) //Someone else tookup the role of a master.
		{
			System.out.println("Someone else became a master");
			unlockReplicExclusive();
			return true;
		}

		//None of the other replicas are master, so I'll become a master.
		replicStatus = JangoRemoteServer.JANGO_SERVER_MASTER;
		unlockReplicExclusive();
		System.out.println("Switching to master, ...");

		return true;
	}

	public void registerReplic(String host, int port, String objName) throws RemoteException
	{

		try
		{
			R replicIntf = (R)LocateRegistry.getRegistry(host, port).lookup(objName+"_jango_rep");
			for(int i=0; i<numReplics; i++)
			{
				if(rmiHosts[i].equals(host) && rmiPorts[i] == port && srvrNames[i].equals(objName))
				{
					if(getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER)
						lockReplicExclusive();

					replics.put(i, replicIntf);
					registeredReplics.put(host+"|"+port+"|"+objName, replicIntf);
					//replics[i] = replicIntf;
					System.out.println("Added replic : " + host + ":" + port + ":" + objName);

					if(getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER)
						unlockReplicExclusive();

					return;
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }

	}

}
