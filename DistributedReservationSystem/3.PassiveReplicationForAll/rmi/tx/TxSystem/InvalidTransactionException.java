package TxSystem;

public class InvalidTransactionException extends Exception
{
  static final int TXN_ACTIVE = 1;
  static final int TXN_ABORTED = 2;
  static final int TXN_INVALID = 3;
  static final int TXN_COMMITED = 4;

	int txId;
	String msg;

  public InvalidTransactionException(int txId, int txStatus, String str)
	{
	  this.txId = txId;
		msg = "Transaction " + txId;

		switch(txStatus)
		{
		  case TXN_ACTIVE:
				msg += " Active ?? "; // Why would anyone use this ?
				break;

		  case TXN_ABORTED:
				msg += " Aborted ";
				break;

		  case TXN_INVALID:
				msg += " Invalid ";
				break;

		  case TXN_COMMITED:
				msg += " Already Commited "; // Why would anyone use this ?
				break;

		}

		if(str != null) msg += str;
	}

	public String toString() { return msg; }
}
