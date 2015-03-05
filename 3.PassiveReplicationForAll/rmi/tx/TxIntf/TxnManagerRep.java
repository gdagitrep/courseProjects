package TxIntf;

import TxSystem.*;
import jango.*;

import java.util.Hashtable;
import java.rmi.RemoteException;

public interface TxnManagerRep extends JangoRemoteServer, java.rmi.Remote
{
	public Hashtable transferTxnData(String host, int port, String repName) throws java.rmi.RemoteException;

	public void registerTxnListner_Rep(int txId, String RMType) throws RemoteException;

	public void registerRM_Rep(TxnListnerIntf rm, String RMType) throws RemoteException;

	public void abortTxn_Rep(int txId, TxnListnerIntf txnListner) throws RemoteException;

	public void commitTxn_Rep(int txId, TxnListnerIntf txnListner) throws RemoteException;

	public void startTxn_Rep(int lastTxn) throws RemoteException;

	public void lockObject_Rep(int txId, String strData, int lockType) throws RemoteException;
}
