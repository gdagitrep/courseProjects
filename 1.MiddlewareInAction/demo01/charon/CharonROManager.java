package charon;

import java.io.*;
import java.net.*;
import java.util.*;

public class CharonROManager implements Runnable
{
	private  HashMap<String,CharonRObject> objList = new java.util.HashMap<String,CharonRObject>();
	private  HashMap<String,Vector<CharonObjectServerThread>> clientList = new java.util.HashMap<String,Vector<CharonObjectServerThread>>();
	private ServerSocket ssck;

	private Thread managerThread;

  private class CharonObjectServerThread extends Thread
	{
		String objectName;
		CharonROSkel rmo_skel;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		Socket sck;

	  public CharonObjectServerThread(String objectName, CharonROSkel rmo_skel, ObjectOutputStream oos, ObjectInputStream ois, Socket sck)
		{
			this.objectName = objectName;
			this.rmo_skel = rmo_skel;
			this.oos = oos;
			this.ois = ois;
			this.sck = sck;
		}

		public void run()
		{
			int method;
		  while(true)
			{

				try
				{
			    method = ois.readInt();
				  rmo_skel.charon_execMethod(method);
				}
				catch(java.io.EOFException e)
				{
					// This happens when the client closes the socket and walks off
					// May be we can do something more here, to remove ourselves
					// from the client list ??
					// TODO
					//e.printStackTrace();
					removeFromClientList(this, objectName);
				  return;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				  return;
				}
			
			}
		}

		public void close()
		{
		  try{ sck.close();  }
			catch(Exception e) {}
		}

		protected void finalize()
		{
		  close();
		}

	}

	public CharonROManager(int managerPort) throws CharonException
	{
	  
		try
		{
		  ssck = new ServerSocket(managerPort);

			managerThread = new Thread(this);
			managerThread.start();
		}
		catch(Exception e)
		{  throw new CharonException("Error trying to establish listner socket for Manager.", e); }
	
	}

	public void run()
	{
	  while(true)
		{
			try
			{
		    Socket sck = ssck.accept();

				ObjectInputStream ois = new ObjectInputStream(sck.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(sck.getOutputStream());

				String objName = (String)ois.readObject();
				CharonRObject obj = objList.get(objName);

				if(obj == null)
				{
				  oos.writeObject("Error: cannot locate " + objName + " in manager  registry.");
					oos.flush();
					sck.close();
				}
				else
				{
		      CharonROSkel skelObj = (CharonROSkel) (Class.forName(obj.charon_getRMOInterfaceName()+"_Skel")).newInstance();
		      skelObj.charon_setObject(obj);

			    Vector<CharonObjectServerThread> clients = clientList.get(objName);
			    if(clients == null)
			    { 
			      clients = new Vector<CharonObjectServerThread>(); 
				    clientList.put(objName, clients);
			    } 
			    CharonObjectServerThread gost = new CharonObjectServerThread(objName, skelObj, oos, ois, sck);
					clients.add(gost);

					skelObj.charon_setCommunicationSocket(oos, ois);
				  oos.writeObject("OK");
				  oos.writeObject(obj.charon_getRMOInterfaceName()+"_Proxy");
					oos.flush();

			    //t.start();
			    gost.start();
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			  return;
			}
		}
	}

	public  void registerObject(String objectName, CharonRObject obj)
	{
	  objList.put(objectName, obj);
	}

	public void waitOnManager() throws CharonException
	{
	  try { managerThread.join(); }
		catch(InterruptedException e) { throw new CharonException("Error: Manager thread interrupted:", e); }
	}

	private void removeFromClientList(CharonObjectServerThread clnt, String objName)
	{
	  Vector<CharonObjectServerThread> clients = clientList.get(objName);
		if(clients != null)
			clients.remove(clnt);

		clnt.close();
	}

  public static Object getRemoteObjectReference(String remoteHost, int remotePort, String objectName) throws CharonException
	{
  	try
		{

		  Socket sck = new java.net.Socket(remoteHost, remotePort);
			ObjectOutputStream oos = new java.io.ObjectOutputStream(sck.getOutputStream());
			ObjectInputStream ois = new java.io.ObjectInputStream(sck.getInputStream());

			oos.writeObject(objectName);
			oos.flush();
			String resp = (String)ois.readObject();

			if(! resp.equals("OK"))
			  throw new CharonException("Err, unable to locate remote object. server response:" + resp);

			Class proxyClass = Class.forName((String)ois.readObject());
			CharonROProxy proxyObj = (CharonROProxy)proxyClass.newInstance();
			proxyObj.charon_setCommunicationSocket(sck, oos, ois);

			return proxyObj;
	
	  }
		catch(Exception e)
		{  throw new CharonException("Error while trying to connect to remote object.", e); }
	}
}

