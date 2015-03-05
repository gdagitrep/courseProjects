// -------------------------------
// adapated from Kevin T. Manley
// CSE 593
//
package MWImpl;

import ResInterface.*;

import MWInterface.*;

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

//public class MWLayer extends java.rmi.server.UnicastRemoteObject
public class MWServer implements MWLayer, JangoRemoteServer
{

	static ResourceManager htl_rm = null;
	static ResourceManager car_rm = null;
	static ResourceManager flt_rm = null;

  protected static TxnManagerIntf txnMgr;

	protected CrashPoint crashPoint;
	
	//-- protected RMHashtable m_itemHT = new RMHashtable();


	public static void main(String args[]) {
				PerfMonitor.setup();
				Trace.setup();
	 
         // Create and install a security manager
         if (System.getSecurityManager() == null) {
	     System.setSecurityManager(new DSRMISecurityManager());
         }
	 try 
	     {

     // get a reference to the Transaction Manager.
     txnMgr = new TxnManagerIntf_RepStub(Integer.parseInt(System.getenv("TMGR_REPLIC_CNT")), System.getenv("TMGR_REPLIC_CFG"));

		 // get a reference to the hotel server.
		 htl_rm = new ResourceManager_RepStub(Integer.parseInt(System.getenv("HTL_REPLIC_CNT")), System.getenv("HTL_REPLIC_CFG"));
		 
		 // get a reference to the car reservation server.
		 car_rm = new ResourceManager_RepStub(Integer.parseInt(System.getenv("CAR_REPLIC_CNT")), System.getenv("CAR_REPLIC_CFG"));
		 
		 // get a reference to the hotel server.
		 flt_rm = new ResourceManager_RepStub(Integer.parseInt(System.getenv("FLT_REPLIC_CNT")), System.getenv("FLT_REPLIC_CFG"));
		 
		 // create a new Server object
		 MWServer obj = new MWServer();
		 // dynamically generate the stub (client proxy)
		 MWLayer rm = (MWLayer) UnicastRemoteObject.exportObject(obj, 0);
		 
		 // Bind the remote object's stub in the registry
		 Registry registry = LocateRegistry.getRegistry(System.getenv("MIDW_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("MIDW_RMI_PORT")));
		 registry.rebind(System.getenv("MIDW_RMI_OBJ_NAME"), rm);
		 registry.rebind(System.getenv("MIDW_RMI_OBJ_NAME")+"_jango", (JangoRemoteServer)obj);
		 
		 System.err.println("Server ready");
	     } 
	 catch (Exception e) 
	     {
		 System.err.println("Server exception: " + e.toString());
		 e.printStackTrace();
	     }
	 }
	 
	 public MWServer() throws RemoteException {

	 	crashPoint = new CrashPoint();
		loadCrashPoints();
	 }

	 protected void loadCrashPoints()
	 {
	 	crashPoint.addCrashPoint("crash_before_read_opn");
	 	crashPoint.addCrashPoint("crash_after_read_opn");

	 	crashPoint.addCrashPoint("crash_before_write_opn");
	 	crashPoint.addCrashPoint("crash_after_write_opn");

	 	crashPoint.addCrashPoint("crash_newcustomerid_before");
	 	crashPoint.addCrashPoint("crash_newcustomerid_after_htl_rm");
	 	crashPoint.addCrashPoint("crash_newcustomerid_after_car_rm");
	 	crashPoint.addCrashPoint("crash_newcustomerid_after");

	 	crashPoint.addCrashPoint("crash_newcustomer_before");
	 	crashPoint.addCrashPoint("crash_newcustomer_after");

	 	crashPoint.addCrashPoint("crash_deletecustomer_before");
	 	crashPoint.addCrashPoint("crash_deletecustomer_after_htl_rm");
	 	crashPoint.addCrashPoint("crash_deletecustomer_after_car_rm");
	 	crashPoint.addCrashPoint("crash_deletecustomer_after");

	 	crashPoint.addCrashPoint("crash_itinerary_before");
	 	crashPoint.addCrashPoint("crash_itinerary_after_flt_hold");
	 	crashPoint.addCrashPoint("crash_itinerary_after_car_hold");
	 	crashPoint.addCrashPoint("crash_itinerary_after_all_hold");
	 	crashPoint.addCrashPoint("crash_itinerary_after_flt_rsrv");
	 	crashPoint.addCrashPoint("crash_itinerary_after_car_rsrv");
	 	crashPoint.addCrashPoint("crash_itinerary_after");

		crashPoint.addCrashPoint("crash_abort_before");
		crashPoint.addCrashPoint("crash_abort_after");

		crashPoint.addCrashPoint("crash_commit_before");
		crashPoint.addCrashPoint("crash_commit_after");

		crashPoint.addCrashPoint("crash_start_before");
		crashPoint.addCrashPoint("crash_start_after");

	 }

	 public String setCrashPoint(String point, boolean status) throws RemoteException
	 {
	 	return crashPoint.setCrashPoint(point, status);
	 }

  public Hashtable<String, Boolean> getCrashPoints() throws RemoteException
  { return crashPoint.getCrashPoints(); }
	 

	// Create a new flight, or add seats to existing flight
	//  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int id, int rqId, int flightNum, int flightSeats, int flightPrice)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::addFlight(" + id + ", " + rqId + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );

		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = flt_rm.addFlight(id,rqId,flightNum,flightSeats,flightPrice);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}


	
	public boolean deleteFlight(int id, int rqId, int flightNum)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::deleteFlight(" + id + ", " + rqId + ", " + flightNum + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = flt_rm.deleteFlight(id, rqId, flightNum);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}



	// Create a new room location or add rooms to an existing location
	//  NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int id, int rqId, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::addRooms(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = htl_rm.addRooms(id, rqId, location,count,price);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}

	// Delete rooms from a location
	public boolean deleteRooms(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::deleteRooms(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = htl_rm.deleteRooms(id,  rqId, location);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}

	// Create a new car location or add cars to an existing location
	//  NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int id, int rqId, String location, int count, int price)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::addCars(" + id + ", " + rqId + ", " + location + ", " + count + ", $" + price + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = car_rm.addCars(id,  rqId, location, count, price);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}


	// Delete cars from a location
	public boolean deleteCars(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::deleteCars(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean b = car_rm.deleteCars(id,  rqId, location);
		crashPoint.crashCheck("crash_after_write_opn");
		return b;
	}



	// Returns the number of empty seats on this flight
	public int queryFlight(int id, int rqId, int flightNum)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryFlight(" + id + ", " + rqId + ", " + flightNum + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = flt_rm.queryFlight(id,  rqId, flightNum);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
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
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryFlightPrice(" + id + ", " + rqId + ", " + flightNum + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = flt_rm.queryFlightPrice(id,  rqId, flightNum);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
	}


	// Returns the number of rooms available at a location
	public int queryRooms(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryRooms(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = htl_rm.queryRooms(id, rqId, location);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
	}


	
	
	// Returns room price at this location
	public int queryRoomsPrice(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryRoomsPrice(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = htl_rm.queryRoomsPrice(id,  rqId, location);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
	}


	// Returns the number of cars available at a location
	public int queryCars(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryCars(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = car_rm.queryCars(id,  rqId, location);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
	}


	// Returns price of cars at this location
	public int queryCarsPrice(int id, int rqId, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryCarsPrice(" + id + ", " + rqId + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_read_opn");
		int val = car_rm.queryCarsPrice(id,  rqId, location);
		crashPoint.crashCheck("crash_after_read_opn");
		return val;
	}

	// Returns data structure containing customer reservation info. Returns null if the
	//  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
	//  reservations.
		// TODO
		// Temporarily disabled as not used anywhere ...
		/*
	public RMHashtable getCustomerReservations(int id, int rqId, int customerID)
		throws RemoteException
	{
		Trace.info("MW::getCustomerReservations(" + id + ", " + rqId + ", " + customerID + ") called" );
		RMHashtable htl_rsrv = htl_rm.getCustomerReservations(id,customerID);
		RMHashtable car_rsrv = car_rm.getCustomerReservations(id,customerID);
		RMHashtable flt_rsrv = flt_rm.getCustomerReservations(id,customerID);

		if( htl_rsrv == null && car_rsrv == null && flt_rsrv == null ) {
			Trace.warn("RM::getCustomerReservations failed(" + id + ", " + rqId + ", " + customerID + ") failed--customer doesn't exist" );
			return null;
		} else {


		RMHashtable htbl = new RMHashtable( (htl_rsrv != null ? htl_rsrv.size() : 0) +
		                                    (car_rsrv != null ? car_rsrv.size() : 0) +
		                                    (flt_rsrv != null ? flt_rsrv.size() : 0) 
																			 );

	 ///TODO
	 return null;



		} // if
	}
		*/

	// return a bill
	public String queryCustomerInfo(int id, int rqId, int customerID)
		throws RemoteException,InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("MW::queryCustomerInfo(" + id + ", " + rqId + ", " + customerID + ") called" );

		crashPoint.crashCheck("crash_before_read_opn");
		String s = "";
		s += htl_rm.queryCustomerInfo(id,  rqId, customerID) + car_rm.queryCustomerInfo(id,  rqId, customerID) + flt_rm.queryCustomerInfo(id,  rqId, customerID);
	  if (s.equals(""))		return "";   // NOTE: don't change this--WC counts on this value indicating a customer does not exist...

		s = "Bill for customer " + customerID + "\n" + s;

		Trace.info("MW::queryCustomerInfo(" + id + ", " + rqId + ", " + customerID + "), bill follows..." );
	  System.out.println( s );
		crashPoint.crashCheck("crash_after_read_opn");
		return s;
	}

  // customer functions
  // new customer just returns a unique customer identifier
	
  public int newCustomer(int id, int rqId)
		throws RemoteException,InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("INFO: MW::newCustomer(" + id + ") called" );
		// Generate a globally unique ID for the new customer

		Random rand = new Random(id*104471 + (rqId+1)*(rqId+3)*92077);
		int cid = Math.abs(rand.nextInt());
		//int cid = Integer.parseInt( String.valueOf(id) + String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) + String.valueOf( Math.round( Math.random() * 100 + 1 )));

		crashPoint.crashCheck("crash_newcustomerid_before");
		htl_rm.newCustomer(cid, rqId);
		crashPoint.crashCheck("crash_newcustomerid_after_htl_rm");
		car_rm.newCustomer(cid, rqId);
		crashPoint.crashCheck("crash_newcustomerid_after_car_rm");
		flt_rm.newCustomer(cid, rqId);
		crashPoint.crashCheck("crash_newcustomerid_after");

		Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid );
		return cid;
	}

	// I opted to pass in customerID instead. This makes testing easier
  public boolean newCustomer(int id, int rqId, int customerID )
		throws RemoteException,InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("INFO: MW::newCustomer(" + id + ", " + rqId + ", " + customerID + ") called" );

		crashPoint.crashCheck("crash_newcustomer_before");
		boolean b = htl_rm.newCustomer(id, rqId, customerID) && car_rm.newCustomer(id, rqId, customerID) && flt_rm.newCustomer(id, rqId, customerID);
		crashPoint.crashCheck("crash_newcustomer_after");
		return b;
	}


	// Deletes customer from the database. 
	public boolean deleteCustomer(int id, int rqId, int customerID)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		Trace.info("RM::deleteCustomer(" + id + ", " + rqId + ", " + customerID + ") called" );

		boolean b = true;

		crashPoint.crashCheck("crash_deletecustomer_before");
		b = b && htl_rm.deleteCustomer(id, rqId, customerID);
		crashPoint.crashCheck("crash_deletecustomer_after_htl_rm");
		b = b && car_rm.deleteCustomer(id, rqId, customerID);
		crashPoint.crashCheck("crash_deletecustomer_after_car_rm");
		b = b && flt_rm.deleteCustomer(id, rqId, customerID);
		crashPoint.crashCheck("crash_deletecustomer_after");

		return b;
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
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.START);
		Trace.info("MW::reserveCar(" + id + ", " + rqId + ", " + customerID + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean status = car_rm.reserveCar(id, rqId, customerID, location);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_CAR, null, customerID+"-"+location,PerfMonitor.END);
		crashPoint.crashCheck("crash_after_write_opn");
		return status;
	}


	// Adds room reservation to this customer. 
	public boolean reserveRoom(int id, int rqId, int customerID, String location)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.START);
		Trace.info("MW::reserveRoom(" + id + ", " + rqId + ", " + customerID + ", " + location + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean status = htl_rm.reserveRoom(id, rqId, customerID, location);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_HOTEL, null, customerID+"-"+location,PerfMonitor.END);
		crashPoint.crashCheck("crash_after_write_opn");
		return status;
	}
	// Adds flight reservation to this customer.  
	public boolean reserveFlight(int id, int rqId, int customerID, int flightNum)
		throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.START);
		Trace.info("MW::reserveFlight(" + id + ", " + rqId + ", " + customerID + ", " + flightNum + ") called" );
		crashPoint.crashCheck("crash_before_write_opn");
		boolean status = flt_rm.reserveFlight(id, rqId, customerID, flightNum);
		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_FLIGHT, null, customerID+"-"+flightNum,PerfMonitor.END);
		crashPoint.crashCheck("crash_after_write_opn");
		return status;
	}
	
	/* reserve an itinerary */
    public boolean itinerary(int id, int rqId,int customer,Vector flightNumbers,String location,boolean Car,boolean Room)
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException
	{

		PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_ITINERARY, null, customer+"-"+location,PerfMonitor.START);
		Trace.info("MW::itinerary(" + id + ", " + rqId + ", " + customer + ", " + location + ", " + Car + ", " + Room + ") called" );

     boolean status = true;

		 crashPoint.crashCheck("crash_itinerary_before");
		 //First round, lets look for availability
     for(int i=0;i<flightNumbers.size();i++)
        status = status && flt_rm.reserveFlight(id, -1*(i+rqId), customer,Integer.parseInt((String)flightNumbers.get(i)), true);

		 crashPoint.crashCheck("crash_itinerary_after_flt_hold");

     if(Car)
         status = status && car_rm.reserveCar(id, -1*rqId,customer, location, true);
		 crashPoint.crashCheck("crash_itinerary_after_car_hold");

     if(Room)
         status = status && htl_rm.reserveRoom(id, -1*rqId,customer,location, true);

		 crashPoint.crashCheck("crash_itinerary_after_all_hold");

		 //At least one of the item is not available, so return unsuccess
		 if(!status) 
		 {
		   PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_ITINERARY, null, customer+"-"+location,PerfMonitor.END);
		   return status;
		 }

		 //Ok, so we have stuff, and we got the locks ... so go ahead and book it.
     for(int i=0;i<flightNumbers.size();i++)
        flt_rm.reserveFlight(id, -1*(i+rqId+2), customer,Integer.parseInt((String)flightNumbers.get(i)));

		 crashPoint.crashCheck("crash_itinerary_after_flt_rsrv");

     if(Car)
         car_rm.reserveCar(id, rqId,customer, location);
		 crashPoint.crashCheck("crash_itinerary_after_car_rsrv");

     if(Room)
         htl_rm.reserveRoom(id, rqId,customer,location);

		 //We are successfull in booking.
    	
		 PerfMonitor.recordPerf(id, PerfMonitor.REQUEST_RESERVE_ITINERARY, null, customer+"-"+location,PerfMonitor.END);

		 crashPoint.crashCheck("crash_itinerary_after");
     return status;
   }


  public void abortTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException
  { 
		Trace.info("MW::abortTxn(" + txId + ") called" );
		crashPoint.crashCheck("crash_abort_before");
	  txnMgr.abortTxn(txId, null); 
		crashPoint.crashCheck("crash_abort_after");
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.ERR);
	}

  public void commitTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException 
	{ 
		Trace.info("MW::commitTxn(" + txId + ") called" );
		crashPoint.crashCheck("crash_commit_before");
	  txnMgr.commitTxn(txId, null); 
		crashPoint.crashCheck("crash_commit_after");
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.END);
	}

  public void shutdown() throws RemoteException
  {
		Trace.info("MW::shutdown( ) called" );
    flt_rm.shutdown();
    htl_rm.shutdown();
    car_rm.shutdown();
    txnMgr.shutdown();
  }  

  //We don't need it in the RM ?
  public int startTxn() throws  RemoteException
  { 
		Trace.info("MW::startTxn( ) called" );
	  int txId =  txnMgr.startTxn(); 
		crashPoint.crashCheck("crash_start_before");
		PerfMonitor.recordPerf(txId, PerfMonitor.REQUEST_TXN, null, null,PerfMonitor.START);
		crashPoint.crashCheck("crash_start_after");
		return txId;
	}

	public int  getServerStatus() throws java.rmi.RemoteException
	{ return JANGO_SERVER_MASTER; }


  public boolean switchToMaster() throws java.rmi.RemoteException
	{ return true; }

  public void registerReplic(String host, int port, String objName) throws java.rmi.RemoteException
	{ }

	}
