package ResInterface;

public class ResponseObject implements java.io.Serializable
{
	int txId;
	int reqId;
	Object resp;

	public ResponseObject(int txId, int reqId, Object resp)
	{
		this.txId = txId;
		this.reqId = reqId;
		this.resp = resp;
	}

	public int getReqId() { return reqId; }

	public void setResponse(Object resp) { this.resp = resp; }

	public Object getResponse() { return resp; }

	public boolean equals(ResponseObject respObj)
	{ return this.txId == respObj.txId && this.resp == respObj.reqId; }

	public int hashCode() { return txId; }

}
