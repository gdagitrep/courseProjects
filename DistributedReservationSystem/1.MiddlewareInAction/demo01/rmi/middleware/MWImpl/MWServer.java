// -------------------------------
// adapated from Kevin T. Manley
// CSE 593
//
package MWImpl;

import ResInterface.*;

import MWInterface.*;

import G13Log.*;
import G13Components.*;

import java.util.*;
import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//public class MWLayer extends java.rmi.server.UnicastRemoteObject
public class MWServer
	implements MWLayer {

	static ResourceManager htl_rm = null;
	static ResourceManager car_rm = null;
	static ResourceManager flt_rm = null;
	
	//-- protected RMHashtable m_itemHT = new RMHashtable();


	public static void main(String args[]) {
        // Figure out where server is running
        String server = "localhost";

         if (args.length == 1) {
             server = server + ":" + args[0];
         } else if (args.length != 0 &&  args.length != 1) {
             System.err.println ("Wrong usage");
             System.out.println("Usage: java MWImpl.MWServer [port]");
             System.exit(1);
         }
	 
	 try 
	     {
		 // get a reference to the hotel server.
		 Registry htl_registry = LocateRegistry.getRegistry(System.getenv("HTL_SRVR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("HTL_SRVR_RMI_PORT")));
		 htl_rm = (ResourceManager)htl_registry.lookup(System.getenv("HTL_SRVR_RMI_OBJ_NAME"));
		 
		 // get a reference to the car reservation server.
		 Registry car_registry = LocateRegistry.getRegistry(System.getenv("CAR_SRVR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("CAR_SRVR_RMI_PORT")));
		 car_rm = (ResourceManager)car_registry.lookup(System.getenv("CAR_SRVR_RMI_OBJ_NAME"));
		 
		 // get a reference to the hotel server.
		 Registry flt_registry = LocateRegistry.getRegistry(System.getenv("FLT_SRVR_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("FLT_SRVR_RMI_PORT")));
		 flt_rm = (ResourceManager)flt_registry.lookup(System.getenv("FLT_SRVR_RMI_OBJ_NAME"));
		 
		 // create a new Server object
		 MWServer obj = new MWServer();
		 // dynamically generate the stub (client proxy)
		 MWLayer rm = (MWLayer) UnicastRemoteObject.exportObject(obj, 0);
		 
		 // Bind the remote object's stub in the registry
		 Registry registry = LocateRegistry.getRegistry(System.getenv("MIDW_RMI_REGISTRY_HOST"), Integer.parseInt(System.getenv("MIDW_RMI_PORT")));
		 registry.rebind(System.getenv("MIDW_RMI_OBJ_NAME"), rm);
		 
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
	 
	 public MWServer() throws RemoteException {
	 }
	 

	// Create a new flight, or add seats to existing flight
	//  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice)
		throws RemoteException
	{
		Trace.info("MW::addFlight(" + id + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );
		return flt_rm.addFlight(id,flightNum,flightSeats,flightPrice);
	}


	
	public boolean deleteFlight(int id, int flightNum)
		throws RemoteException
	{
		Trace.info("MW::deleteFlight(" + id + ", " + flightNum + ") called" );
		return flt_rm.deleteFlight(id, flightNum);
	}



	// Create a new room location or add rooms to an existing location
	//  NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int id, String location, int count, int price)
		throws RemoteException
	{
		Trace.info("MW::addRooms(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		return htl_rm.addRooms(id,location,count,price);
	}

	// Delete rooms from a location
	public boolean deleteRooms(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::deleteRooms(" + id + ", " + location + ") called" );
		return htl_rm.deleteRooms(id, location);
	}

	// Create a new car location or add cars to an existing location
	//  NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int id, String location, int count, int price)
		throws RemoteException
	{
		Trace.info("MW::addCars(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		return car_rm.addCars(id, location, count, price);
	}


	// Delete cars from a location
	public boolean deleteCars(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::deleteCars(" + id + ", " + location + ") called" );
		return car_rm.deleteCars(id, location);
	}



	// Returns the number of empty seats on this flight
	public int queryFlight(int id, int flightNum)
		throws RemoteException
	{
		Trace.info("MW::queryFlight(" + id + ", " + flightNum + ") called" );
		return flt_rm.queryFlight(id, flightNum);
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
		throws RemoteException
	{
		Trace.info("MW::queryFlightPrice(" + id + ", " + flightNum + ") called" );
		return flt_rm.queryFlightPrice(id, flightNum);
	}


	// Returns the number of rooms available at a location
	public int queryRooms(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::queryRooms(" + id + ", " + location + ") called" );
		return htl_rm.queryRooms(id,location);
	}


	
	
	// Returns room price at this location
	public int queryRoomsPrice(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::queryRoomsPrice(" + id + ", " + location + ") called" );
		return htl_rm.queryRoomsPrice(id, location);
	}


	// Returns the number of cars available at a location
	public int queryCars(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::queryCars(" + id + ", " + location + ") called" );
		return car_rm.queryCars(id, location);
	}


	// Returns price of cars at this location
	public int queryCarsPrice(int id, String location)
		throws RemoteException
	{
		Trace.info("MW::queryCarsPrice(" + id + ", " + location + ") called" );
		return car_rm.queryCarsPrice(id, location);
	}

	// Returns data structure containing customer reservation info. Returns null if the
	//  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
	//  reservations.
		// TODO
		// Temporarily disabled as not used anywhere ...
		/*
	public RMHashtable getCustomerReservations(int id, int customerID)
		throws RemoteException
	{
		Trace.info("MW::getCustomerReservations(" + id + ", " + customerID + ") called" );
		RMHashtable htl_rsrv = htl_rm.getCustomerReservations(id,customerID);
		RMHashtable car_rsrv = car_rm.getCustomerReservations(id,customerID);
		RMHashtable flt_rsrv = flt_rm.getCustomerReservations(id,customerID);

		if( htl_rsrv == null && car_rsrv == null && flt_rsrv == null ) {
			Trace.warn("RM::getCustomerReservations failed(" + id + ", " + customerID + ") failed--customer doesn't exist" );
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
	public String queryCustomerInfo(int id, int customerID)
		throws RemoteException
	{
		Trace.info("MW::queryCustomerInfo(" + id + ", " + customerID + ") called" );

		String s = "";
		s += htl_rm.queryCustomerInfo(id, customerID) + car_rm.queryCustomerInfo(id, customerID) + flt_rm.queryCustomerInfo(id, customerID);
	  if (s.equals(""))		return "";   // NOTE: don't change this--WC counts on this value indicating a customer does not exist...

		s = "Bill for customer " + customerID + "\n" + s;

		Trace.info("MW::queryCustomerInfo(" + id + ", " + customerID + "), bill follows..." );
	  System.out.println( s );
		return s;
	}

  // customer functions
  // new customer just returns a unique customer identifier
	
  public int newCustomer(int id)
		throws RemoteException
	{
		Trace.info("INFO: MW::newCustomer(" + id + ") called" );
		// Generate a globally unique ID for the new customer

		int cid = Integer.parseInt( String.valueOf(id) +
								String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
								String.valueOf( Math.round( Math.random() * 100 + 1 )));

		htl_rm.newCustomer(cid);
		car_rm.newCustomer(cid);
		flt_rm.newCustomer(cid);

		Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid );
		return cid;
	}

	// I opted to pass in customerID instead. This makes testing easier
  public boolean newCustomer(int id, int customerID )
		throws RemoteException
	{
		Trace.info("INFO: MW::newCustomer(" + id + ", " + customerID + ") called" );

		return htl_rm.newCustomer(id, customerID) && car_rm.newCustomer(id, customerID) && flt_rm.newCustomer(id, customerID);
	}


	// Deletes customer from the database. 
	public boolean deleteCustomer(int id, int customerID)
			throws RemoteException
	{
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called" );

		return htl_rm.deleteCustomer(id, customerID) && car_rm.deleteCustomer(id, customerID) && flt_rm.deleteCustomer(id, customerID) ;
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
		throws RemoteException
	{
		Trace.info("MW::reserveCar(" + id + ", " + customerID + ", " + location + ") called" );
		return car_rm.reserveCar(id, customerID, location);
	}


	// Adds room reservation to this customer. 
	public boolean reserveRoom(int id, int customerID, String location)
		throws RemoteException
	{
		Trace.info("MW::reserveRoom(" + id + ", " + customerID + ", " + location + ") called" );
		return htl_rm.reserveRoom(id, customerID, location);
	}
	// Adds flight reservation to this customer.  
	public boolean reserveFlight(int id, int customerID, int flightNum)
		throws RemoteException
	{
		Trace.info("MW::reserveFlight(" + id + ", " + customerID + ", " + flightNum + ") called" );
		return flt_rm.reserveFlight(id, customerID, flightNum);
	}
	
	/* reserve an itinerary */
    public boolean itinerary(int id,int customer,Vector flightNumbers,String location,boolean Car,boolean Room)
	throws RemoteException {

		Trace.info("MW::itinerary(" + id + ", " + customer + ", " + location + ", " + Car + ", " + Room + ") called" );

     boolean status = true;

     for(int i=0;i<flightNumbers.size();i++)
        status = status && flt_rm.reserveFlight(id, customer,Integer.parseInt((String)flightNumbers.get(i)));

     if(Car)
         status = status && car_rm.reserveCar(id,customer, location);
     if(Room)
         status = status && htl_rm.reserveRoom(id,customer,location);
    	
     return status;
    }

}
