package G13Components;

import java.security.Permission;
import java.rmi.RMISecurityManager;

public class DSRMISecurityManager extends RMISecurityManager
{
	public DSRMISecurityManager() { super(); }

	public void checkAccept(String host, int port) { }

	public void checkAccess(Thread t)  { }

	public void checkAccess(ThreadGroup g) { }

	public void checkConnect(String host, int port) { }

	public void checkConnect(String host, int port, Object context) { }

	public void checkListen(int port) { }

	public void checkCreateClassLoader() { }

	public void checkPermission(Permission perm) { }

	public void checkPermission(Permission perm, Object context) { }

}
