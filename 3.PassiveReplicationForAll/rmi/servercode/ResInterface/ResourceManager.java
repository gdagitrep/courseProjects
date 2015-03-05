package ResInterface;


import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

import TxSystem.*;
import TxIntf.*;
/** 
 * Simplified version from CSE 593 Univ. of Washington
 *
 * Distributed  System in Java.
 * 
 * failure reporting is done using two pieces, exceptions and boolean 
 * return values.  Exceptions are used for systemy things. Return
 * values are used for operations that would affect the consistency
 * 
 * If there is a boolean return value and you're not sure how it 
 * would be used in your implementation, ignore it.  I used boolean
 * return values in the interface generously to allow flexibility in 
 * implementation.  But don't forget to return true when the operation
 * has succeeded.
 */

public interface ResourceManager extends Remote, TxnListnerIntf
{
    /* Add seats to a flight.  In general this will be used to create a new
     * flight, but it should be possible to add seats to an existing flight.
     * Adding to an existing flight should overwrite the current price of the
     * available seats.
     *
     * @return success.
     */
    public boolean addFlight(int txId, int rqId, int flightNum, int flightSeats, int flightPrice) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    
    /* Add cars to a location.  
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     */
    public boolean addCars(int txId, int rqId, String location, int numCars, int price) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
   
    /* Add rooms to a location.  
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     */
    public boolean addRooms(int txId, int rqId, String location, int numRooms, int price) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 			    

			    
    /* new customer just returns a unique customer identifier */
    public int newCustomer(int txId, int rqId) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    
    /* new customer with providing id */
    public boolean newCustomer(int txId, int rqId, int cid)
    throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException;

    /**
     *   Delete the entire flight.
     *   deleteflight implies whole deletion of the flight.  
     *   all seats, all reservations.  If there is a reservation on the flight, 
     *   then the flight cannot be deleted
     *
     * @return success.
     */   
    public boolean deleteFlight(int txId, int rqId, int flightNum) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    
    /* Delete all Cars from a location.
     * It may not succeed if there are reservations for this location
     *
     * @return success
     */		    
    public boolean deleteCars(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* Delete all Rooms from a location.
     * It may not succeed if there are reservations for this location.
     *
     * @return success
     */
    public boolean deleteRooms(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    
    /* deleteCustomer removes the customer and associated reservations */
    public boolean deleteCustomer(int txId, int rqId,int customer) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* queryFlight returns the number of empty seats. */
    public int queryFlight(int txId, int rqId, int flightNumber) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* return the number of cars available at a location */
    public int queryCars(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* return the number of rooms available at a location */
    public int queryRooms(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* return a bill */
    public String queryCustomerInfo(int txId, int rqId,int customer) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    
    /* queryFlightPrice returns the price of a seat on this flight. */
    public int queryFlightPrice(int txId, int rqId, int flightNumber) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* return the price of a car at a location */
    public int queryCarsPrice(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* return the price of a room at a location */
    public int queryRoomsPrice(int txId, int rqId, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* Reserve a seat on this flight*/
    public boolean reserveFlight(int txId, int rqId, int customer, int flightNumber) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    public boolean reserveFlight(int txId, int rqId, int customer, int flightNumber, boolean checkOnly) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* reserve a car at this location */
    public boolean reserveCar(int txId, int rqId, int customer, String location) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    public boolean reserveCar(int txId, int rqId, int customer, String location, boolean checkOnly) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 

    /* reserve a room certain at this location */
    public boolean reserveRoom(int txId, int rqId, int customer, String locationd) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    public boolean reserveRoom(int txId, int rqId, int customer, String locationd, boolean checkOnly) 
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 


    /* reserve an itinerary */
    public boolean itinerary(int txId, int rqId,int customer,Vector flightNumbers,String location, boolean Car, boolean Room)
	throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException; 
    			
}
