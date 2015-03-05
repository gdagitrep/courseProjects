package jango;

public interface JangoRemoteServer extends java.rmi.Remote
{
	public final static int JANGO_SERVER_UNA = 0;

	public final static int JANGO_SERVER_MASTER = 1;

	public final static int JANGO_SERVER_PASSIVE = 2;

  public int getServerStatus() throws java.rmi.RemoteException;

	public boolean switchToMaster() throws java.rmi.RemoteException;

	public void registerReplic(String host, int port, String objName) throws java.rmi.RemoteException;

	public String setCrashPoint(String point, boolean status) throws java.rmi.RemoteException;

	public java.util.Hashtable<String, Boolean> getCrashPoints() throws java.rmi.RemoteException;
}
