package TxSystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

public interface TxnListnerIntf
{
  public void abortTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException;
  public void commitTxn(TxnListnerIntf txnListner, int txId) throws RemoteException, InvalidTransactionException, TransactionAbortedException, DeadlockException;
	public void shutdown() throws RemoteException;
	public int startTxn() throws  RemoteException;
}
