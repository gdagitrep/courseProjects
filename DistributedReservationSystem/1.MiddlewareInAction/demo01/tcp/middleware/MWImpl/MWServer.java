// -------------------------------
// adapated from Kevin T. Manley
// CSE 593
//
package MWImpl;

import ResInterface.*;

import MWInterface.*;

import G13Log.*;
import G13Components.*;

import charon.*;

import java.util.*;


//public class MWLayer extends java.rmi.server.UnicastRemoteObject
public class MWServer
	implements MWLayer {

	static ResourceManager htl_rm = null;
	static ResourceManager car_rm = null;
	static ResourceManager flt_rm = null;
	
	//-- protected RMHashtable m_itemHT = new RMHashtable();


	public String charon_getRMOInterfaceName()
	{  return "MWInterface.MWLayer"; }


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

		 htl_rm = (ResourceManager) CharonROManager.getRemoteObjectReference(System.getenv("HTL_SRVR_RO_REGISTRY_HOST"), Integer.parseInt(System.getenv("HTL_SRVR_RO_PORT")), System.getenv("HTL_SRVR_RO_OBJ_NAME"));
		 car_rm = (ResourceManager) CharonROManager.getRemoteObjectReference(System.getenv("CAR_SRVR_RO_REGISTRY_HOST"), Integer.parseInt(System.getenv("CAR_SRVR_RO_PORT")), System.getenv("CAR_SRVR_RO_OBJ_NAME"));
		 flt_rm = (ResourceManager) CharonROManager.getRemoteObjectReference(System.getenv("FLT_SRVR_RO_REGISTRY_HOST"), Integer.parseInt(System.getenv("FLT_SRVR_RO_PORT")), System.getenv("FLT_SRVR_RO_OBJ_NAME"));
		 
		 CharonROManager crom = new CharonROManager(Integer.parseInt(System.getenv("MIDW_RO_PORT")));
		 
		 // create a new Server object
		 MWLayer obj = new MWServer();
		 crom.registerObject(System.getenv("MIDW_RO_OBJ_NAME"), obj);

		 System.err.println("Server ready");

		 crom.waitOnManager();
	     } 
	 catch (Exception e) 
	     {
		 System.err.println("Server exception: " + e.toString());
		 e.printStackTrace();
	     }
	 
	 }
	 
	 public MWServer() throws CharonException {
	 }
	 

	// Create a new flight, or add seats to existing flight
	//  NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice)
		throws CharonException
	{
		Trace.info("MW::addFlight(" + id + ", " + flightNum + ", $" + flightPrice + ", " + flightSeats + ") called" );
		return flt_rm.addFlight(id,flightNum,flightSeats,flightPrice);
	}


	
	public boolean deleteFlight(int id, int flightNum)
		throws CharonException
	{
		Trace.info("MW::deleteFlight(" + id + ", " + flightNum + ") called" );
		return flt_rm.deleteFlight(id, flightNum);
	}



	// Create a new room location or add rooms to an existing location
	//  NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int id, String location, int count, int price)
		throws CharonException
	{
		Trace.info("MW::addRooms(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		return htl_rm.addRooms(id,location,count,price);
	}

	// Delete rooms from a location
	public boolean deleteRooms(int id, String location)
		throws CharonException
	{
		Trace.info("MW::deleteRooms(" + id + ", " + location + ") called" );
		return htl_rm.deleteRooms(id, location);
	}

	// Create a new car location or add cars to an existing location
	//  NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int id, String location, int count, int price)
		throws CharonException
	{
		Trace.info("MW::addCars(" + id + ", " + location + ", " + count + ", $" + price + ") called" );
		return car_rm.addCars(id, location, count, price);
	}


	// Delete cars from a location
	public boolean deleteCars(int id, String location)
		throws CharonException
	{
		Trace.info("MW::deleteCars(" + id + ", " + location + ") called" );
		return car_rm.deleteCars(id, location);
	}



	// Returns the number of empty seats on this flight
	public int queryFlight(int id, int flightNum)
		throws CharonException
	{
		Trace.info("MW::queryFlight(" + id + ", " + flightNum + ") called" );
		return flt_rm.queryFlight(id, flightNum);
	}

	// Returns the number of reservations for this flight. 
//	public int queryFlightReservations(int id, int flightNum)
//		throws CharonException
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
		throws CharonException
	{
		Trace.info("MW::queryFlightPrice(" + id + ", " + flightNum + ") called" );
		return flt_rm.queryFlightPrice(id, flightNum);
	}


	// Returns the number of rooms available at a location
	public int queryRooms(int id, String location)
		throws CharonException
	{
		Trace.info("MW::queryRooms(" + id + ", " + location + ") called" );
		return htl_rm.queryRooms(id,location);
	}


	
	
	// Returns room price at this location
	public int queryRoomsPrice(int id, String location)
		throws CharonException
	{
		Trace.info("MW::queryRoomsPrice(" + id + ", " + location + ") called" );
		return htl_rm.queryRoomsPrice(id, location);
	}


	// Returns the number of cars available at a location
	public int queryCars(int id, String location)
		throws CharonException
	{
		Trace.info("MW::queryCars(" + id + ", " + location + ") called" );
		return car_rm.queryCars(id, location);
	}


	// Returns price of cars at this location
	public int queryCarsPrice(int id, String location)
		throws CharonException
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
		throws CharonException
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
		throws CharonException
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
		throws CharonException
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
		throws CharonException
	{
		Trace.info("INFO: MW::newCustomer(" + id + ", " + customerID + ") called" );

		return htl_rm.newCustomer(id, customerID) && car_rm.newCustomer(id, customerID) && flt_rm.newCustomer(id, customerID);
	}


	// Deletes customer from the database. 
	public boolean deleteCustomer(int id, int customerID)
			throws CharonException
	{
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called" );

		return htl_rm.deleteCustomer(id, customerID) && car_rm.deleteCustomer(id, customerID) && flt_rm.deleteCustomer(id, customerID) ;
	}




	// Frees flight reservation record. Flight reservation records help us make sure we
	//  don't delete a flight if one or more customers are holding reservations
//	public boolean freeFlightReservation(int id, int flightNum)
//		throws CharonException
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
		throws CharonException
	{
		Trace.info("MW::reserveCar(" + id + ", " + customerID + ", " + location + ") called" );
		return car_rm.reserveCar(id, customerID, location);
	}


	// Adds room reservation to this customer. 
	public boolean reserveRoom(int id, int customerID, String location)
		throws CharonException
	{
		Trace.info("MW::reserveRoom(" + id + ", " + customerID + ", " + location + ") called" );
		return htl_rm.reserveRoom(id, customerID, location);
	}
	// Adds flight reservation to this customer.  
	public boolean reserveFlight(int id, int customerID, int flightNum)
		throws CharonException
	{
		Trace.info("MW::reserveFlight(" + id + ", " + customerID + ", " + flightNum + ") called" );
		return flt_rm.reserveFlight(id, customerID, flightNum);
	}
	
	/* reserve an itinerary */
    public boolean itinerary(int id,int customer,Vector flightNumbers,String location,boolean Car,boolean Room)
	throws CharonException {

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
