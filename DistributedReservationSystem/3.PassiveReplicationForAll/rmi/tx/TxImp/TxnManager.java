package TxImp;

import java.util.*;

import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import TxSystem.*;
import TxIntf.*;

import G13Log.*;
import G13Components.*;

import jango.*;

  class TxnData implements java.io.Serializable
	{
		static final int TXN_ACTIVE = 1;
		static final int TXN_ABORTED = 2;
		static final int TXN_INVALID = 3;
		static final int TXN_COMMITED = 4;

	  int txnId;
		int txnStatus;
		long txnLastOpTime;
		Vector<String> txnListners;

		TxnData(int txnId)
		{ 
		  this.txnId = txnId; 
			this.txnStatus = TXN_ACTIVE;
			txnLastOpTime = System.currentTimeMillis();
			txnListners = new Vector<String>();
		}

		void setTxnStaus(int txnStatus)
		{ this.txnStatus = txnStatus; }

		void addTxnListner(String RMType)
		{ txnListners.add(RMType); }

		void resetTxnLastOpTime()
		{ txnLastOpTime = System.currentTimeMillis(); }
	}


public class TxnManager extends JangoRep<TxnManagerRep, TxnManagerIntf> implements TxnManagerRep, TxnManagerIntf, JangoRemoteServer
//public class TxnManager implements Remote, TxnListnerIntf
{
	//Hashtable txNListners; // keep track 
	Hashtable txnsAndListnersMap;
	Hashtable abortedTxns; // keep track of txns aborted for being idle.
	Hashtable userAbortedTxns;
	Hashtable commitedTxns;
	Hashtable<String, Vector<TxnListnerIntf>> RMs;
	Object txnCounterLockObj;
	int txnCounter;
	LockManager lm;
	IdleTxnAbortManager idlTxnMgr;
	CrashPoint crashPoint;

	private static int IDLE_TIMEOUT = 20000;

	public TxnManager(int numReplics, String replicConfigs, String me_rmiHost, String me_srvrName, int me_rmiPort)
	{
		super(numReplics, replicConfigs, me_rmiHost, me_srvrName, me_rmiPort, JANGO_REPLIC_CFG_SINGLE_MASTER);
	  txnCounterLockObj = new Object();
		//txNListners = new Hashtable();
		txnsAndListnersMap = new Hashtable(500);
		abortedTxns = new Hashtable(300);
		userAbortedTxns = new Hashtable(300);
		commitedTxns = new Hashtable(300);
		RMs = new Hashtable(10);
		txnCounter = 0;
		lm = new LockManager(this);
		try { IDLE_TIMEOUT = Integer.parseInt(System.getenv("TRANSACTION_IDLE_TIMEOUT")); }
		catch(Exception e) { }
		System.out.println("TRANSACTION_IDLE_TIMEOUT = "+IDLE_TIMEOUT);
		idlTxnMgr = new IdleTxnAbortManager(this, txnsAndListnersMap, abortedTxns);
		idlTxnMgr.start();

		crashPoint = new CrashPoint();
		loadCrashPoints();

		try
		{
    	 UnicastRemoteObject.exportObject(this, 0);
    	//TxnManagerIntf tm = (TxnManagerIntf) UnicastRemoteObject.exportObject(this, 0);
    	//TxnManagerRep tmr = (TxnManagerRep) UnicastRemoteObject.exportObject(this, 0);
			//initialize(tm, tmr);
			initialize(this, this);
		}
		catch(RemoteException e) { e.printStackTrace(); }
	}

  public static void main(String args[])
	{
	  Trace.setup();
	  PerfMonitor.setup();

   // Create and install a security manager
   if (System.getSecurityManager() == null) 
   { System.setSecurityManager(new DSRMISecurityManager()); }

   try
   {
     // create a new Server object
     TxnManager obj = new TxnManager( Integer.parseInt(System.getenv("TMGR_REPLIC_CNT")), System.getenv("TMGR_REPLIC_CFG"), System.getenv("TMGR_RMI_REGISTRY_HOST"), System.getenv("TMGR_RMI_OBJ_NAME"), Integer.parseInt(System.getenv("TMGR_RMI_PORT")) );
     // dynamically generate the stub (client proxy)
     //--TxnManagerIntf tm = (TxnManagerIntf) UnicastRemoteObject.exportObject(obj, 0);

     // Bind the remote object's stub in the registry
		 /*
     Registry registry = LocateRegistry.getRegistry(System.getenv("TMGR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("TMGR_RMI_PORT")));
     registry.rebind(System.getenv("TMGR_RMI_OBJ_NAME"), tm);
     registry.rebind(System.getenv("TMGR_RMI_OBJ_NAME")+"_jango", (JangoRemoteServer)obj);
		 */

     System.err.println("Server ready");
   }
   catch (Exception e)
   {
     System.err.println("Server exception: " + e.toString());
     e.printStackTrace();
   }
	}

	protected void loadCrashPoints()
	{
		crashPoint.addCrashPoint("crash_lock_before");
		crashPoint.addCrashPoint("crash_lock_before_rep");
		crashPoint.addCrashPoint("crash_lock_after_rep");
		crashPoint.addCrashPoint("crash_lock_after");

		crashPoint.addCrashPoint("crash_abort_before");
		crashPoint.addCrashPoint("crash_abort_after_rm1");
		crashPoint.addCrashPoint("crash_abort_after_rmType1");
		crashPoint.addCrashPoint("crash_abort_after_rms");
		crashPoint.addCrashPoint("crash_abort_before_mcast");
		crashPoint.addCrashPoint("crash_abort_after");
		crashPoint.addCrashPoint("crash_abort_after_mcast1");

		crashPoint.addCrashPoint("crash_commit_before");
		crashPoint.addCrashPoint("crash_commit_after_rm1");
		crashPoint.addCrashPoint("crash_commit_after_rmType1");
		crashPoint.addCrashPoint("crash_commit_after_rms");
		crashPoint.addCrashPoint("crash_commit_before_mcast");
		crashPoint.addCrashPoint("crash_commit_after_mcast");
		crashPoint.addCrashPoint("crash_commit_after_mcast1");

		crashPoint.addCrashPoint("crash_start_before");
		crashPoint.addCrashPoint("crash_start_after");

	}

  protected void sendTxnException(Integer tx) throws InvalidTransactionException, TransactionAbortedException
	{
	  TxnData txnData = (TxnData)abortedTxns.get(tx);

		if(txnData == null)
				throw new InvalidTransactionException(tx, TxnData.TXN_INVALID, " is invalid");

		//Now that we are reporting it, we don't have to keep track of it.
		abortedTxns.remove(tx);
		throw new TransactionAbortedException(tx, " Transaction was aborted for being idle");

	}

	public String setCrashPoint(String point, boolean status) throws RemoteException
	{
		return crashPoint.setCrashPoint(point, status);
	}

	public Hashtable<String, Boolean> getCrashPoints() throws RemoteException
	{ return crashPoint.getCrashPoints(); }

  public void registerTxnListner(int txId, String RMType) throws RemoteException, InvalidTransactionException, TransactionAbortedException
  {
		Trace.info("TM:registerTxnListner(" + txId + "," + RMType + ")");
		try
		{
			lockReplicForNormalWork();
			if(RMType == null) return;
	
			Integer tx = new Integer(txId);
		  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);
	
			if(txnData == null)
					sendTxnException(tx);
	
			synchronized(txnData)
			{
			  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
					throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to listen on this transaction is invalid");
	
			  if(!txnData.txnListners.contains(RMType))
				{ txnData.txnListners.add(RMType); }

				mcast_registerTxnListner_Rep(txId, RMType);
			}
		}
		finally { unlockReplicForNormalWork(); }
	}

	public void registerTxnListner_Rep(int txId, String RMType) throws RemoteException
	{
		Trace.info("TM:registerTxnListner_Rep(" + txId + "," + RMType + ")");
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		  if(!txnData.txnListners.contains(RMType))
			  txnData.txnListners.add(RMType);
	}

	protected void mcast_registerTxnListner_Rep(int txId, String RMType)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.registerTxnListner_Rep(txId, RMType);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}
		}
	}

	public void registerRM(TxnListnerIntf txnListner, String RMType) throws RemoteException
	{
		Trace.info("TM:registerRM() For " + RMType);
		if(txnListner == null || RMType == null) return;

		try
		{
			lockReplicExclusive();
			Vector<TxnListnerIntf> rmList = RMs.get(RMType);
			if(rmList == null)
			{
				Trace.info("TM:registerRM:" + RMType + ": is new RMType");
				rmList = new Vector<TxnListnerIntf>(10);
				RMs.put(RMType, rmList);
			}

			if(!rmList.contains(txnListner))
			{ 
				Trace.info("TM:registerRM(" + RMType + ") has a new RM");
				rmList.add(txnListner); 
			}

			mcast_registerRM_Rep(txnListner, RMType);
		}
		finally
		{ unlockReplicExclusive(); }
	}

	public void registerRM_Rep(TxnListnerIntf txnListner, String RMType) throws RemoteException
	{
		Trace.info("TM:registerRM_Rep() For " + RMType);
		Vector<TxnListnerIntf> rmList = RMs.get(RMType);

		if(rmList == null)
		{
			Trace.info("TM:registerRM:" + RMType + ": is new RMType");
			rmList = new Vector<TxnListnerIntf>(10);
			RMs.put(RMType, rmList);
		}

		if(!rmList.contains(txnListner))
		{ 
			Trace.info("TM:registerRM(" + RMType + ") has a new RM");
			rmList.add(txnListner); 
		}

	}

	protected void mcast_registerRM_Rep(TxnListnerIntf txnListner, String RMType)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.registerRM_Rep(txnListner, RMType);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}
		}
	}

	public void abortTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{
		Trace.info("TM:abortTxn(" + txId  + ")");
		try
		{
			lockReplicForNormalWork();
			Integer tx = new Integer(txId);
		  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

			if(txnData == null)
			{
				//This was already aborted, do nothing.
				TxnData txnData2 = (TxnData)userAbortedTxns.get(tx);
				if(txnData2 != null) return;
			}
	
			if(txnData == null)
				sendTxnException(tx);
			/*
			if(txnData == null)
			{
				if(abortedTxns.get(tx) != null)
				  sendTxnException(tx);
				else
				  return;
			}
			*/
	
			if(txnListner != null) // client is not aware of the abort
			{ abortedTxns.put(tx, txnData); }
			else
			{ userAbortedTxns.put(tx, txnData); }
	
			synchronized(txnData)
			{
			  if(!(txnData.txnStatus == TxnData.TXN_ACTIVE || txnData.txnStatus == TxnData.TXN_ABORTED))
					throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to abort this transaction is invalid");
	
			  txnData.setTxnStaus(TxnData.TXN_ABORTED);

				crashPoint.crashCheck("crash_abort_before");

 				// client is not aware of the abort, so make sure replics abort
				// before sending abort to RMs.
				if(txnListner != null)
				{ 
					crashPoint.crashCheck("crash_abort_before_mcast");
					mcast_abortTxn_Rep(txId, txnListner); 
				}

				//Loop through each RMType registered for this Txn.
	      for (int i=0; i<txnData.txnListners.size(); i++)
				{
					Vector<TxnListnerIntf> txnListners = RMs.get(txnData.txnListners.get(i));
					//Loop through each registered replic for the RM
					for (int j=0; j<txnListners.size(); j++)
					{
				  	TxnListnerIntf txnLn = txnListners.get(j);
						//TODO: see if we can do this in parallel.
						try
						{
							if(txnListner != txnLn)
					  		txnLn.abortTxn(txId, null);
						}
						catch(RemoteException e) 
						{ 
							e.printStackTrace(); 
							//TODO: may be we can remove failed replics from here
						}
						crashPoint.crashCheck("crash_abort_after_rm1");
					}
					crashPoint.crashCheck("crash_abort_after_rmType1");
				}
				crashPoint.crashCheck("crash_abort_after_rms");
				unlockAll(txId); //After an abort, unlock all stuff.
			  txnsAndListnersMap.remove(tx);
			}
		}
		finally
		{
			//For client initiated aborts, we can do replic mcast in the end
			//As client will resend the abort if the master TM fails.
			if(txnListner == null)
			{
				crashPoint.crashCheck("crash_abort_before_mcast");
				mcast_abortTxn_Rep(txId, txnListner);
			}

			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_abort_after");
			PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
		}
	}

	public void abortTxn_Rep(int txId, TxnListnerIntf txnListner) throws RemoteException
	{
		Trace.info("TM:abortTxn_Rep(" + txId +  ")");
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			return;

		if(txnListner != null) // client is not aware of the abort
		{ abortedTxns.put(tx, txnData); }
		else
		{ userAbortedTxns.put(tx, txnData); }

		synchronized(txnData)
		{
		  if(!(txnData.txnStatus == TxnData.TXN_ACTIVE || txnData.txnStatus == TxnData.TXN_ABORTED))
				return;

		  txnData.setTxnStaus(TxnData.TXN_ABORTED);
			try { unlockAll(txId); }  //After an abort, unlock all stuff. 
			catch(Exception e) { e.printStackTrace(); }
		  txnsAndListnersMap.remove(tx);
		}
	}

	protected void mcast_abortTxn_Rep(int txId, TxnListnerIntf txnListner)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.abortTxn_Rep(txId, txnListner);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}
			crashPoint.crashCheck("crash_abort_after_mcast1");
		}
	}

	public void commitTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("TM:commitTxn(" + txId +  ")");
		try
		{
			lockReplicForNormalWork();
			Integer tx = new Integer(txId);
		  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);
	
			if(txnData == null)
			{
				//This was already commited.
				TxnData txnData2 = (TxnData)commitedTxns.get(tx);
				if(txnData2 != null) return;
			}

			crashPoint.crashCheck("crash_commit_before");
	
			if(txnData == null)
				sendTxnException(tx);
	
			synchronized(txnData)
			{
			  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
					throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to commit this transaction is invalid");
	
			  txnData.setTxnStaus(TxnData.TXN_COMMITED);
	      for (int i=0; i<txnData.txnListners.size(); i++)
				{
					Vector<TxnListnerIntf> txnListners = RMs.get(txnData.txnListners.get(i));
					Trace.info("TM:commitTxn:" + txId + ":" + txnData.txnListners.get(i) + ":RM replic count :" + txnListners.size());
					for (int j=0; j<txnListners.size(); j++)
					{
				  	TxnListnerIntf txnLn = txnListners.get(j);
						//TODO: see if we can do this in parallel.

						try{	txnLn.commitTxn(txId, null); }
						catch(RemoteException e) 
						{ 
							//TODO: may be we can remove failed replics here.
							e.printStackTrace(); 
						}

						crashPoint.crashCheck("crash_commit_after_rm1");
					}
					crashPoint.crashCheck("crash_commit_after_rmType1");
				}
				commitedTxns.put(tx,txnData);
				unlockAll(txId); //After a commit, unlock all stuff.
			  txnsAndListnersMap.remove(tx);
				crashPoint.crashCheck("crash_commit_after_rms");
			}
		}
		finally
		{
			crashPoint.crashCheck("crash_commit_before_mcast");
			mcast_commitTxn_Rep(txId, txnListner);
			unlockReplicForNormalWork();
			PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
			crashPoint.crashCheck("crash_commit_after_mcast");
		}
	}

	public void commitTxn_Rep(int txId, TxnListnerIntf txnListner) throws RemoteException
	{
		Trace.info("TM:commitTxn_Rep(" + txId +  ")");
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			return;

		synchronized(txnData)
		{
		  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
				return;

		  txnData.setTxnStaus(TxnData.TXN_COMMITED);
			commitedTxns.put(txId, txnData);
			try{	unlockAll(txId); }//After a commit, unlock all stuff.
			catch(Exception e) { e.printStackTrace(); }
		  txnsAndListnersMap.remove(tx);
		}
	}

	protected void mcast_commitTxn_Rep(int txId, TxnListnerIntf txnListner)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.commitTxn_Rep(txId, txnListner);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}

			crashPoint.crashCheck("crash_commit_after_mcast1");
		}
	}

	public void keepTxnAlive(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			sendTxnException(tx);

		synchronized(txnData)
		{ 
		  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
				throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to keep alive on this transaction is invalid");

		  txnData.resetTxnLastOpTime(); 
		}
	}

	public int getTxnStaus(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{ 
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			sendTxnException(tx);

		synchronized(txnData)
		{ return txnData.txnStatus; }
	}

	public int startTxn() throws RemoteException
	{
		try
		{
			lockReplicForNormalWork();
		  synchronized(txnCounterLockObj)
			{ 
				TxnData txnData = new TxnData(++txnCounter);
	
			  PerfMonitor.recordPerf(txnCounter, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.START);
	
				txnsAndListnersMap.put(new Integer(txnCounter), txnData);
			  Trace.info("TM:startTxn() returns "+txnCounter+";");

				crashPoint.crashCheck("crash_start_before");
				mcast_startTxn_Rep(txnCounter);
				crashPoint.crashCheck("crash_start_after");

			  return txnCounter; 
			}
		}
		finally
		{
			unlockReplicForNormalWork();
		}
	}

	public void startTxn_Rep(int lastTxn) throws RemoteException
	{
		Trace.info("TM:startTxn_Rep(" + lastTxn +  ")");
		synchronized(txnCounterLockObj)
		{ 
			TxnData txnData = new TxnData(lastTxn);
			txnsAndListnersMap.put(new Integer(lastTxn), txnData);

			if(lastTxn > txnCounter) txnCounter = lastTxn ; 
		}
	}

	protected void mcast_startTxn_Rep(int lastTxn)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.startTxn_Rep(lastTxn);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}
		}
	}

	public boolean lockObject(int txId, String strData, int lockType) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("TM:lockObject(" + txId + "," + strData + "," + lockType + ")");
		crashPoint.crashCheck("crash_lock_before");
		try
		{
			Integer tx = new Integer(txId);
		  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);
	
			if(txnData == null)
				sendTxnException(tx);
	
			synchronized(txnData)
			{
				if (txnData.txnStatus != TxnData.TXN_ACTIVE)
					throw new InvalidTransactionException(txId, txnData.txnStatus, " lock request on a txn that is not active");
	
				lockReplicForNormalWork();
				keepTxnAlive(txId);
	
			  try
				{ 
			    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.START);
	
				  boolean gotLock = lm.Lock(txId, strData, lockType);
				  keepTxnAlive(txId); // Just in case we spend a lot of time in blocking.
					crashPoint.crashCheck("crash_lock_before_rep");
					mcast_lockObject_Rep(txId, strData, lockType);
					crashPoint.crashCheck("crash_lock_after_rep");

			    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.END);
	
					return gotLock;
				}
				catch(DeadlockException dle) 
				{ 
					dle.printStackTrace();
					//--This is getting called from lock manager in case of a dead lock.
					abortTxn(txId, null); // we hit a dead lock, abort the txn
			    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.ERR);
					throw dle;
				}
			}
		}
		finally
		{ 
			unlockReplicForNormalWork(); 
			crashPoint.crashCheck("crash_lock_after");
		}
	}

	public void lockObject_Rep(int txId, String strData, int lockType) throws RemoteException
	{
		Trace.info("TM:lockObject_Rep(" + txId + "," + strData + "," + lockType + ")");
		
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			return;

		synchronized(txnData)
		{
			if (txnData.txnStatus != TxnData.TXN_ACTIVE)
				return;

		  try
			{ 
			  lm.Lock(txId, strData, lockType);
			  try { keepTxnAlive(txId); }
				//Shouldn't really be happening.
				catch(Exception e) { e.printStackTrace(); }
			}
			catch(DeadlockException dle) 
			{ 
				//This shouldn't happen, replics are not supposed to have deadlocks.
				dle.printStackTrace();
			}
		}
	}

	protected void mcast_lockObject_Rep(int txId, String strData, int lockType)
	{
		for(int i=0; i<numReplics; i++)
		{
			if ( replics.get(i) == null ) continue;

			try 
			{ 
				TxnManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
				if(rep != null) rep.lockObject_Rep(txId, strData, lockType);
			}
			catch(RemoteException re) //Remove the replics that fail.
			{ 
				System.out.println("Err " + re);
				re.printStackTrace();
				registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]); 
			}
		}
	}

	public boolean unlockAll(int txId) throws InvalidTransactionException, TransactionAbortedException, RemoteException
	{
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			sendTxnException(tx);

		synchronized(txnData)
		{
			if ( !(txnData.txnStatus == TxnData.TXN_COMMITED || txnData.txnStatus == TxnData.TXN_ABORTED) )
				throw new InvalidTransactionException(txId, txnData.txnStatus, " unlock lock request on a txn that is not commited or aborted.");

		  PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_UNLOCK, null, null,PerfMonitor.START);
		  boolean status = lm.UnlockAll(txId);
		  PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_UNLOCK, null, null,PerfMonitor.END);
		  return status;
		}
	}


	public void shutdown() throws RemoteException
	{
	  //TODO: Terminate all threads in here..

		idlTxnMgr.shutdown();
	}

	private class IdleTxnAbortManager extends Thread
	{
		boolean shutdown;
		Hashtable txnsAndListnersMap;
		Hashtable abortedTxns;
		TxnManagerIntf txnManager;

		private IdleTxnAbortManager(TxnManagerIntf txnManager, Hashtable txnsAndListnersMap, Hashtable abortedTxns) 
		{ 
			this.txnsAndListnersMap = txnsAndListnersMap;
			this.abortedTxns = abortedTxns;
			this.txnManager = txnManager;
		  shutdown=false; 
		}

	  public void run()
		{
			while(!shutdown)
			{
				try
				{
			    try { sleep(10000); }
				  catch(InterruptedException e) { e.printStackTrace(); }
	
					try
					{
						// Only master needs to do aborting of idle txns.
						if(getServerStatus() != JangoRemoteServer.JANGO_SERVER_MASTER)
							continue;
					} //We are calling locally, so this will never happen.
					catch(Exception e) { e.printStackTrace(); }
	
	  			Iterator txnList = txnsAndListnersMap.values().iterator();
				  while(txnList.hasNext())
				  {
				    TxnData txnData = (TxnData)txnList.next();
					  synchronized(txnData)
					  {
						  //This could have changed
					    if(txnData.txnStatus != TxnData.TXN_ACTIVE)
							  continue;
	
						  //
						  if(System.currentTimeMillis() - txnData.txnLastOpTime < TxnManager.IDLE_TIMEOUT)
						    continue;
	
						  //Ask to abort this transaction.
						  try { abortTxn(txnData.txnId, txnManager); }
						  catch(Exception e) { e.printStackTrace(); }
					  }
					}
				}
				catch(Exception e) { }
			}
		}

		public void shutdown() 
		{ 
		  shutdown = true; interrupt(); 
			try{ idlTxnMgr.join();  }
			catch(Exception e) { e.printStackTrace(); }
		}
	}

	public void startAsMaster(boolean clean)
	{}

	public void loadDataFromMaster(TxnManagerRep repMaster) throws RemoteException
	{
		Trace.info("TM:loadDataFromMaster()");
		Hashtable data = repMaster.transferTxnData(me_rmiHost, me_rmiPort, me_srvrName);

		lm.setLockTable((TPHashTable)data.get("lm"));
		//txNListners = (Hashtable)data.get("txnListners");
		txnCounter = ((Integer)data.get("txnCounter")).intValue();
		abortedTxns.putAll((Hashtable)data.get("abortedTxns"));
		userAbortedTxns.putAll((Hashtable)data.get("userAbortedTxns"));
		commitedTxns.putAll((Hashtable)data.get("commitedTxns"));
		txnsAndListnersMap.putAll((Hashtable)data.get("txnsAndListnersMap"));
		RMs.putAll((Hashtable<String, Vector<TxnListnerIntf>>)data.get("RMs"));
	}

	public Hashtable transferTxnData(String host, int port, String repName) throws RemoteException
	{
		Trace.info("TM:transferTxnData(" + host + "," + port + "," + repName + ")");
		Hashtable data = new Hashtable(10);

		lockReplicExclusive();

		data.put("lm",lm.getLockTable());
		//data.put("txnListners", txNListners);
		data.put("txnCounter", new Integer(txnCounter));
		data.put("abortedTxns", abortedTxns);
		data.put("userAbortedTxns", userAbortedTxns);
		data.put("commitedTxns", commitedTxns);
		data.put("txnsAndListnersMap", txnsAndListnersMap);
		data.put("RMs", RMs);

		registerReplic(host, port, repName);

		unlockReplicExclusive();

		return data;
	}

}
