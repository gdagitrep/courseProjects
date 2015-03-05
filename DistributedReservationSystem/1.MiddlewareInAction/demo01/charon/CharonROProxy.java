package charon;

public interface CharonROProxy
{
  public void charon_setCommunicationSocket(java.net.Socket sck, java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws CharonException;

	public void charon_close() throws CharonException;
}
