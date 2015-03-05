// -------------------------------
// adapated from Kevin T. Manley
// CSE 593
//
package ResImpl;

import ResInterface.*;

import G13Log.*;
import G13Components.*;
import TxSystem.*;
import TxIntf.*;

import jango.*;

import java.util.*;
import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ResourceManagerImpl extends JangoRep<ResourceManagerRep, ResourceManager> implements ResourceManagerRep, ResourceManager, JangoRemoteServer
{
	
	//Actual "Database"
	protected RMHashtable m_itemHT;
	//Keep our txn "undo" data here.
	//this is a hashtable that maps txn specific hashtables.
	protected Hashtable txnBuffers;
	protected Hashtable<Integer,ResponseObject> txnLastResp;

	protected TxnManagerIntf txnMgr;

	protected String rmServerName;

	protected String RMType;

	protected CrashPoint crashPoint;

	//TODO... should default to passive first, till we have the replica built.
  //protected static int srvrStatus = JANGO_SERVER_MASTER;

	public ResourceManagerImpl(int numReplics, String replicConfigs, String me_rmiHost,String me_srvrName, int me_rmiPort, String RMType)
	{
		super(numReplics, replicConfigs, me_rmiHost, me_srvrName, me_rmiPort, JANGO_REPLIC_CFG_SINGLE_MASTER);

		this.RMType = RMType;

		m_itemHT = new RMHashtable();
		txnBuffers = new Hashtable(200);
		txnLastResp = new Hashtable<Integer, ResponseObject>(200);

		crashPoint = new CrashPoint();
		loadCrashPoints();

	 	try 
	  {
			// get a reference to the Transaction Manager.
			txnMgr = new TxnManagerIntf_RepStub(Integer.parseInt(System.getenv("TMGR_REPLIC_CNT")), System.getenv("TMGR_REPLIC_CFG"));
		}
		catch(Exception e) { e.printStackTrace(); }

		try
		{
			UnicastRemoteObject.exportObject(this, 0);
			txnMgr.registerRM(this, RMType);
			initialize(this, this);
		}
		catch(RemoteException e) { e.printStackTrace(); }

	}


	public static void main(String args[]) 
	{
		PerfMonitor.setup();
		Trace.setup();
	 
     // Create and install a security manager
         if (System.getSecurityManager() == null) {
	     System.setSecurityManager(new DSRMISecurityManager());
         }
	 
	 	try 
	  {
			ResourceManagerImpl obj = new ResourceManagerImpl(Integer.parseInt(System.getenv("RM_REPLIC_CNT")), System.getenv("RM_REPLIC_CFG"), System.getenv("SRVR_RMI_REGISTRY_HOST"), System.getenv("SRVR_RMI_OBJ_NAME"), Integer.parseInt(System.getenv("SRVR_RMI_PORT")), System.getenv("RM_TYPE") );
		 
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
		crashPoint.addCrashPoint("crash_deleteitem_before");
		crashPoint.addCrashPoint("crash_deleteitem_after_mcast");
		crashPoint.addCrashPoint("crash_deleteitem_after_mcast_rep1");

		crashPoint.addCrashPoint("crash_reserveitem_before");
		crashPoint.addCrashPoint("crash_reserveitem_after_mcast");
		crashPoint.addCrashPoint("crash_reserveitem_after_mcast_rep1");

		if(RMType.equals("FLT"))
		{
			crashPoint.addCrashPoint("crash_addflight_before");
			crashPoint.addCrashPoint("crash_addflight_after_mcast");
			crashPoint.addCrashPoint("crash_addflight_after_mcast_rep1");
		}

		if(RMType.equals("HTL"))
		{
			crashPoint.addCrashPoint("crash_addrooms_before");
			crashPoint.addCrashPoint("crash_addrooms_after_mcast");
			crashPoint.addCrashPoint("crash_addrooms_after_mcast_rep1");
		}

		if(RMType.equals("CAR"))
		{
			crashPoint.addCrashPoint("crash_addcars_before");
			crashPoint.addCrashPoint("crash_addcars_after_mcast");
			crashPoint.addCrashPoint("crash_addcars_after_mcast_rep1");
		}

		crashPoint.addCrashPoint("crash_newcustomer_before");
		crashPoint.addCrashPoint("crash_newcustomer_after_mcast");
		crashPoint.addCrashPoint("crash_newcustomer_after_mcast_rep1");

		crashPoint.addCrashPoint("crash_deletecustomer_before");
		crashPoint.addCrashPoint("crash_deletecustomer_after_mcast");
		crashPoint.addCrashPoint("crash_deletecustomer_after_mcast_rep1");

		crashPoint.addCrashPoint("crash_abort");
		crashPoint.addCrashPoint("crash_commit");
	}

	public String setCrashPoint(String point, boolean status) throws RemoteException
	{
		return crashPoint.setCrashPoint(point, status);
	}

  public Hashtable<String, Boolean> getCrashPoints() throws RemoteException
  { return crashPoint.getCrashPoints(); }

	protected ResponseObject getPreviousResponse(int txId, int rqId)
	{
		ResponseObject resp = txnLastResp.get(txId);

		if(resp == null) return null;

		if(resp.getReqId() != rqId) return null;

		return resp;
	}

	private RMItem readData( int id, int rqId, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{ return readData(id, rqId, key, LockManager.READ); }

	// Reads a data item
	private RMItem readData( int id, int rqId, String key, int lockType) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		//Request Lock Only if I am the master, saves redundant lock reqs to the LM.
		if(getServerStatus() == JangoRemoteServer.JANGO_SERVER_MASTER)
		{
			PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_LOCK, null, key,PerfMonitor.START);
			//locking is the first stuff...
			//we don't care if the data exists or not etc ...
			txnMgr.lockObject(id, key, lockType);
			PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_LOCK, null, key,PerfMonitor.END);
		}

		//Fetch data from this txn's buffer
		RMHashtable txnBuffer = (RMHashtable)txnBuffers.get(id);

		//looks like we don't have any record of this txn..
		//First time ?
		if(txnBuffer == null)
		{ 
			PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.START);
			//Lets register with TxnManager first.
			txnMgr.registerTxnListner(id, RMType);
		  //Each txn get it's own copy of "db"
		  txnBuffer = new RMHashtable();
			txnBuffers.put(id, txnBuffer);
			Trace.info("RM::readData: Transaction " + id + " is new to this RM , allocating Txn Buffers");
		}
		//Check in the TxnBuffer first.
		RMItem rmItem = (RMItem) txnBuffer.get(key);

		//We never read this data ?
		if(rmItem == null)
		{
			 Trace.info("RM::readData: Transaction " + id + " data " + key + " Not in txn buffer");
			//read the main data store.
		  rmItem = (RMItem) m_itemHT.get(key);

			//If this is a read for write, put it into our local
			// txn specific buffer.
			if(rmItem != null &&  lockType == LockManager.WRITE)
			{
				//Create a copy of the object as we might setting some flags etc.
				// clone is not overriden in any of the RM classes, hopefully
				// the default implmenation of Object is good enough.
			  try 
				{ 
				  txnBuffer.put(key, rmItem=rmItem.copy()); 
			    Trace.info("RM::readData: Transaction " + id + " data " + key + "  copied to txn buffer");
				}
				catch (CloneNotSupportedException e) { e.printStackTrace(); }
			}
			else if(rmItem == null)
			{
			  Trace.info("RM::readData: Transaction " + id + " data " + key + " not found");
			}
			else
			{
			  Trace.info("RM::readData: Transaction " + id + " data " + key + " will be returned from main data store");
			}
		}

		//if our txn had "deleted" this data, return null
		if(rmItem != null && rmItem.isDeleted()) return null;

		return rmItem;
	}

	// Writes a data item
	private void writeData( int id, int rqId, String key, RMItem value ) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		//txnMgr.lockObject(this, id, key, lockType);

		//This will take care of locks, getting into our local txn buffer etc.
		readData(id, rqId, key, LockManager.WRITE);

		//Get this txn's buffer.
		RMHashtable txnBuffer = (RMHashtable)txnBuffers.get(id);
		value.markAsModified();

		//Write it to the local buffer.
		txnBuffer.put(key, value);

	}
	
	// Remove the item out of storage
	protected RMItem removeData(int id, int rqId, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		//Get it to our local txn buffer, if it is not already there.
	  RMItem rmItem = readData(id, rqId, key, LockManager.WRITE);

		//We do only "soft" deletes on txn buffers.
		if(rmItem != null)
			rmItem.mark4Deletion();

		return rmItem;

	}

	
	// deletes the entire item
	protected boolean deleteItem(int id, int rqId, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::deleteItem(" + id + ", " + rqId + ", " + key + ") called" );
		crashPoint.crashCheck("crash_deleteitem_before");
			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			ReservableItem curObj = (ReservableItem) readData( id, rqId, key, LockManager.WRITE );
			// Check if there is such an item in the storage
			if( curObj == null ) {
				Trace.warn("RM::deleteItem(" + id + ", " + rqId + ", " + key + ") failed--item doesn't exist" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
				return false;
			} else {
				if(curObj.getReserved()==0){
					removeData(id, rqId, curObj.getKey());
					Trace.info("RM::deleteItem(" + id + ", " + rqId + ", " + key + ") item deleted" );
					txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
					return true;
				}
				else{
					Trace.info("RM::deleteItem(" + id + ", " + rqId + ", " + key + ") item can't be deleted because some customers reserved it" );
					txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
					return false;
				}
			} // if
		}
		finally
		{
			mcast_deleteItem_Rep(id, rqId, key, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_deleteitem_after_mcast");
		}
	}

	public void deleteItem_Rep(int id, int rqId, String key, ResponseObject masterResp) throws RemoteException
	{
		Trace.info("RM::deleteItem_Rep(" + id + ", " + rqId + ", " + key + ") called" );
		ResponseObject resp = getPreviousResponse(id, rqId);

		//We have seen this request already, so no actual processing to be done.
		if(resp != null) return;

		//Master did not commit to any changes, so I can skip rest of the 
		// processing. Just store the response.
		if(! ((Boolean)masterResp.getResponse()).booleanValue() )
		{
			txnLastResp.put(id, masterResp);
			return;
		}

		try
		{
			ReservableItem curObj = (ReservableItem) readData( id, rqId, key, LockManager.WRITE );
			removeData(id, rqId, curObj.getKey());
			Trace.info("RM::deleteItem_Rep(" + id + ", " + rqId + ", " + key + ") item deleted" );
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
		}
		catch(Exception e) { e.printStackTrace(); }
		return ;

	}
	
  protected void mcast_deleteItem_Rep(int id, int rqId, String key, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.deleteItem_Rep(id, rqId, key, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}

			crashPoint.crashCheck("crash_deleteitem_after_mcast_rep1");
    }
  }

	

	// query the number of available seats/rooms/cars
	protected int queryNum(int id, int rqId, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryNum(" + id + ", " + rqId + ", " + key + ") called" );
		ReservableItem curObj = (ReservableItem) readData( id, rqId, key);
		int value = 0;  
		if( curObj != null ) {
			value = curObj.getCount();
		} // else
		Trace.info("RM::queryNum(" + id + ", " + rqId + ", " + key + ") returns count=" + value);
		return value;
	}	
	
	// query the price of an item
	protected int queryPrice(int id, int rqId, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryCarsPrice(" + id + ", " + rqId + ", " + key + ") called" );
		ReservableItem curObj = (ReservableItem) readData( id, rqId, key);
		int value = 0; 
		if( curObj != null ) {
			value = curObj.getPrice();
		} // else
		Trace.info("RM::queryCarsPrice(" + id + ", " + rqId + ", " + key + ") returns cost=$" + value );
		return value;		
	}
	
	// reserve an item
	protected boolean reserveItem(int id, int rqId, int customerID, String key, String location, boolean checkOnly) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{

		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::reserveItem( " + id + ", customer=" + customerID + ", " +key+ ", "+location+" ) called" );		
			crashPoint.crashCheck("crash_reserveitem_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			// Read customer object if it exists (and write lock it)
			Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID) , LockManager.WRITE);		
			if( cust == null ) {
				Trace.warn("RM::reserveItem( " + id + ", " + rqId + ", " + customerID + ", " + key + ", "+location+")  failed--customer doesn't exist" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
				return false;
			} 
			
			// check if the item is available and write lock it
			ReservableItem item = (ReservableItem)readData(id, rqId, key, LockManager.WRITE);
			if(item==null){
				Trace.warn("RM::reserveItem( " + id + ", " + rqId + ", " + customerID + ", " + key+", " +location+") failed--item doesn't exist" );
				resp = new ResponseObject(id, rqId, false);
				return false;
			}else if(item.getCount()==0){
				Trace.warn("RM::reserveItem( " + id + ", " + rqId + ", " + customerID + ", " + key+", " + location+") failed--No more items" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
				return false;
			}else{			
	
				//Don't make actual changes we were told to keep things on hold.
			  if(checkOnly)
				{
					txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
					return true;
				}
	
				cust.reserve( key, location, item.getPrice());		
				writeData( id, rqId, cust.getKey(), cust );
				
				// decrease the number of available items in the storage
				item.setCount(item.getCount() - 1);
				item.setReserved(item.getReserved()+1);
				
				Trace.info("RM::reserveItem( " + id + ", " + rqId + ", " + customerID + ", " + key + ", " +location+") succeeded" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return true;
			}		
		}
		finally
		{
			mcast_reserveItem_Rep(id, rqId, customerID, key, location, checkOnly, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_reserveitem_after_mcast");
		}
	}

	public void reserveItem_Rep(int id, int rqId, int customerID, String key , String location, boolean checkOnly, ResponseObject masterResp) throws RemoteException
	{
			Trace.info("RM::reserveItem_Rep( " + id + ", customer=" + customerID + ", " +key+ ", "+location+" ) called" );		
		ResponseObject resp = getPreviousResponse(id, rqId);
		//We have seen this request already, so no actual processing to be done.
		if(resp != null) return;

		//Master did not commit to any changes, so I can skip rest of the 
		// processing. Just store the response.
		if(! ((Boolean)masterResp.getResponse()).booleanValue() )
		{
			txnLastResp.put(id, masterResp);
			return;
		}

		try
		{
			// Read customer object if it exists (and write lock it)
			Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID) , LockManager.WRITE);		
			
			// check if the item is available and write lock it
			ReservableItem item = (ReservableItem)readData(id, rqId, key, LockManager.WRITE);
	
				//Don't make actual changes we were told to keep things on hold.
			  if(checkOnly)
				{
					txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
					return ;
				}
	
				cust.reserve( key, location, item.getPrice());		
				writeData( id, rqId, cust.getKey(), cust );
				
				// decrease the number of available items in the storage
				item.setCount(item.getCount() - 1);
				item.setReserved(item.getReserved()+1);
				
				Trace.info("RM::reserveItem_Rep( " + id + ", " + rqId + ", " + customerID + ", " + key + ", " +location+") succeeded" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return ;
		}
		catch(Exception e) { e.printStackTrace(); }
	}

  protected void mcast_reserveItem_Rep(int id, int rqId, int customerID, String key, String location, boolean checkOnly, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.reserveItem_Rep(id, rqId, customerID, key, location, checkOnly, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_reserveitem_after_mcast_rep1");
    }
  }

	
	// Create a new flight, or add seats to existing flight
	//  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int id, int rqId, int flightNum, int flightSeats, int flightPrice)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::addFlight(" + id + ", " + rqId + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );
			crashPoint.crashCheck("crash_addflight_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			Flight curObj = (Flight) readData( id, rqId, Flight.getKey(flightNum), LockManager.WRITE );
			if( curObj == null ) {
				// doesn't exist...add it
				Flight newObj = new Flight( flightNum, flightSeats, flightPrice );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addFlight(" + id + ") created new flight " + flightNum + ", seats=" +
						flightSeats + ", price=$" + flightPrice );
			} else {
				// add seats to existing flight and update the price...
				curObj.setCount( curObj.getCount() + flightSeats );
				if( flightPrice > 0 ) {
					curObj.setPrice( flightPrice );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addFlight(" + id + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return(true);
		}
		finally
		{
			mcast_addFlight_Rep(id, rqId, flightNum, flightSeats, flightPrice, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_addflight_after_mcast");
		}
	}

	public void addFlight_Rep(int id, int rqId, int flightNum, int flightSeats,int flightPrice, ResponseObject masterResp) throws RemoteException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);

		//We have already processed this.
		if(resp != null) return;

		try
		{
			Trace.info("RM::addFlight_Rep(" + id + ", " + rqId + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );

			Flight curObj = (Flight) readData( id, rqId, Flight.getKey(flightNum), LockManager.WRITE );
			if( curObj == null ) {
				// doesn't exist...add it
				Flight newObj = new Flight( flightNum, flightSeats, flightPrice );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addFlight_Rep(" + id + ") created new flight " + flightNum + ", seats=" +
						flightSeats + ", price=$" + flightPrice );
			} else {
				// add seats to existing flight and update the price...
				curObj.setCount( curObj.getCount() + flightSeats );
				if( flightPrice > 0 ) {
					curObj.setPrice( flightPrice );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addFlight_Rep(" + id + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return;
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	protected void mcast_addFlight_Rep(int id, int rqId, int flightNum, int flightSeats,int flightPrice, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.addFlight_Rep(id, rqId, flightNum, flightSeats, flightPrice, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_addflight_after_mcast_rep1");
    }
  }
	

	public boolean deleteFlight(int id, int rqId, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteFlight(" + id + ", " + rqId + ", " + flightNum + ") called" );
		return deleteItem(id, rqId, Flight.getKey(flightNum));
	}



	// Create a new room location or add rooms to an existing location
	//  NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int id, int rqId, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::addRooms(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );
			crashPoint.crashCheck("crash_addrooms_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			Hotel curObj = (Hotel) readData( id, rqId, Hotel.getKey(location), LockManager.WRITE );
			if( curObj == null ) {
				// doesn't exist...add it
				Hotel newObj = new Hotel( location, count, price );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addRooms(" + id + ") created new room location " + location + ", count=" + count + ", price=$" + price );
			} else {
				// add count to existing object and update price...
				curObj.setCount( curObj.getCount() + count );
				if( price > 0 ) {
					curObj.setPrice( price );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addRooms(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return(true);
		}
		finally
		{
			mcast_addRooms_Rep(id, rqId, location, count, price, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_addrooms_after_mcast");
		}
	}

	public void addRooms_Rep(int id, int rqId, String location, int count,int     price, ResponseObject masterResp) throws RemoteException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);

		//We have already processed this.
		if(resp != null) return;

		try
		{
			Trace.info("RM::addRooms_Rep(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );

			Hotel curObj = (Hotel) readData( id, rqId, Hotel.getKey(location), LockManager.WRITE );
			if( curObj == null ) {
				// doesn't exist...add it
				Hotel newObj = new Hotel( location, count, price );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addRooms_Rep(" + id + ") created new room location " + location + ", count=" + count + ", price=$" + price );
			} else {
				// add count to existing object and update price...
				curObj.setCount( curObj.getCount() + count );
				if( price > 0 ) {
					curObj.setPrice( price );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addRooms_Rep(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return;
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	protected void mcast_addRooms_Rep(int id, int rqId, String location, int count,int price, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.addRooms_Rep(id, rqId, location, count, price, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_addrooms_after_mcast_rep1");
    }
  }
	

	// Delete rooms from a location
	public boolean deleteRooms(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteRooms(" + id + ", " + rqId + ", " + location + ") called" );
		return deleteItem(id, rqId, Hotel.getKey(location));
		
	}

	// Create a new car location or add cars to an existing location
	//  NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int id, int rqId, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::addCars(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );
			crashPoint.crashCheck("crash_addcars_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			Car curObj = (Car) readData( id, rqId, Car.getKey(location), LockManager.WRITE );
			if( curObj == null ) {
				// car location doesn't exist...add it
				Car newObj = new Car( location, count, price );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addCars(" + id + ") created new location " + location + ", count=" + count + ", price=$" + price );
			} else {
				// add count to existing car location and update price...
				curObj.setCount( curObj.getCount() + count );
				if( price > 0 ) {
					curObj.setPrice( price );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addCars(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return(true);
		}
		finally
		{
			mcast_addCars_Rep(id, rqId, location, count, price, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_addcars_after_mcast");
		}
	}

	public void addCars_Rep(int id, int rqId, String location, int count,int price, ResponseObject masterResp) throws RemoteException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);

		//We have already processed this.
		if(resp != null) return;

		try
		{
			Trace.info("RM::addCars_Rep(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );

			Car curObj = (Car) readData( id, rqId, Car.getKey(location), LockManager.WRITE );
			if( curObj == null ) {
				// car location doesn't exist...add it
				Car newObj = new Car( location, count, price );
				writeData( id, rqId, newObj.getKey(), newObj );
				Trace.info("RM::addCars_Rep(" + id + ") created new location " + location + ", count=" + count + ", price=$" + price );
			} else {
				// add count to existing car location and update price...
				curObj.setCount( curObj.getCount() + count );
				if( price > 0 ) {
					curObj.setPrice( price );
				} // if
				writeData( id, rqId, curObj.getKey(), curObj );
				Trace.info("RM::addCars_Rep(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
			} // else
			txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
			return;
		}
		catch(Exception e) { e.printStackTrace(); }

	}

	protected void mcast_addCars_Rep(int id, int rqId, String location, int count,int price, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.addCars_Rep(id, rqId, location, count, price, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_addcars_after_mcast_rep1");
    }
  }
	

	// Delete cars from a location
	public boolean deleteCars(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteCars(" + id + ", " + rqId + ", " + location + ") called" );
		return deleteItem(id, rqId, Car.getKey(location));
	}



	// Returns the number of empty seats on this flight
	public int queryFlight(int id, int rqId, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryFlight(" + id + ", " + rqId + ", " + flightNum + ") called" );
		return queryNum(id, rqId, Flight.getKey(flightNum));
	}

	// Returns the number of reservations for this flight. 
//	public int queryFlightReservations(int id, int rqId, int flightNum)
//		throws RemoteException
//	{
//		Trace.info("RM::queryFlightReservations(" + id + ", #" + flightNum + ") called" );
//		RMInteger numReservations = (RMInteger) readData( id, Flight.getNumReservationsKey(flightNum) );
//		if( numReservations == null ) {
//			numReservations = new RMInteger(0);
//		} // if
//		Trace.info("RM::queryFlightReservations(" + id + ", #" + flightNum + ") returns " + numReservations );
//		return numReservations.getValue();
//	}


	// Returns price of this flight
	public int queryFlightPrice(int id, int rqId, int flightNum )
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryFlightPrice(" + id + ", " + rqId + ", " + flightNum + ") called" );
		return queryPrice(id, rqId, Flight.getKey(flightNum));
	}


	// Returns the number of rooms available at a location
	public int queryRooms(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryRooms(" + id + ", " + rqId + ", " + location + ") called" );
		return queryNum(id, rqId, Hotel.getKey(location));
	}

	// Returns room price at this location
	public int queryRoomsPrice(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryRoomsPrice(" + id + ", " + rqId + ", " + location + ") called" );
		return queryPrice(id, rqId, Hotel.getKey(location));
	}


	// Returns the number of cars available at a location
	public int queryCars(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryCars(" + id + ", " + rqId + ", " + location + ") called" );
		return queryNum(id, rqId, Car.getKey(location));
	}


	// Returns price of cars at this location
	public int queryCarsPrice(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryPrice(" + id + ", " + rqId + ", " + location + ") called" );
		return queryPrice(id, rqId, Car.getKey(location));
	}

	// Returns data structure containing customer reservation info. Returns null if the
	//  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
	//  reservations.
	public RMHashtable getCustomerReservations(int id, int rqId, int customerID)
		throws RemoteException, InvalidTransactionException,DeadlockException,TransactionAbortedException
	{
		Trace.info("RM::getCustomerReservations(" + id + ", " + rqId + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID) );
		if( cust == null ) {
			Trace.warn("RM::getCustomerReservations failed(" + id + ", " + rqId + ", " + customerID + ") failed--customer doesn't exist" );
			return null;
		} else {
			return cust.getReservations();
		} // if
	}

	// return a bill
	public String queryCustomerInfo(int id, int rqId, int customerID)
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryCustomerInfo(" + id + ", " + rqId + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID) );
		if( cust == null ) {
			Trace.warn("RM::queryCustomerInfo(" + id + ", " + rqId + ", " + customerID + ") failed--customer doesn't exist" );
			return "";   // NOTE: don't change this--WC counts on this value indicating a customer does not exist...
		} else {
				String s = cust.printBill();
				Trace.info("RM::queryCustomerInfo(" + id + ", " + rqId + ", " + customerID + "), bill follows..." );
				System.out.println( s );
				return s;
		} // if
	}

  // customer functions
  // new customer just returns a unique customer identifier
	
	//THIS Interface is not used anywhere.
	//Middleware generates the customer id and calls the
	//Next newCustomer(int, int, int) function.
  public int newCustomer(int id, int rqId)
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		Trace.info("INFO: RM::newCustomer(" + id + ") called" );
		// Generate a globally unique ID for the new customer
		Customer cust = new Customer( id );
		writeData( id, rqId, cust.getKey(), cust );
		Trace.info("RM::newCustomer(" + id + ") returns ID=" + id );
		return id;
	}

	// I opted to pass in customerID instead. This makes testing easier
  public boolean newCustomer(int id, int rqId, int customerID )
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("INFO: RM::newCustomer(" + id + ", " + rqId + ", " + customerID + ") called" );
			crashPoint.crashCheck("crash_newcustomer_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID), LockManager.WRITE );
			if( cust == null ) {
				cust = new Customer(customerID);
				writeData( id, rqId, cust.getKey(), cust );
				Trace.info("INFO: RM::newCustomer(" + id + ", " + rqId + ", " + customerID + ") created a new customer" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return true;
			} else {
				Trace.info("INFO: RM::newCustomer(" + id + ", " + rqId + ", " + customerID + ") failed--customer already exists");
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
				return false;
			} // else
		}
		finally
		{
			mcast_newCustomer_Rep(id, rqId, customerID, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_newcustomer_after_mcast");
		}
	}

	public void newCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp) throws RemoteException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		Trace.info("INFO: RM::newCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") called" );

		//We have already processed this.
		if(resp != null) return;

		//Master did not commit to any changes, so I can skip rest of the 
		// processing. Just store the response.
		if(! ((Boolean)masterResp.getResponse()).booleanValue() )
		{
			txnLastResp.put(id, masterResp);
			return;
		}

		try
		{
				Customer cust = new Customer(customerID);
				writeData( id, rqId, cust.getKey(), cust );
				Trace.info("INFO: RM::newCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") created a new customer" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return;
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	protected void mcast_newCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp)
	{
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.newCustomer_Rep(id, rqId, customerID, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_newcustomer_after_mcast_rep1");
    }
	}

	// Deletes customer from the database. 
	public boolean deleteCustomer(int id, int rqId, int customerID)
			throws RemoteException, InvalidTransactionException, DeadlockException,TransactionAbortedException
	{
		ResponseObject resp = getPreviousResponse(id, rqId);
		try
		{
			Trace.info("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") called" );
			crashPoint.crashCheck("crash_deleteitem_before");

			lockReplicForNormalWork();

			//We have seen this request already, so no actual processing to be
			//done.
			if(resp != null) return ((Boolean)resp.getResponse()).booleanValue();

			Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID), LockManager.WRITE );
			if( cust == null ) {
				Trace.warn("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") failed--customer doesn't exist" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, false));
				return false;
			} else {			
				// Increase the reserved numbers of all reservable items which the customer reserved. 
				RMHashtable reservationHT = cust.getReservations();
				for(Enumeration e = reservationHT.keys(); e.hasMoreElements();){		
					String reservedkey = (String) (e.nextElement());
					ReservedItem reserveditem = cust.getReservedItem(reservedkey);
					Trace.info("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times"  );
					ReservableItem item  = (ReservableItem) readData(id, rqId, reserveditem.getKey(), LockManager.WRITE);
					Trace.info("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") has reserved " + reserveditem.getKey() + "which is reserved" +  item.getReserved() +  " times and is still available " + item.getCount() + " times"  );
					item.setReserved(item.getReserved()-reserveditem.getCount());
					item.setCount(item.getCount()+reserveditem.getCount());
				}
				
				// remove the customer from the storage
				removeData(id, rqId, cust.getKey());
				
				Trace.info("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") succeeded" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return true;
			} // if
		}
		finally
		{
			mcast_deleteCustomer_Rep(id, rqId, customerID, resp);
			unlockReplicForNormalWork();
			crashPoint.crashCheck("crash_deleteitem_after_mcast");
		}
	}

	public void deleteCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp) throws RemoteException
	{
		Trace.info("RM::deleteCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") called" );
		ResponseObject resp = getPreviousResponse(id, rqId);
		//We have seen this request already, so no actual processing to be done.
		if(resp != null) return;

		//Master did not commit to any changes, so I can skip rest of the 
		// processing. Just store the response.
		if(! ((Boolean)masterResp.getResponse()).booleanValue() )
		{
			txnLastResp.put(id, masterResp);
			return;
		}

		try
		{
			Customer cust = (Customer) readData( id, rqId, Customer.getKey(customerID), LockManager.WRITE );
			// Increase the reserved numbers of all reservable items which the customer reserved. 
			RMHashtable reservationHT = cust.getReservations();
			for(Enumeration e = reservationHT.keys(); e.hasMoreElements();){		
					String reservedkey = (String) (e.nextElement());
					ReservedItem reserveditem = cust.getReservedItem(reservedkey);
					Trace.info("RM::deleteCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times"  );
					ReservableItem item  = (ReservableItem) readData(id, rqId, reserveditem.getKey(), LockManager.WRITE);
					Trace.info("RM::deleteCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") has reserved " + reserveditem.getKey() + "which is reserved" +  item.getReserved() +  " times and is still available " + item.getCount() + " times"  );
					item.setReserved(item.getReserved()-reserveditem.getCount());
					item.setCount(item.getCount()+reserveditem.getCount());
				}
				
				// remove the customer from the storage
				removeData(id, rqId, cust.getKey());
				
				Trace.info("RM::deleteCustomer_Rep(" + id + ", " + rqId + ", " + customerID + ") succeeded" );
				txnLastResp.put(id, resp = new ResponseObject(id, rqId, true));
				return ;
		}
		catch(Exception e) { e.printStackTrace(); }
	}

  protected void mcast_deleteCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp)
  {
    for(int i=0; i<numReplics; i++)
    {   
      if ( replics.get(i) == null ) continue;

      try 
      {   
        ResourceManagerRep rep = registeredReplics.get(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
        if(rep != null) rep.deleteCustomer_Rep(id, rqId, customerID, masterResp);
      }   
      catch(RemoteException re) //Remove the replics that fail.
      {   
        System.out.println("Err " + re);
        re.printStackTrace();
        registeredReplics.remove(rmiHosts[i]+"|"+rmiPorts[i]+"|"+srvrNames[i]);
			}
			crashPoint.crashCheck("crash_deleteitem_after_mcast_rep1");
    }
  }


	// Frees flight reservation record. Flight reservation records help us make sure we
	//  don't delete a flight if one or more customers are holding reservations
//	public boolean freeFlightReservation(int id, int rqId, int flightNum)
//		throws RemoteException
//	{
//		Trace.info("RM::freeFlightReservations(" + id + ", " + rqId + ", " + flightNum + ") called" );
//		RMInteger numReservations = (RMInteger) readData( id, Flight.getNumReservationsKey(flightNum) );
//		if( numReservations != null ) {
//			numReservations = new RMInteger( Math.max( 0, numReservations.getValue()-1) );
//		} // if
//		writeData(id, Flight.getNumReservationsKey(flightNum), numReservations );
//		Trace.info("RM::freeFlightReservations(" + id + ", " + rqId + ", " + flightNum + ") succeeded, this flight now has "
//				+ numReservations + " reservations" );
//		return true;
//	}
//	

	
	// Adds car reservation to this customer. 
	public boolean reserveCar(int id, int rqId, int customerID, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveCar(id, rqId, customerID, location, false);
	}
	public boolean reserveCar(int id, int rqId, int customerID, String location, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveCar(" + id + ", " + rqId + ", " + location + ", " + checkOnly +") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.START);
		boolean status = reserveItem(id, rqId, customerID, Car.getKey(location), location, checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.END);
		return status;
	}


	// Adds room reservation to this customer. 
	public boolean reserveRoom(int id, int rqId, int customerID, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveRoom(id, rqId, customerID, location, false);
	}
	public boolean reserveRoom(int id, int rqId, int customerID, String location, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveRoom(" + id + ", " + rqId + ", " + customerID + ", "  + location + ", " + checkOnly + ") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.START);
		boolean status = reserveItem(id, rqId, customerID, Hotel.getKey(location), location, checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.END);
		return status;
	}

	// Adds flight reservation to this customer.  
	public boolean reserveFlight(int id, int rqId, int customerID, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveFlight(id, rqId, customerID, flightNum, false);
	}
	public boolean reserveFlight(int id, int rqId, int customerID, int flightNum, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveFlight(" + id + ", " + rqId + ", " + customerID + ", "  + flightNum + ", " + checkOnly + ") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.START);
		boolean status = reserveItem(id, rqId, customerID, Flight.getKey(flightNum), String.valueOf(flightNum), checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.END);
		return status;
	}
	
	/* reserve an itinerary */
	// This is taken care of at MW server.
    public boolean itinerary(int id, int rqId,int customer,Vector flightNumbers,String location,boolean Car,boolean Room)
	throws RemoteException {
	    Trace.info("RM::itinerary(" + id + ", " + rqId + ", " + customer + ", "  + location + ", " + Car + ", " + Room + ") called : UNSUPPORTED" );
    	return false;
    }


  public void abortTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{
		//Get rid of this txn's buffer
		Trace.info("RM::abortTxn(" + txId + ") called" );
		crashPoint.crashCheck("crash_abort");
		try
		{
			lockReplicForNormalWork();
			txnBuffers.remove(txId);
			txnLastResp.remove(txId);
		}
		finally
		{
			unlockReplicForNormalWork();
			PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
		}
	}

	public void commitTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("RM::commitTxn(" + txId + ") called" );
		crashPoint.crashCheck("crash_commit");
		try
		{
			lockReplicForNormalWork();
		  //Get this txn's buffer.
			RMHashtable txnBuffer = (RMHashtable)txnBuffers.get(txId);
	
			//We had nothing to write for this txn, so we are good.
			if(txnBuffer == null) return;
	
			//Get the keys to all objects in this txn (possibly modified)
			Enumeration txnObjects = txnBuffer.keys();
	
			while(txnObjects != null && txnObjects.hasMoreElements())
			{
			  String key = (String)txnObjects.nextElement();
				RMItem rmItem = (RMItem)txnBuffer.get(key);
	
				if(! rmItem.isModified()) continue;
	
				if(rmItem.isDeleted()) // this is a deleted item
				{
				  m_itemHT.remove(key);
					continue;
				}
	
				//We are going to flush this guy into permanent DB store.
				rmItem.resetFlags();
	
				//Store it to the main DB Store.
				m_itemHT.put(key, rmItem);
			}
	
			//Remove this txn, now that we have done the commit.
			txnBuffers.remove(txId);
			txnLastResp.remove(txId);
		}
		finally
		{
			unlockReplicForNormalWork();
			PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
		}

	}

	public void shutdown() throws RemoteException
	{
		Trace.info("RM::shutdown() called" );
	}

	//We don't need it in the RM ?
	public int startTxn() throws  RemoteException
	{ return 0; }

	public void startAsMaster(boolean clean) { }

	public void loadDataFromMaster(ResourceManagerRep repMaster) throws RemoteException
	{ 
		Hashtable data = repMaster.transferDB(me_rmiHost, me_rmiPort, me_srvrName);
		m_itemHT = (RMHashtable)data.get("m_itemHT");
		txnBuffers.putAll((Hashtable)data.get("txnBuffers"));
		txnLastResp.putAll((Hashtable<Integer, ResponseObject>)data.get("txnLastResp"));
	}

	public Hashtable transferDB(String host, int port, String repName) throws RemoteException
	{
		Hashtable data = new Hashtable(5);

		lockReplicExclusive();

		data.put("m_itemHT", m_itemHT);
		data.put("txnBuffers", txnBuffers);
		data.put("txnLastResp", txnLastResp);

		registerReplic(host, port, repName);

		unlockReplicExclusive();

		return data;
	}

}
