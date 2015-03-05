package MWInterface;

import MWInterface.MWLayer;
import jango.JangoRepStub;


public class MWLayer_RepStub extends JangoRepStub<MWLayer> implements MWLayer
{
	public MWLayer_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[])
	{ super(rmiHosts, srvrNames, rmiPorts, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

	public MWLayer_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize)
	{ super(rmiHosts, srvrNames, rmiPorts, maxHashBucketSize, -1); }

	public MWLayer_RepStub(String rmiHosts[], String srvrNames[], int rmiPorts[], int maxHashBucketSize, int maxRetries)
	{ super(rmiHosts, srvrNames, rmiPorts, maxHashBucketSize, maxRetries); }

	public MWLayer_RepStub(int numReplics, String replicConfigs)
	{ super(numReplics, replicConfigs, JANGO_MAX_HASH_BUCKET_SIZE, -1); }

	public MWLayer_RepStub(int numReplics, String replicConfigs, int maxHashBucketSize)
	{ super(numReplics, replicConfigs, maxHashBucketSize, -1); }

	public MWLayer_RepStub(int numReplics, String replicConfigs, int maxHashBucketSize, int maxRetries)
	{ super(numReplics, replicConfigs, maxHashBucketSize, maxRetries); }

	public boolean addFlight(int arg_0,int arg_1,int arg_2,int arg_3,int arg_4) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).addFlight( arg_0, arg_1, arg_2, arg_3, arg_4);  }
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

	public boolean addCars(int arg_0,int arg_1,java.lang.String arg_2,int arg_3,int arg_4) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).addCars( arg_0, arg_1, arg_2, arg_3, arg_4);  }
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

	public boolean addRooms(int arg_0,int arg_1,java.lang.String arg_2,int arg_3,int arg_4) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).addRooms( arg_0, arg_1, arg_2, arg_3, arg_4);  }
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

	public boolean newCustomer(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).newCustomer( arg_0, arg_1, arg_2);  }
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

	public int newCustomer(int arg_0,int arg_1) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).newCustomer( arg_0, arg_1);  }
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

	public boolean deleteFlight(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).deleteFlight( arg_0, arg_1, arg_2);  }
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

	public boolean deleteCars(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).deleteCars( arg_0, arg_1, arg_2);  }
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

	public boolean deleteRooms(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).deleteRooms( arg_0, arg_1, arg_2);  }
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

	public boolean deleteCustomer(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).deleteCustomer( arg_0, arg_1, arg_2);  }
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

	public int queryFlight(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryFlight( arg_0, arg_1, arg_2);  }
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

	public int queryCars(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryCars( arg_0, arg_1, arg_2);  }
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

	public int queryRooms(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryRooms( arg_0, arg_1, arg_2);  }
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

	public java.lang.String queryCustomerInfo(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryCustomerInfo( arg_0, arg_1, arg_2);  }
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

	public int queryFlightPrice(int arg_0,int arg_1,int arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryFlightPrice( arg_0, arg_1, arg_2);  }
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

	public int queryCarsPrice(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryCarsPrice( arg_0, arg_1, arg_2);  }
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

	public int queryRoomsPrice(int arg_0,int arg_1,java.lang.String arg_2) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).queryRoomsPrice( arg_0, arg_1, arg_2);  }
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

	public boolean reserveFlight(int arg_0,int arg_1,int arg_2,int arg_3) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).reserveFlight( arg_0, arg_1, arg_2, arg_3);  }
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

	public boolean reserveCar(int arg_0,int arg_1,int arg_2,java.lang.String arg_3) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).reserveCar( arg_0, arg_1, arg_2, arg_3);  }
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

	public boolean reserveRoom(int arg_0,int arg_1,int arg_2,java.lang.String arg_3) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).reserveRoom( arg_0, arg_1, arg_2, arg_3);  }
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

	public boolean itinerary(int arg_0,int arg_1,int arg_2,java.util.Vector arg_3,java.lang.String arg_4,boolean arg_5,boolean arg_6) throws java.rmi.RemoteException, TxSystem.InvalidTransactionException, TxSystem.TransactionAbortedException, TxSystem.DeadlockException
	{
		int retries=0;
		int srvrIdx = hash(arg_0);
		java.rmi.RemoteException eR;
		do
		{
			try{ return getSrvr(srvrIdx).itinerary( arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6);  }
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