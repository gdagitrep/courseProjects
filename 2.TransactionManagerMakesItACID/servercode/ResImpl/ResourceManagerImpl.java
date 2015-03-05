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

import java.util.*;
import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//public class ResourceManagerImpl extends java.rmi.server.UnicastRemoteObject
public class ResourceManagerImpl
	implements ResourceManager {
	
	//Actual "Database"
	protected RMHashtable m_itemHT = new RMHashtable();
	//Keep our txn "undo" data here.
	//this is a hashtable that maps txn specific hashtables.
	protected Hashtable txnBuffers = new Hashtable();

	protected static TxnManagerIntf txnMgr;

	protected static String rmServerName;


	public static void main(String args[]) {
				PerfMonitor.setup();
				Trace.setup();
        // Figure out where server is running
        String server = "localhost";

         if (args.length == 1) {
             server = server + ":" + args[0];
         } else if (args.length != 0 &&  args.length != 1) {
             System.err.println ("Wrong usage");
             System.out.println("Usage: java ResImpl.ResourceManagerImpl [port]");
             System.exit(1);
         }
	 
	 try 
	     {
			   // get a reference to the Transaction Manager.
				Registry tm_registry = LocateRegistry.getRegistry(System.getenv("TMGR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("TMGR_RMI_PORT")));
				txnMgr = (TxnManagerIntf)tm_registry.lookup(System.getenv("TMGR_RMI_OBJ_NAME"));

		 // create a new Server object
		 ResourceManagerImpl obj = new ResourceManagerImpl();
		 // dynamically generate the stub (client proxy)
		 ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(obj, 0);
		 
		 // Bind the remote object's stub in the registry
		 Registry registry = LocateRegistry.getRegistry(System.getenv("SRVR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("SRVR_RMI_PORT")));
		 registry.rebind(rmServerName=System.getenv("SRVR_RMI_OBJ_NAME"), rm);
		 
		 System.err.println("Server ready");
	     } 
	 catch (Exception e) 
	     {
		 System.err.println("Server exception: " + e.toString());
		 e.printStackTrace();
	     }
	 
         // Create and install a security manager
         if (System.getSecurityManager() == null) {
	     System.setSecurityManager(new RMISecurityManager());
         }
	 
	 }
	 
	 public ResourceManagerImpl() throws RemoteException {
	 }
	 
	private RMItem readData( int id, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{ return readData(id, key, LockManager.READ); }

	// Reads a data item
	private RMItem readData( int id, String key, int lockType) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  //TODO catch exceptions from TxnManager and "abort." ?

		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_LOCK, null, key,PerfMonitor.START);
		//locking is the first stuff...
		//we don't care if the data exists or not etc ...
		txnMgr.lockObject(id, key, lockType);
		//--Also we keep the keys unique by pre-pending our servernames.
		//txnMgr.lockObject(id, rmServerName+"-"+key, lockType);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_LOCK, null, key,PerfMonitor.END);

		//Fetch data from this txn's buffer
		RMHashtable txnBuffer = (RMHashtable)txnBuffers.get(id);

		//looks like we don't have any record of this txn..
		//First time ?
		if(txnBuffer == null)
		{ 
			PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.START);
			//Lets register with TxnManager first.
			txnMgr.registerTxnListner(this,id);
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
	private void writeData( int id, String key, RMItem value ) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		//txnMgr.lockObject(this, id, key, lockType);

		//This will take care of locks, getting into our local txn buffer etc.
		readData(id, key, LockManager.WRITE);

		//Get this txn's buffer.
		RMHashtable txnBuffer = (RMHashtable)txnBuffers.get(id);
		value.markAsModified();

		//Write it to the local buffer.
		txnBuffer.put(key, value);

	}
	
	// Remove the item out of storage
	protected RMItem removeData(int id, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		//Get it to our local txn buffer, if it is not already there.
	  RMItem rmItem = readData(id, key, LockManager.WRITE);

		//We do only "soft" deletes on txn buffers.
		if(rmItem != null)
			rmItem.mark4Deletion();

		return rmItem;

	}

	
	// deletes the entire item
	protected boolean deleteItem(int id, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteItem(" + id + ", " + key + ") called" );
		ReservableItem curObj = (ReservableItem) readData( id, key, LockManager.WRITE );
		// Check if there is such an item in the storage
		if( curObj == null ) {
			Trace.warn("RM::deleteItem(" + id + ", " + key + ") failed--item doesn't exist" );
			return false;
		} else {
			if(curObj.getReserved()==0){
				removeData(id, curObj.getKey());
				Trace.info("RM::deleteItem(" + id + ", " + key + ") item deleted" );
				return true;
			}
			else{
				Trace.info("RM::deleteItem(" + id + ", " + key + ") item can't be deleted because some customers reserved it" );
				return false;
			}
		} // if
	}
	

	// query the number of available seats/rooms/cars
	protected int queryNum(int id, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryNum(" + id + ", " + key + ") called" );
		ReservableItem curObj = (ReservableItem) readData( id, key);
		int value = 0;  
		if( curObj != null ) {
			value = curObj.getCount();
		} // else
		Trace.info("RM::queryNum(" + id + ", " + key + ") returns count=" + value);
		return value;
	}	
	
	// query the price of an item
	protected int queryPrice(int id, String key) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") called" );
		ReservableItem curObj = (ReservableItem) readData( id, key);
		int value = 0; 
		if( curObj != null ) {
			value = curObj.getPrice();
		} // else
		Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") returns cost=$" + value );
		return value;		
	}
	
	// reserve an item
	protected boolean reserveItem(int id, int customerID, String key, String location, boolean checkOnly) throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{

		Trace.info("RM::reserveItem( " + id + ", customer=" + customerID + ", " +key+ ", "+location+" ) called" );		
		// Read customer object if it exists (and write lock it)
		Customer cust = (Customer) readData( id, Customer.getKey(customerID) , LockManager.WRITE);		
		if( cust == null ) {
			Trace.warn("RM::reserveCar( " + id + ", " + customerID + ", " + key + ", "+location+")  failed--customer doesn't exist" );
			return false;
		} 
		
		// check if the item is available and write lock it
		ReservableItem item = (ReservableItem)readData(id, key, LockManager.WRITE);
		if(item==null){
			Trace.warn("RM::reserveItem( " + id + ", " + customerID + ", " + key+", " +location+") failed--item doesn't exist" );
			return false;
		}else if(item.getCount()==0){
			Trace.warn("RM::reserveItem( " + id + ", " + customerID + ", " + key+", " + location+") failed--No more items" );
			return false;
		}else{			

			//Don't make actual changes we were told to keep things on hold.
		  if(checkOnly) return true;

			cust.reserve( key, location, item.getPrice());		
			writeData( id, cust.getKey(), cust );
			
			// decrease the number of available items in the storage
			item.setCount(item.getCount() - 1);
			item.setReserved(item.getReserved()+1);
			
			Trace.info("RM::reserveItem( " + id + ", " + customerID + ", " + key + ", " +location+") succeeded" );
			return true;
		}		
	}
	
	// Create a new flight, or add seats to existing flight
	//  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::addFlight(" + id + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );
		Flight curObj = (Flight) readData( id, Flight.getKey(flightNum), LockManager.WRITE );
		if( curObj == null ) {
			// doesn't exist...add it
			Flight newObj = new Flight( flightNum, flightSeats, flightPrice );
			writeData( id, newObj.getKey(), newObj );
			Trace.info("RM::addFlight(" + id + ") created new flight " + flightNum + ", seats=" +
					flightSeats + ", price=$" + flightPrice );
		} else {
			// add seats to existing flight and update the price...
			curObj.setCount( curObj.getCount() + flightSeats );
			if( flightPrice > 0 ) {
				curObj.setPrice( flightPrice );
			} // if
			writeData( id, curObj.getKey(), curObj );
			Trace.info("RM::addFlight(" + id + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice );
		} // else
		return(true);
	}


	
	public boolean deleteFlight(int id, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteFlight(" + id + ", " + flightNum + ") called" );
		return deleteItem(id, Flight.getKey(flightNum));
	}



	// Create a new room location or add rooms to an existing location
	//  NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int id, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::addRooms(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		Hotel curObj = (Hotel) readData( id, Hotel.getKey(location), LockManager.WRITE );
		if( curObj == null ) {
			// doesn't exist...add it
			Hotel newObj = new Hotel( location, count, price );
			writeData( id, newObj.getKey(), newObj );
			Trace.info("RM::addRooms(" + id + ") created new room location " + location + ", count=" + count + ", price=$" + price );
		} else {
			// add count to existing object and update price...
			curObj.setCount( curObj.getCount() + count );
			if( price > 0 ) {
				curObj.setPrice( price );
			} // if
			writeData( id, curObj.getKey(), curObj );
			Trace.info("RM::addRooms(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
		} // else
		return(true);
	}

	// Delete rooms from a location
	public boolean deleteRooms(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteRooms(" + id + ", " + location + ") called" );
		return deleteItem(id, Hotel.getKey(location));
		
	}

	// Create a new car location or add cars to an existing location
	//  NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int id, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::addCars(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		Car curObj = (Car) readData( id, Car.getKey(location), LockManager.WRITE );
		if( curObj == null ) {
			// car location doesn't exist...add it
			Car newObj = new Car( location, count, price );
			writeData( id, newObj.getKey(), newObj );
			Trace.info("RM::addCars(" + id + ") created new location " + location + ", count=" + count + ", price=$" + price );
		} else {
			// add count to existing car location and update price...
			curObj.setCount( curObj.getCount() + count );
			if( price > 0 ) {
				curObj.setPrice( price );
			} // if
			writeData( id, curObj.getKey(), curObj );
			Trace.info("RM::addCars(" + id + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price );
		} // else
		return(true);
	}


	// Delete cars from a location
	public boolean deleteCars(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::deleteCars(" + id + ", " + location + ") called" );
		return deleteItem(id, Car.getKey(location));
	}



	// Returns the number of empty seats on this flight
	public int queryFlight(int id, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryFlight(" + id + ", " + flightNum + ") called" );
		return queryNum(id, Flight.getKey(flightNum));
	}

	// Returns the number of reservations for this flight. 
//	public int queryFlightReservations(int id, int flightNum)
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
	public int queryFlightPrice(int id, int flightNum )
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryFlightPrice(" + id + ", " + flightNum + ") called" );
		return queryPrice(id, Flight.getKey(flightNum));
	}


	// Returns the number of rooms available at a location
	public int queryRooms(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryRooms(" + id + ", " + location + ") called" );
		return queryNum(id, Hotel.getKey(location));
	}


	
	
	// Returns room price at this location
	public int queryRoomsPrice(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryRoomsPrice(" + id + ", " + location + ") called" );
		return queryPrice(id, Hotel.getKey(location));
	}


	// Returns the number of cars available at a location
	public int queryCars(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryCars(" + id + ", " + location + ") called" );
		return queryNum(id, Car.getKey(location));
	}


	// Returns price of cars at this location
	public int queryCarsPrice(int id, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::queryPrice(" + id + ", " + location + ") called" );
		return queryPrice(id, Car.getKey(location));
	}

	// Returns data structure containing customer reservation info. Returns null if the
	//  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
	//  reservations.
	public RMHashtable getCustomerReservations(int id, int customerID)
		throws RemoteException, InvalidTransactionException,DeadlockException,TransactionAbortedException
	{
		Trace.info("RM::getCustomerReservations(" + id + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, Customer.getKey(customerID) );
		if( cust == null ) {
			Trace.warn("RM::getCustomerReservations failed(" + id + ", " + customerID + ") failed--customer doesn't exist" );
			return null;
		} else {
			return cust.getReservations();
		} // if
	}

	// return a bill
	public String queryCustomerInfo(int id, int customerID)
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, Customer.getKey(customerID) );
		if( cust == null ) {
			Trace.warn("RM::queryCustomerInfo(" + id + ", " + customerID + ") failed--customer doesn't exist" );
			return "";   // NOTE: don't change this--WC counts on this value indicating a customer does not exist...
		} else {
				String s = cust.printBill();
				Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID + "), bill follows..." );
				System.out.println( s );
				return s;
		} // if
	}

  // customer functions
  // new customer just returns a unique customer identifier
	
  public int newCustomer(int id)
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		Trace.info("INFO: RM::newCustomer(" + id + ") called" );
		// Generate a globally unique ID for the new customer
		Customer cust = new Customer( id );
		writeData( id, cust.getKey(), cust );
		Trace.info("RM::newCustomer(" + id + ") returns ID=" + id );
		return id;
	}

	// I opted to pass in customerID instead. This makes testing easier
  public boolean newCustomer(int id, int customerID )
		throws RemoteException, InvalidTransactionException,DeadlockException, TransactionAbortedException
	{
		Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, Customer.getKey(customerID), LockManager.WRITE );
		if( cust == null ) {
			cust = new Customer(customerID);
			writeData( id, cust.getKey(), cust );
			Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") created a new customer" );
			return true;
		} else {
			Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID + ") failed--customer already exists");
			return false;
		} // else
	}


	// Deletes customer from the database. 
	public boolean deleteCustomer(int id, int customerID)
			throws RemoteException, InvalidTransactionException, DeadlockException,TransactionAbortedException
	{
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called" );
		Customer cust = (Customer) readData( id, Customer.getKey(customerID), LockManager.WRITE );
		if( cust == null ) {
			Trace.warn("RM::deleteCustomer(" + id + ", " + customerID + ") failed--customer doesn't exist" );
			return false;
		} else {			
			// Increase the reserved numbers of all reservable items which the customer reserved. 
			RMHashtable reservationHT = cust.getReservations();
			for(Enumeration e = reservationHT.keys(); e.hasMoreElements();){		
				String reservedkey = (String) (e.nextElement());
				ReservedItem reserveditem = cust.getReservedItem(reservedkey);
				Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times"  );
				ReservableItem item  = (ReservableItem) readData(id, reserveditem.getKey(), LockManager.WRITE);
				Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") has reserved " + reserveditem.getKey() + "which is reserved" +  item.getReserved() +  " times and is still available " + item.getCount() + " times"  );
				item.setReserved(item.getReserved()-reserveditem.getCount());
				item.setCount(item.getCount()+reserveditem.getCount());
			}
			
			// remove the customer from the storage
			removeData(id, cust.getKey());
			
			Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") succeeded" );
			return true;
		} // if
	}




	// Frees flight reservation record. Flight reservation records help us make sure we
	//  don't delete a flight if one or more customers are holding reservations
//	public boolean freeFlightReservation(int id, int flightNum)
//		throws RemoteException
//	{
//		Trace.info("RM::freeFlightReservations(" + id + ", " + flightNum + ") called" );
//		RMInteger numReservations = (RMInteger) readData( id, Flight.getNumReservationsKey(flightNum) );
//		if( numReservations != null ) {
//			numReservations = new RMInteger( Math.max( 0, numReservations.getValue()-1) );
//		} // if
//		writeData(id, Flight.getNumReservationsKey(flightNum), numReservations );
//		Trace.info("RM::freeFlightReservations(" + id + ", " + flightNum + ") succeeded, this flight now has "
//				+ numReservations + " reservations" );
//		return true;
//	}
//	

	
	// Adds car reservation to this customer. 
	public boolean reserveCar(int id, int customerID, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveCar(id, customerID, location, false);
	}
	public boolean reserveCar(int id, int customerID, String location, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveCar(" + id + ", " + location + ", " + checkOnly +") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.START);
		boolean status = reserveItem(id, customerID, Car.getKey(location), location, checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.END);
		return status;
	}


	// Adds room reservation to this customer. 
	public boolean reserveRoom(int id, int customerID, String location)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveRoom(id, customerID, location, false);
	}
	public boolean reserveRoom(int id, int customerID, String location, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveRoom(" + id + ", " + customerID + ", "  + location + ", " + checkOnly + ") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.START);
		boolean status = reserveItem(id, customerID, Hotel.getKey(location), location, checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.END);
		return status;
	}

	// Adds flight reservation to this customer.  
	public boolean reserveFlight(int id, int customerID, int flightNum)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  return reserveFlight(id, customerID, flightNum, false);
	}
	public boolean reserveFlight(int id, int customerID, int flightNum, boolean checkOnly)
		throws RemoteException, InvalidTransactionException, DeadlockException, TransactionAbortedException
	{
	  Trace.info("RM::reserveFlight(" + id + ", " + customerID + ", "  + flightNum + ", " + checkOnly + ") called" );
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.START);
		boolean status = reserveItem(id, customerID, Flight.getKey(flightNum), String.valueOf(flightNum), checkOnly);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.END);
		return status;
	}
	
	/* reserve an itinerary */
	// This is taken care of at MW server.
    public boolean itinerary(int id,int customer,Vector flightNumbers,String location,boolean Car,boolean Room)
	throws RemoteException {
	    Trace.info("RM::itinerary(" + id + ", " + customer + ", "  + location + ", " + Car + ", " + Room + ") called : UNSUPPORTED" );
    	return false;
    }


  public void abortTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException
	{
		//Get rid of this txn's buffer
		Trace.info("RM::abortTxn(" + txId + ") called" );
		txnBuffers.remove(txId);
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
	}

	public void commitTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("RM::commitTxn(" + txId + ") called" );
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
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);

	}

	public void shutdown() throws RemoteException
	{
		Trace.info("RM::shutdown() called" );
	}

	//We don't need it in the RM ?
	public int startTxn() throws  RemoteException
	{ return 0; }

}
