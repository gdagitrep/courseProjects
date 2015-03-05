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

public class TxnManager implements TxnManagerIntf
//public class TxnManager implements Remote, TxnListnerIntf
{
	Hashtable txNListners; // keep track 
	Hashtable txnsAndListnersMap;
	Hashtable abortedTxns; // keep track of txns aborted for being idle.
	Object txnCounterLockObj;
	int txnCounter;
	LockManager lm;
	IdleTxnAbortManager idlTxnMgr;

	private static int IDLE_TIMEOUT = 20000;

	public TxnManager()
	{
	  txnCounterLockObj = new Object();
		txNListners = new Hashtable();
		txnsAndListnersMap = new Hashtable(500);
		abortedTxns = new Hashtable(300);
		txnCounter = 0;
		lm = new LockManager(this);
		try { IDLE_TIMEOUT = Integer.parseInt(System.getenv("TRANSACTION_IDLE_TIMEOUT")); }
		catch(Exception e) { }
		System.out.println("TRANSACTION_IDLE_TIMEOUT = "+IDLE_TIMEOUT);
		idlTxnMgr = new IdleTxnAbortManager(this, txnsAndListnersMap, abortedTxns);
		idlTxnMgr.start();
	}

  public static void main(String args[])
	{
	  Trace.setup();
	  PerfMonitor.setup();
   try
   {
     // create a new Server object
     TxnManager obj = new TxnManager();
     // dynamically generate the stub (client proxy)
     TxnManagerIntf tm = (TxnManagerIntf) UnicastRemoteObject.exportObject(obj, 0);

     // Bind the remote object's stub in the registry
     Registry registry = LocateRegistry.getRegistry(System.getenv("TMGR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("TMGR_RMI_PORT")));
     registry.rebind(System.getenv("TMGR_RMI_OBJ_NAME"), tm);

     System.err.println("Server ready");
   }
   catch (Exception e)
   {
     System.err.println("Server exception: " + e.toString());
     e.printStackTrace();
   }

   // Create and install a security manager
   if (System.getSecurityManager() == null) 
   { System.setSecurityManager(new RMISecurityManager()); }
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

  public void registerTxnListner(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException
  {
		if(txnListner == null) return;

		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
				sendTxnException(tx);

		synchronized(txnData)
		{
		  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
				throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to listen on this transaction is invalid");

		  if(!txnData.txnListners.contains(txnListner))
			  txnData.txnListners.add(txnListner);
		}
	}

	public void abortTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

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

		synchronized(txnData)
		{
		  if(!(txnData.txnStatus == TxnData.TXN_ACTIVE || txnData.txnStatus == TxnData.TXN_ABORTED))
				throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to abort this transaction is invalid");

		  txnData.setTxnStaus(TxnData.TXN_ABORTED);
      for (int i=0; i<txnData.txnListners.size(); i++)
			{
			  TxnListnerIntf txnLn = (TxnListnerIntf)txnData.txnListners.get(i);
				//TODO: see if we can do this in parallel.
				if(txnListner != txnLn)
				  txnLn.abortTxn(null, txId);
			}
			unlockAll(txId); //After an abort, unlock all stuff.
		  txnsAndListnersMap.remove(tx);
		}
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
	}

	public void commitTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			sendTxnException(tx);

		synchronized(txnData)
		{
		  if(txnData.txnStatus != TxnData.TXN_ACTIVE)
				throw new InvalidTransactionException(txId, txnData.txnStatus, " Request to commit this transaction is invalid");

		  txnData.setTxnStaus(TxnData.TXN_COMMITED);
      for (int i=0; i<txnData.txnListners.size(); i++)
			{
			  TxnListnerIntf txnLn = (TxnListnerIntf)txnData.txnListners.get(i);
				//TODO: see if we can do this in parallel.
				txnLn.commitTxn(null, txId);
			}
			unlockAll(txId); //After a commit, unlock all stuff.
		  txnsAndListnersMap.remove(tx);
		  PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
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
	  synchronized(txnCounterLockObj)
		{ 
			TxnData txnData = new TxnData(++txnCounter);

		  PerfMonitor.recordPerf(txnCounter, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.START);

			txnsAndListnersMap.put(new Integer(txnCounter), txnData);
		  Trace.info("TM:startTxn() returns "+txnCounter+";");
		  return txnCounter; 
		}
	}

	public boolean lockObject(int txId, String strData, int lockType) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Integer tx = new Integer(txId);
	  TxnData txnData = (TxnData)txnsAndListnersMap.get(tx);

		if(txnData == null)
			sendTxnException(tx);

		synchronized(txnData)
		{
			if (txnData.txnStatus != TxnData.TXN_ACTIVE)
				throw new InvalidTransactionException(txId, txnData.txnStatus, " lock request on a txn that is not active");

			keepTxnAlive(txId);

		  try
			{ 
		    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.START);

			  boolean gotLock = lm.Lock(txId, strData, lockType);
			  keepTxnAlive(txId); // Just in case we spend a lot of time in blocking.
		    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.END);

				return gotLock;
			}
			catch(DeadlockException dle) 
			{ 
				dle.printStackTrace();
				//--This is getting called from lock manager in case of a dead lock.
				abortTxn(null, txId); // we hit a dead lock, abort the txn
		    PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_LOCK, null, strData,PerfMonitor.ERR);
				throw dle;
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

  private class TxnData
	{
		static final int TXN_ACTIVE = 1;
		static final int TXN_ABORTED = 2;
		static final int TXN_INVALID = 3;
		static final int TXN_COMMITED = 4;

	  int txnId;
		int txnStatus;
		long txnLastOpTime;
		Vector txnListners;

		TxnData(int txnId)
		{ 
		  this.txnId = txnId; 
			this.txnStatus = TXN_ACTIVE;
			txnLastOpTime = System.currentTimeMillis();
			txnListners = new Vector();
		}

		void setTxnStaus(int txnStatus)
		{ this.txnStatus = txnStatus; }

		void addTxnListner(TxnListnerIntf txnListner)
		{ txnListners.add(txnListner); }

		void resetTxnLastOpTime()
		{ txnLastOpTime = System.currentTimeMillis(); }
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
		    try { sleep(10000); }
			  catch(InterruptedException e) { e.printStackTrace(); }

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
					  try { abortTxn(txnManager, txnData.txnId); }
					  catch(Exception e) { e.printStackTrace(); }
				  }
				}
			}
		}

		public void shutdown() 
		{ 
		  shutdown = true; interrupt(); 
			try{ idlTxnMgr.join();  }
			catch(Exception e) { e.printStackTrace(); }
		}
	}
}
