package jango;

public class JangoNotMasterException extends java.lang.Exception
{
	protected String s;

  public JangoNotMasterException()
	{ super (); s = "JangoNotMasterException:: This Remote Server is not functioning as a master."; }

  public JangoNotMasterException(String s)
	{ super (); this.s = s; }

	public String toString() { return s; }
}
