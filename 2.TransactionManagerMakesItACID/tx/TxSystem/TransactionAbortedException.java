package TxSystem;

public class TransactionAbortedException extends Exception
{
  static final int TXN_ACTIVE = 1;
  static final int TXN_ABORTED = 2;
  static final int TXN_INVALID = 3;
  static final int TXN_COMMITED = 4;

	int txId;
	String msg;

  public TransactionAbortedException(int txId, String str)
	{
	  this.txId = txId;
		msg = "Transaction " + txId;

		if(str != null) msg += str;
	}

	public String toString() { return msg; }
}
