package TxIntf;

import java.util.*;

import java.rmi.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import TxSystem.*;

public interface TxnManagerIntf extends Remote, TxnListnerIntf
{
  public void registerTxnListner(int txId, String  RMType) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

	public void registerRM(TxnListnerIntf rm, String RMType) throws RemoteException;

	public void abortTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

	public void commitTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException;

	public void keepTxnAlive(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

	public int getTxnStaus(int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;

	public int startTxn() throws RemoteException;

	public boolean lockObject(int txId, String strData, int lockType) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException;

	public boolean unlockAll(int txId) throws InvalidTransactionException, RemoteException, TransactionAbortedException;

	public void shutdown() throws RemoteException;
}