package TxSystem;

import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


import java.util.BitSet;
import java.util.Vector;

public class LockManager
{
	public static final int READ = 0;
	public static final int WRITE = 1;

	private static int TABLE_SIZE = 2039;
	private static int DEADLOCK_TIMEOUT = 10000;

	private static TPHashTable lockTable = new TPHashTable(LockManager.TABLE_SIZE);
	private static TPHashTable stampTable = new TPHashTable(LockManager.TABLE_SIZE);
	private static TPHashTable waitTable = new TPHashTable(LockManager.TABLE_SIZE);

	private TxnListnerIntf txnManager;

	public LockManager(TxnListnerIntf txnManager) 
	{ 
	  super(); 
		this.txnManager = txnManager;
    try { DEADLOCK_TIMEOUT = Integer.parseInt(System.getenv("DEADLOCK_TIMEOUT")); }
		catch(Exception e) { }

	}

	public TPHashTable getLockTable()
	{ return lockTable; }

	public void setLockTable(TPHashTable lockTable)
	{ this.lockTable = lockTable; }

	public boolean Lock(int xid, String strData, int lockType) throws DeadlockException 
	{

		// if any parameter is invalid, then return false
		if (xid < 0) { return false; }

		if (strData == null) { return false; }

		if ((lockType != TrxnObj.READ) && (lockType != TrxnObj.WRITE)) { return false; }

		// two objects in lock table for easy lookup.
		TrxnObj trxnObj = new TrxnObj(xid, strData, lockType);
		DataObj dataObj = new DataObj(xid, strData, lockType);

		// return true when there is no lock conflict or throw a deadlock exception.
		try 
		{
			boolean bConflict = true;
			BitSet bConvert = new BitSet(1);
			while (bConflict) 
			{
				synchronized (this.lockTable) 
				{
					//-- synchronized (this.waitTable) 
					//--{
					// check if this lock request conflicts with existing locks
					bConflict = LockConflict(dataObj, bConvert);
					if (!bConflict) 
					{
						// no lock conflict
						synchronized (this.stampTable) 
						{
							// remove the timestamp (if any) for this lock request
							TimeObj timeObj = new TimeObj(xid);
							this.stampTable.remove(timeObj);
						}
						// remove the entry for this transaction from waitTable (if it
						// is there) as it has been granted its lock request
						WaitObj waitObj = new WaitObj(xid, strData, lockType);
						this.waitTable.remove(waitObj);

						if (bConvert.get(0) == true) 
						{
							// lock conversion 
							// *** ADD CODE HERE *** to carry out the lock conversion in the
							// lock table

							// Upgrade the lock types on existing objects.
							((TrxnObj)lockTable.get(new TrxnObj(xid, strData, TrxnObj.READ))).setLockType(TrxnObj.WRITE);
							((DataObj)lockTable.get(new DataObj(xid, strData, DataObj.READ))).setLockType(DataObj.WRITE);
						} 
						else 
						{
							// a lock request that is not lock conversion
							this.lockTable.add(trxnObj);
							this.lockTable.add(dataObj);
						}
					} //end of if (!bConflict)
					//--} // synchronizing on wait table
				} // synchronizing on locktable

				// lock conflict exists, wait
				// This should probably be outside sync block to pevent
				// other threads from blocking ?
				if (bConflict) { WaitLock(dataObj); }
			} // while (bConflict)
		} 
		catch (DeadlockException deadlock) 
		{
			//TODO : should we unlock every lock from this txn here ?
			throw deadlock;
		}
		// just ignore the redundant lock request
		catch (RedundantLockRequestException redundantlockrequest) 
		{ return true; } 

		return true;
	}


	// remove all locks for this transaction in the lock table.
	public boolean  UnlockAll(int xid) 
	{

		// if any parameter is invalid, then return false
		if (xid < 0) { return false; }

		TrxnObj trxnQueryObj = new TrxnObj(xid, "", -1);  // Only used in elements() call below.
		synchronized (this.lockTable) 
		{
			Vector vect = this.lockTable.elements(trxnQueryObj);

			TrxnObj trxnObj;
			Vector waitVector;
			WaitObj waitObj;
			int size = vect.size();

			for (int i = (size - 1); i >= 0; i--) 
			{

				trxnObj = (TrxnObj) vect.elementAt(i);
				this.lockTable.remove(trxnObj);

				DataObj dataObj = new DataObj(trxnObj.getXId(), trxnObj.getDataName(), trxnObj.getLockType());
				this.lockTable.remove(dataObj);

				// check if there are any waiting transactions. 
				synchronized (this.waitTable) 
				{
					// get all the transactions waiting on this dataObj
					waitVector = this.waitTable.elements(dataObj);
					int waitSize = waitVector.size();
					for (int j = 0; j < waitSize; j++) 
					{
						waitObj = (WaitObj) waitVector.elementAt(j);
						System.out.println("Unlock:Waittable Waiting for "+dataObj+" "+waitObj);
						if (waitObj.getLockType() == LockManager.WRITE) 
						{
							if (j == 0) 
							{
								// get all other transactions which have locks on the
								// data item just unlocked. 
								Vector vect1 = this.lockTable.elements(dataObj);

								// remove interrupted thread from waitTable only if no
								// other transaction has locked this data item
								if (vect1.size () == 0) 
								{
								  System.out.println("Unlock: no oher locks on this data"+dataObj);
									this.waitTable.remove(waitObj);     

									try 
									{
										synchronized (waitObj.getThread())
										{
											waitObj.getThread().notify();
										}    
									}
									catch (Exception e)    
									{
										System.out.println("Exception on unlock\n" + e.getMessage());
									}        
								}
								//
								else if (vect1.size () == 1)
								{
								  //TODO: check if the lock table entry is my own
									TrxnObj txnObj = (TrxnObj)vect1.elementAt(0);
									if(txnObj.getXId() == waitObj.getXId())
									{
										try 
										{
											synchronized (waitObj.getThread()) 
											{ waitObj.getThread().notify(); }    
										}
										catch (Exception e) 
										{
											System.out.println("Exception e\n" + e.getMessage());
										}
									}
								}
								else 
								{
								  System.out.println("Unlock: some other locks on this data"+dataObj);
									// some other transaction still has a lock on
									// the data item just unlocked. So, WRITE lock
									// cannot be granted.
									break;
								}
							}

							System.out.println("Write lock " +waitObj+ "waiting on data"+dataObj);
							// stop granting READ locks as soon as you find a WRITE lock
							// request in the queue of requests
							break;
						} 
						else if (waitObj.getLockType() == LockManager.READ)
						{
							// remove interrupted thread from waitTable.
							this.waitTable.remove(waitObj);    
							System.out.println("Read lock " +waitObj+ "waiting on data"+dataObj + " freed.");

							try 
							{
								synchronized (waitObj.getThread()) 
								{ waitObj.getThread().notify(); }    
							}
							catch (Exception e) 
							{
								System.out.println("Exception e\n" + e.getMessage());
							}
						}
					}
				} 
			}
		} 

		return true;
	}


	// returns true if the lock request on dataObj conflicts with already existing locks. If the lock request is a
	// redundant one (for eg: if a transaction holds a read lock on certain data item and again requests for a read
	// lock), then this is ignored. This is done by throwing RedundantLockRequestException which is handled 
	// appropriately by the caller. If the lock request is a conversion from READ lock to WRITE lock, then bitset 
	// is set. 

	private boolean LockConflict(DataObj dataObj, BitSet bitset) throws DeadlockException, RedundantLockRequestException 
	{
		Vector vect = this.lockTable.elements(dataObj);
		DataObj dataObj2;
		int size = vect.size();
		boolean needToUpgrade = false;

		// as soon as a lock that conflicts with the current lock request is found, return true
		for (int i = 0; i < size; i++) 
		{
			dataObj2 = (DataObj) vect.elementAt(i);
			if (dataObj.getXId() == dataObj2.getXId()) 
			{    
				// the transaction already has a lock on this data item which means that it is either
				// relocking it or is converting the lock
				if (dataObj.getLockType() == DataObj.READ) 
				{    
					// since transaction already has a lock (may be READ, may be WRITE. we don't
					// care) on this data item and it is requesting a READ lock, this lock request
					// is redundant.
					throw new RedundantLockRequestException(dataObj.getXId(), "Redundant READ lock request");
				} else if (dataObj.getLockType() == DataObj.WRITE) 
				{
					// transaction already has a lock and is requesting a WRITE lock
					// now there are two cases to analyze here
					// (1) transaction already had a READ lock
					// (2) transaction already had a WRITE lock
					// Seeing the comments at the top of this function might be helpful
					// *** ADD CODE HERE *** to take care of both these cases

					// Already has a write lock, so scold him.
					if(dataObj2.getLockType() == DataObj.WRITE)
						throw new RedundantLockRequestException(dataObj.getXId(), "Redundant WRITE lock request");

					//He needs a read->write upgrade, but someone else 
					//is waiting, he will have to wait
					//if(hasTxnWaitingFor(dataObj)) return true;
					//Screw it, we will just let this guy upgrade lock
					// to avoid dead lock.
					//Not a true serialization implementation.
					//Anyhow there's no true way to "queue" locks now.
					//Implementation can lead to writer starvation.

					//He needs a read->write upgrade
					//No other txn waiting and this is the only read lock
					if(vect.size() == 1) // no conflict, upgrade
					{ bitset.set(0); return false; }
					else //He needs a read->write upgrade,
						//But there are other read locks. conflict, wait.
						{ 
							System.out.println(dataObj+" Want WRITE, has READ and someone else also has READ");
							return true; 
						}

				}
			} 
			else 
			{
				if (dataObj.getLockType() == DataObj.READ) 
				{
					if (dataObj2.getLockType() == DataObj.WRITE) 
					{
						// transaction is requesting a READ lock and some other transaction
						// already has a WRITE lock on it ==> conflict
						System.out.println(dataObj+" Want READ, someone has WRITE");
						return true;
					}
					// do nothing 
					else { }
				} else if (dataObj.getLockType() == DataObj.WRITE) 
				{
					// transaction is requesting a WRITE lock and some other transaction has either
					// a READ or a WRITE lock on it ==> conflict
					System.out.println(dataObj+" Want WRITE, someone has READ or WRITE");
					return true;
				}
			}
		}

		// no conflicting lock found, return false
		return false;

	}

	private void WaitLock(DataObj dataObj) throws DeadlockException 
	{
		// Check timestamp or add a new one.
		// Will always add new timestamp for each new lock request since
		// the timeObj is deleted each time the transaction succeeds in
		// getting a lock (see Lock() )

		TimeObj timeObj = new TimeObj(dataObj.getXId());
		TimeObj timestamp = null;
		long timeBlocked = 0;
		Thread thisThread = Thread.currentThread();
		WaitObj waitObj = new WaitObj(dataObj.getXId(), dataObj.getDataName(), dataObj.getLockType(), thisThread);

		synchronized (this.stampTable) 
		{
			Vector vect = this.stampTable.elements(timeObj);
			if (vect.size() == 0) 
			{
				// add the time stamp for this lock request to stampTable
				this.stampTable.add(timeObj);
				timestamp = timeObj;
				System.out.println(dataObj + " stamptable is empty");
			} 
			else if (vect.size() == 1) 
			{
				// lock operation could have timed out; check for deadlock
				TimeObj prevStamp = (TimeObj)vect.firstElement();
				timestamp = prevStamp;
				timeBlocked = timeObj.getTime() - prevStamp.getTime();
				System.out.println(dataObj + " stamptable has an entry "+prevStamp);
				if (timeBlocked >= LockManager.DEADLOCK_TIMEOUT) 
				{
					// the transaction has been waiting for a period greater than the timeout period
					cleanupDeadlock(prevStamp, waitObj);
				}
			} 
			else 
			{
				// should never get here. shouldn't be more than one time stamp per transaction
				// because a transaction at a given time the transaction can be blocked on just one lock
				// request. 
				//TODO: Add a trace / alert here for debugging.
				System.out.println(dataObj + " stamptable has many entries "+vect.size());
			}
		} 

		// suspend thread and wait until notified...

		synchronized (this.waitTable) 
		{
			if (! this.waitTable.contains(waitObj)) 
			{
				// register this transaction in the waitTable if it is not already there 
				this.waitTable.add(waitObj);
			}
			else 
			{
				// else lock manager already knows the transaction is waiting.
			}
		}

		synchronized (thisThread) 
		{
			try 
			{
				thisThread.wait(LockManager.DEADLOCK_TIMEOUT - timeBlocked);
				TimeObj currTime = new TimeObj(dataObj.getXId());
				timeBlocked = currTime.getTime() - timestamp.getTime();
				System.out.println(dataObj + " Time blocked " + timeBlocked);
				if (timeBlocked >= LockManager.DEADLOCK_TIMEOUT) 
				{
					// the transaction has been waiting for a period greater than the timeout period
					cleanupDeadlock(timestamp, waitObj);
				}
				else { return; }
			}
			catch (InterruptedException e) 
			{
				System.out.println("Thread interrupted?");
			}
		}
	}


	private boolean hasTxnWaitingFor(DataObj dataObj)
	{
		// if we have any Txns waiting for this data object, return true.
		return this.waitTable.elements(dataObj).size() != 0;
	}

	// cleanupDeadlock cleans up stampTable and waitTable, and throws DeadlockException
	private void cleanupDeadlock(TimeObj tmObj, WaitObj waitObj) throws DeadlockException
	{
		synchronized (this.stampTable) 
		{
			synchronized (this.waitTable) 
			{
				this.stampTable.remove(tmObj);
				this.waitTable.remove(waitObj);
			}
		}
		// Unlock all the locks belonging to this Txn, this is a deadlock.
		System.out.println("Aborting " + waitObj);

		//Send out an abort transaction message.
		//try { txnManager.abortTxn(null, waitObj.getXId()); }
		//catch (Exception e) { e.printStackTrace(); }
		//Unlock all the locks by this txn;
		UnlockAll(waitObj.getXId());
		// Report a deadlock
		throw new DeadlockException(waitObj.getXId(), "Sleep timeout...deadlock.");
	}
}
