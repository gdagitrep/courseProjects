package TxSystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

public interface TxnListnerIntf
{
  public void abortTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException;
  public void commitTxn(int txId, TxnListnerIntf txnListner) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException;
	public void shutdown() throws RemoteException;
	public int startTxn() throws  RemoteException;
}
