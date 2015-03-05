package ResInterface;


public class ResourceManager_Skel implements charon.CharonROSkel
{


java.io.ObjectInputStream ois;
java.io.ObjectOutputStream oos;
boolean socketOpen;
ResourceManager obj;

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

public void charon_execMethod(int method) throws charon.CharonException
{
try{
switch((int)method) { 
case CHARON_MTHD_NUM_ADDFLIGHT_0:addFlight_0();break;
case CHARON_MTHD_NUM_ADDCARS_1:addCars_1();break;
case CHARON_MTHD_NUM_ADDROOMS_2:addRooms_2();break;
case CHARON_MTHD_NUM_NEWCUSTOMER_3:newCustomer_3();break;
case CHARON_MTHD_NUM_NEWCUSTOMER_4:newCustomer_4();break;
case CHARON_MTHD_NUM_DELETEFLIGHT_5:deleteFlight_5();break;
case CHARON_MTHD_NUM_DELETECARS_6:deleteCars_6();break;
case CHARON_MTHD_NUM_DELETEROOMS_7:deleteRooms_7();break;
case CHARON_MTHD_NUM_DELETECUSTOMER_8:deleteCustomer_8();break;
case CHARON_MTHD_NUM_QUERYFLIGHT_9:queryFlight_9();break;
case CHARON_MTHD_NUM_QUERYCARS_10:queryCars_10();break;
case CHARON_MTHD_NUM_QUERYROOMS_11:queryRooms_11();break;
case CHARON_MTHD_NUM_QUERYCUSTOMERINFO_12:queryCustomerInfo_12();break;
case CHARON_MTHD_NUM_QUERYFLIGHTPRICE_13:queryFlightPrice_13();break;
case CHARON_MTHD_NUM_QUERYCARSPRICE_14:queryCarsPrice_14();break;
case CHARON_MTHD_NUM_QUERYROOMSPRICE_15:queryRoomsPrice_15();break;
case CHARON_MTHD_NUM_RESERVEFLIGHT_16:reserveFlight_16();break;
case CHARON_MTHD_NUM_RESERVECAR_17:reserveCar_17();break;
case CHARON_MTHD_NUM_RESERVEROOM_18:reserveRoom_18();break;
case CHARON_MTHD_NUM_ITINERARY_19:itinerary_19();break;
}

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method. unable to establish communication streams.", e); }
}

public void charon_setCommunicationSocket(java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws charon.CharonException
{
try{
this.oos = oos;
this.ois = ois;
socketOpen = true;

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method. unable to establish communication streams.", e); }
}

public void charon_setObject(java.lang.Object obj) throws charon.CharonException
{
try{
this.obj = (ResourceManager)obj;
}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method. unable to set the target Object.", e); }
}

public Object charon_getTargetObject()
{
return obj;}

public  void  addFlight_0() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
int arg_2 = ois.readInt();
int arg_3 = ois.readInt();
boolean __ret_arg = obj.addFlight(arg_0,arg_1,arg_2,arg_3);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  addCars_1() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int arg_2 = ois.readInt();
int arg_3 = ois.readInt();
boolean __ret_arg = obj.addCars(arg_0,arg_1,arg_2,arg_3);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  addRooms_2() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int arg_2 = ois.readInt();
int arg_3 = ois.readInt();
boolean __ret_arg = obj.addRooms(arg_0,arg_1,arg_2,arg_3);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  newCustomer_3() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
boolean __ret_arg = obj.newCustomer(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  newCustomer_4() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int __ret_arg = obj.newCustomer(arg_0);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  deleteFlight_5() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
boolean __ret_arg = obj.deleteFlight(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  deleteCars_6() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
boolean __ret_arg = obj.deleteCars(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  deleteRooms_7() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
boolean __ret_arg = obj.deleteRooms(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  deleteCustomer_8() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
boolean __ret_arg = obj.deleteCustomer(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryFlight_9() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
int __ret_arg = obj.queryFlight(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryCars_10() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int __ret_arg = obj.queryCars(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryRooms_11() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int __ret_arg = obj.queryRooms(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryCustomerInfo_12() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
java.lang.String __ret_arg = obj.queryCustomerInfo(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeObject(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryFlightPrice_13() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
int __ret_arg = obj.queryFlightPrice(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryCarsPrice_14() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int __ret_arg = obj.queryCarsPrice(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  queryRoomsPrice_15() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
java.lang.String arg_1 = (java.lang.String)ois.readObject();
int __ret_arg = obj.queryRoomsPrice(arg_0,arg_1);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeInt(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  reserveFlight_16() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
int arg_2 = ois.readInt();
boolean __ret_arg = obj.reserveFlight(arg_0,arg_1,arg_2);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  reserveCar_17() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
java.lang.String arg_2 = (java.lang.String)ois.readObject();
boolean __ret_arg = obj.reserveCar(arg_0,arg_1,arg_2);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  reserveRoom_18() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
java.lang.String arg_2 = (java.lang.String)ois.readObject();
boolean __ret_arg = obj.reserveRoom(arg_0,arg_1,arg_2);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public  void  itinerary_19() throws charon.CharonException
{
try{
int arg_0 = ois.readInt();
int arg_1 = ois.readInt();
java.util.Vector arg_2 = (java.util.Vector)ois.readObject();
java.lang.String arg_3 = (java.lang.String)ois.readObject();
boolean arg_4 = ois.readBoolean();
boolean arg_5 = ois.readBoolean();
boolean __ret_arg = obj.itinerary(arg_0,arg_1,arg_2,arg_3,arg_4,arg_5);
oos.writeInt(CHARON_MTHD_EXEC_OK);
oos.writeBoolean(__ret_arg);
oos.flush();

}
catch(Exception e){ throw new charon.CharonException("An unexpected error occured in proxy class method.", e); }

}

public void charon_close() throws charon.CharonException
{
try{
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
