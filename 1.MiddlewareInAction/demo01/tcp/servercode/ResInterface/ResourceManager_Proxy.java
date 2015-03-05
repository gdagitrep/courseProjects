package ResInterface;


public class ResourceManager_Proxy implements ResourceManager, charon.CharonROProxy
{


java.io.ObjectInputStream ois;
java.io.ObjectOutputStream oos;
java.net.Socket sck; boolean socketOpen;

private final static int CHARON_MTHD_EXEC_OK = 0;
private final static int CHARON_MTHD_EXEC_ERR = 1;

private final static int CHARON_MTHD_NUM_ADDFLIGHT_0 = 10000;
private final static int CHARON_MTHD_NUM_ADDCARS_1 = 10001;
private final static int CHARON_MTHD_NUM_ADDROOMS_2 = 10002;
private final static int CHARON_MTHD_NUM_NEWCUSTOMER_3 = 10003;
private final static int CHARON_MTHD_NUM_NEWCUSTOMER_4 = 10004;
private final static int CHARON_MTHD_NUM_DELETEFLIGHT_5 = 10005;
private final static int CHARON_MTHD_NUM_DELETECARS_6 = 10006;
private final static int CHARON_MTHD_NUM_DELETEROOMS_7 = 10007;
private final static int CHARON_MTHD_NUM_DELETECUSTOMER_8 = 10008;
private final static int CHARON_MTHD_NUM_QUERYFLIGHT_9 = 10009;
private final static int CHARON_MTHD_NUM_QUERYCARS_10 = 10010;
private final static int CHARON_MTHD_NUM_QUERYROOMS_11 = 10011;
private final static int CHARON_MTHD_NUM_QUERYCUSTOMERINFO_12 = 10012;
private final static int CHARON_MTHD_NUM_QUERYFLIGHTPRICE_13 = 10013;
private final static int CHARON_MTHD_NUM_QUERYCARSPRICE_14 = 10014;
private final static int CHARON_MTHD_NUM_QUERYROOMSPRICE_15 = 10015;
private final static int CHARON_MTHD_NUM_RESERVEFLIGHT_16 = 10016;
private final static int CHARON_MTHD_NUM_RESERVECAR_17 = 10017;
private final static int CHARON_MTHD_NUM_RESERVEROOM_18 = 10018;
private final static int CHARON_MTHD_NUM_ITINERARY_19 = 10019;

public String charon_getRMOInterfaceName()
{
return "package ResInterface.ResourceManager";}

public void charon_setCommunicationSocket(java.net.Socket sck, java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws charon.CharonException
{
try{
this.sck = sck;
this.oos = oos;
this.ois = ois;
socketOpen = true;

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method. unable to establish communication streams.", e); }
}

public boolean addFlight(int arg_0,int arg_1,int arg_2,int arg_3) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_ADDFLIGHT_0);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.writeInt(arg_2);
oos.writeInt(arg_3);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean addCars(int arg_0,java.lang.String arg_1,int arg_2,int arg_3) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_ADDCARS_1);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.writeInt(arg_2);
oos.writeInt(arg_3);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean addRooms(int arg_0,java.lang.String arg_1,int arg_2,int arg_3) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_ADDROOMS_2);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.writeInt(arg_2);
oos.writeInt(arg_3);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean newCustomer(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_NEWCUSTOMER_3);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int newCustomer(int arg_0) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_NEWCUSTOMER_4);
oos.writeInt(arg_0);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean deleteFlight(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_DELETEFLIGHT_5);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean deleteCars(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_DELETECARS_6);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean deleteRooms(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_DELETEROOMS_7);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean deleteCustomer(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_DELETECUSTOMER_8);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryFlight(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYFLIGHT_9);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryCars(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYCARS_10);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryRooms(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYROOMS_11);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public java.lang.String queryCustomerInfo(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYCUSTOMERINFO_12);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return (java.lang.String)ois.readObject();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryFlightPrice(int arg_0,int arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYFLIGHTPRICE_13);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryCarsPrice(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYCARSPRICE_14);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public int queryRoomsPrice(int arg_0,java.lang.String arg_1) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_QUERYROOMSPRICE_15);
oos.writeInt(arg_0);
oos.writeObject(arg_1);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readInt();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean reserveFlight(int arg_0,int arg_1,int arg_2) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_RESERVEFLIGHT_16);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.writeInt(arg_2);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean reserveCar(int arg_0,int arg_1,java.lang.String arg_2) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_RESERVECAR_17);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.writeObject(arg_2);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean reserveRoom(int arg_0,int arg_1,java.lang.String arg_2) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_RESERVEROOM_18);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.writeObject(arg_2);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public boolean itinerary(int arg_0,int arg_1,java.util.Vector arg_2,java.lang.String arg_3,boolean arg_4,boolean arg_5) throws charon.CharonException
{
try{
synchronized(this){
oos.writeInt(CHARON_MTHD_NUM_ITINERARY_19);
oos.writeInt(arg_0);
oos.writeInt(arg_1);
oos.writeObject(arg_2);
oos.writeObject(arg_3);
oos.writeBoolean(arg_4);
oos.writeBoolean(arg_5);
oos.flush();
int __status = ois.readInt();
if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException("Error Remote method returned error during execution.");
return ois.readBoolean();
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public void charon_close() throws charon.CharonException
{
try{
sck.close();
socketOpen = false;

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method. unable to perform cleanup as part of closing.", e); }
}

protected void finalize()
{
try{
if(socketOpen) charon_close();

}
catch(Exception e){ e.printStackTrace(); }
}

}
