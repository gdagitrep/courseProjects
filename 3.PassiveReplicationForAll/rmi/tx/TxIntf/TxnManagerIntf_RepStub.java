package TxIntf;

import TxIntf.TxnManagerIntf;
import jango.JangoRepStub;


public class TxnManagerIntf_RepStub extends JangoRepStub<TxnManagerIntf> implements TxnManagerIntf
{
	public TxnManagerIntf_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[])
	{ super(rmiHosts, srvrNames, rmiPorts, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

	public TxnManagerIntf_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize)
	{ super(rmiHosts, srvrNames, rmiPorts, maxHashBucketSize, -1); }

	public TxnManagerIntf_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize, int maxRetries)
	{ super(rmiHosts, srvrNames, rmiPorts, maxHashBucketSize, maxRetries); }

	public TxnManagerIntf_RepStub(int numReplics, String replicConfigs)
	{ super(numReplics, replicConfigs, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

	public TxnManagerIntf_RepStub(int numReplics, String replicConfigs, int maxHashBucketSize)
	{ super(numReplics, replicConfigs, maxHashBucketSize, -1); }

	public TxnManagerIntf_RepStub(int numReplics, String replicConfigs, int maxHashBucketSize, int maxRetries)
	{ super(numReplics, replicConfigs, maxHashBucketSize, maxRetries); }

	public void registerRM(TxSystem.TxnListnerIntf arg_0,java.lang.String arg_1) throws java.rmi.RemoteException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).registerRM( arg_0, arg_1);  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public void registerTxnListner(int arg_0,java.lang.String arg_1) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).registerTxnListner( arg_0, arg_1);  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public void abortTxn(int arg_0,TxSystem.TxnListnerIntf arg_1) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).abortTxn( arg_0, arg_1);  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public void commitTxn(int arg_0,TxSystem.TxnListnerIntf arg_1) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).commitTxn( arg_0, arg_1);  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public void keepTxnAlive(int arg_0) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).keepTxnAlive( arg_0);  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public int getTxnStaus(int arg_0) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).getTxnStaus( arg_0);  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public int startTxn() throws java.rmi.RemoteException
	{
		int retries=0;
		int srvrIdx = hash();
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).startTxn();  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public boolean lockObject(int arg_0,java.lang.String arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).lockObject( arg_0, arg_1, arg_2);  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public boolean unlockAll(int arg_0) throws TxSystem.InvalidTransactionException, java.rmi.RemoteException, TxSystem.TransactionAbortedException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).unlockAll( arg_0);  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}

	public void shutdown() throws java.rmi.RemoteException
	{
		int retries=0;
		int srvrIdx = hash();
		java.rmi.RemoteException eR;
		do
		{
			try{ getSrvr(srvrIdx).shutdown();  return;  }
			catch(java.rmi.RemoteException e)
			{
				System.out.println("Exception:" + e);
				eR = e;
				retries++;
				if(retries < maxRetries)
					fixHashBucket(srvrIdx);
			}
		}while(retries < maxRetries);

		throw eR;
	}


}