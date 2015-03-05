package ResInterface;

import jango.*;

import java.util.Hashtable;
import java.rmi.RemoteException;

public interface ResourceManagerRep extends JangoRemoteServer, java.rmi.Remote
{
	public Hashtable transferDB(String host, int port, String repName) throws java.rmi.RemoteException;

	public void reserveItem_Rep(int id, int rqId, int customerID, String key    , String location, boolean checkOnly, ResponseObject masterResp) throws RemoteException;

	public void addFlight_Rep(int id, int rqId, int flightNum, int flightSeats,int flightPrice, ResponseObject masterResp) throws RemoteException;

	public void addRooms_Rep(int id, int rqId, String location, int count,int price, ResponseObject masterResp) throws RemoteException;

	public void addCars_Rep(int id, int rqId, String location, int count,int price, ResponseObject masterResp) throws RemoteException;

	public void newCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp) throws RemoteException;

	public void deleteItem_Rep(int id, int rqId, String key, ResponseObject masterResp) throws RemoteException;

	public void deleteCustomer_Rep(int id, int rqId, int customerID, ResponseObject masterResp) throws RemoteException;

}
