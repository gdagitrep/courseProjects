package charon;

public interface CharonROSkel
{
  public void charon_setCommunicationSocket(java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws CharonException;

  public void charon_setObject(java.lang.Object obj) throws CharonException;

  public Object charon_getTargetObject();

  public void charon_execMethod(int method) throws CharonException;

	public void charon_close() throws CharonException;
}

