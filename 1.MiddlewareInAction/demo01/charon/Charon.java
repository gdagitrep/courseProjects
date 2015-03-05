package charon;

import java.lang.reflect.*;
import java.io.*;

public class Charon
{

  public static StringBuilder buildProxy(Class cls)
	{
			StringBuilder proxyClassStr = new StringBuilder(2000);

		  Method[] mthds = cls.getDeclaredMethods();

			// our proxy class package.
			proxyClassStr.append(cls.getPackage());
			proxyClassStr.append(";\n\n\n");
			proxyClassStr.append("public class ");

			String className = cls.getSimpleName();

			// generate the name for our proxy class.
			proxyClassStr.append(className + "_Proxy");
			proxyClassStr.append(" implements ");
			proxyClassStr.append(className);
			proxyClassStr.append(", charon.CharonROProxy");
			proxyClassStr.append("\n{\n");
			proxyClassStr.append("\n\njava.io.ObjectInputStream ois;\njava.io.ObjectOutputStream oos;\njava.net.Socket sck; boolean socketOpen;\n");

			//Define constants for internal use, one constant per function
			//This will be used to figure out which function is being called.
			// whether the execution was successfull, etc.
			proxyClassStr.append("\n");
			proxyClassStr.append("private final static int CHARON_MTHD_EXEC_OK = 0;\n");
			proxyClassStr.append("private final static int CHARON_MTHD_EXEC_ERR = 1;\n");
			proxyClassStr.append("\n");
			for(int l=0; l<mthds.length; l++)
			{
			  proxyClassStr.append("private final static int CHARON_MTHD_NUM_");
			  proxyClassStr.append(mthds[l].getName().toUpperCase());
			  proxyClassStr.append("_"+l);
			  proxyClassStr.append(" = " + (l+10000) + ";\n");
			}
			proxyClassStr.append("\n");

			// common to all proxy classes, interface function to get communication socket.
			proxyClassStr.append("public String charon_getRMOInterfaceName()");
			proxyClassStr.append("\n{\n");
			proxyClassStr.append("return \"" + cls.getPackage() + "." + cls.getSimpleName() + "\";");
			proxyClassStr.append("}\n\n");


			// common to all proxy classes, interface function to get communication socket.
			proxyClassStr.append("public void charon_setCommunicationSocket(java.net.Socket sck, java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws charon.CharonException");
			proxyClassStr.append("\n{\n");
			// catch everything in an exception block
			proxyClassStr.append("try{\n");
			proxyClassStr.append("this.sck = sck;\n");
			proxyClassStr.append("this.oos = oos;\n");
			proxyClassStr.append("this.ois = ois;\n");
			proxyClassStr.append("socketOpen = true;\n");
			// catch everything from exception block
			proxyClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to establish communication streams.\", e); }\n");

			proxyClassStr.append("}\n\n");

			//Generate proxy for each method defined in the interface.
			for(int i=0; i<mthds.length; i++)
			{
				if ( ((mthds[i].getModifiers()) & (Member.PUBLIC)) == Member.PUBLIC)
				  proxyClassStr.append("public");
			  else
				  proxyClassStr.append("      ");

				proxyClassStr.append(" ");

				// specify the original return types.
				String returnType = mthds[i].getReturnType().getName();
			  proxyClassStr.append(returnType);
				proxyClassStr.append(" ");

			  proxyClassStr.append(mthds[i].getName());
				proxyClassStr.append("(");

				// we will generate our own sequential argument names
				Class params[] = mthds[i].getParameterTypes();
				for(int j=0; j<params.length; j++)
				{
				  proxyClassStr.append(params[j].getName());
				  proxyClassStr.append(" arg_"); proxyClassStr.append(j);
				  if(j!=params.length-1) proxyClassStr.append(",");
				}

				proxyClassStr.append(")");

				// specify required exceptions.
				Class exceptions[] = mthds[i].getExceptionTypes();
        if(exceptions.length > 0) proxyClassStr.append(" throws ");
				for(int j=0; j<exceptions.length; j++)
				{
				  proxyClassStr.append(exceptions[j].getName());
				  if(j!=exceptions.length-1) System.out.print(",");
				}

				// body of proxy method.
				proxyClassStr.append("\n{\n");

				// catch everything in an exception block
				proxyClassStr.append("try{\n");
				// we need to synchronize all messages on ntwk so that Proxy class
				// is thread safe.
				proxyClassStr.append("synchronized(this){\n");

				// write out the method name of the calling method.
			  proxyClassStr.append("oos.writeInt(CHARON_MTHD_NUM_");
			  proxyClassStr.append(mthds[i].getName().toUpperCase());
			  proxyClassStr.append("_"+i);
			  proxyClassStr.append(");\n");


				// write out each argument passed on into the object output stream.
				for(int j=0; j<params.length; j++)
				{
					String type = params[j].getName();
					String argName = "arg_"+j;

					if(!params[j].isPrimitive())
					  proxyClassStr.append("oos.writeObject("+argName+");");
					else if(type.equals("int"))
					  proxyClassStr.append("oos.writeInt("+argName+");");
					else if(type.equals("boolean"))
					  proxyClassStr.append("oos.writeBoolean("+argName+");");
					else if(type.equals("byte"))
					  proxyClassStr.append("oos.writeByte("+argName+");");
					else if(type.equals("char"))
					  proxyClassStr.append("oos.writeChar("+argName+");");
					else if(type.equals("short"))
					  proxyClassStr.append("oos.writeShort("+argName+");");
					else if(type.equals("double"))
					  proxyClassStr.append("oos.writeDouble("+argName+");");
					else if(type.equals("long"))
					  proxyClassStr.append("oos.writeLong("+argName+");");
					else if(type.equals("float"))
					  proxyClassStr.append("oos.writeFloat("+argName+");");

					proxyClassStr.append("\n");
				}

				// send everything out.
			  proxyClassStr.append("oos.flush();\n");

				// read back the status first.
			  proxyClassStr.append("int __status = ois.readInt();\n");
			  proxyClassStr.append("if( __status !=  CHARON_MTHD_EXEC_OK) throw new charon.CharonException(\"Error Remote method returned error during execution.\");\n");

				
			  // if this function has a return type, fetch it and return it.
        if(! returnType.equals("void"))
				{

					proxyClassStr.append("return ");
				  if(! mthds[i].getReturnType().isPrimitive())
					   proxyClassStr.append("(" + returnType + ")" + "ois.readObject();");
					else if(returnType.equals("int"))
					  proxyClassStr.append("ois.readInt();");
					else if(returnType.equals("boolean"))
					  proxyClassStr.append("ois.readBoolean();");
					else if(returnType.equals("byte"))
					  proxyClassStr.append("ois.readByte();");
					else if(returnType.equals("char"))
					  proxyClassStr.append("ois.readChar();");
					else if(returnType.equals("short"))
					  proxyClassStr.append("ois.readShort();");
					else if(returnType.equals("double"))
					  proxyClassStr.append("ois.readDouble();");
					else if(returnType.equals("long"))
					  proxyClassStr.append("ois.readLong();");
					else if(returnType.equals("float"))
					  proxyClassStr.append("ois.readFloat();");
				  
				}

				// end of synchronization block
				proxyClassStr.append("\n}\n");
				// catch everything from exception block
				proxyClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method.\", e); }\n");

				// end of proxy method
				proxyClassStr.append("\n}\n");

				proxyClassStr.append("\n");
			}

			// close function common to all proxy objects.
			proxyClassStr.append("public void charon_close() throws charon.CharonException");
			proxyClassStr.append("\n{\n");
			// catch everything in an exception block
			proxyClassStr.append("try{\n");
			proxyClassStr.append("sck.close();\n");
			proxyClassStr.append("socketOpen = false;\n");
			proxyClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to perform cleanup as part of closing.\", e); }\n");
			proxyClassStr.append("}\n\n");

			// generic destructor
			proxyClassStr.append("protected void finalize()");
			proxyClassStr.append("\n{\n");
			// catch everything in an exception block
			proxyClassStr.append("try{\n");
			proxyClassStr.append("if(socketOpen) charon_close();\n");
			proxyClassStr.append("\n}\ncatch(Exception e){ e.printStackTrace(); }\n");
			proxyClassStr.append("}\n\n");


			// end of proxy class
			proxyClassStr.append("}\n");

			return proxyClassStr;
	}

  public static StringBuilder buildSkel(Class cls)
	{
			StringBuilder skelClassStr = new StringBuilder(2000);

		  Method[] mthds = cls.getDeclaredMethods();

			// our proxy class package.
			skelClassStr.append(cls.getPackage());
			skelClassStr.append(";\n\n\n");
			skelClassStr.append("public class ");

			String className = cls.getSimpleName();

			// generate the name for our proxy class.
			skelClassStr.append(className + "_Skel");
			skelClassStr.append(" implements ");
			//skelClassStr.append(className);
			skelClassStr.append("charon.CharonROSkel");
			skelClassStr.append("\n{\n");
			skelClassStr.append("\n\njava.io.ObjectInputStream ois;\njava.io.ObjectOutputStream oos;\nboolean socketOpen;\n");
			skelClassStr.append(className + " obj;\n");

			//Define constants for internal use, one constant per function
			//This will be used to figure out which function is being called.
			// whether the execution was successfull, etc.
			skelClassStr.append("\n");
			skelClassStr.append("private final static int CHARON_MTHD_EXEC_OK = 0;\n");
			skelClassStr.append("private final static int CHARON_MTHD_EXEC_ERR = 1;\n");
			skelClassStr.append("\n");
			for(int l=0; l<mthds.length; l++)
			{
			  skelClassStr.append("private final static int CHARON_MTHD_NUM_");
			  skelClassStr.append(mthds[l].getName().toUpperCase());
			  skelClassStr.append("_"+l);
			  skelClassStr.append(" = " + (l+10000L) + ";\n");
			}
			skelClassStr.append("\n");

			// common to all proxy classes, interface function to get communication socket.
			skelClassStr.append("public void charon_execMethod(int method) throws charon.CharonException");
			skelClassStr.append("\n{\n");
			// catch everything in an exception block
			skelClassStr.append("try{\n");

			// call required method depednding on the input argument.
			skelClassStr.append("switch((int)method) { \n");
			for(int l=0; l<mthds.length; l++)
			{
			  skelClassStr.append("case CHARON_MTHD_NUM_");
			  skelClassStr.append(mthds[l].getName().toUpperCase());
			  skelClassStr.append("_"+l);
			  skelClassStr.append(":");
			  skelClassStr.append(mthds[l].getName());
			  skelClassStr.append("_"+l);
			  skelClassStr.append("();break;\n");
			}
			skelClassStr.append("}\n"); // end of switch for methods.

			// catch everything from exception block
			skelClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to establish communication streams.\", e); }\n");
			skelClassStr.append("}\n\n");

			// common to all proxy classes, interface function to get communication socket.
			skelClassStr.append("public void charon_setCommunicationSocket(java.io.ObjectOutputStream oos, java.io.ObjectInputStream ois) throws charon.CharonException");
			skelClassStr.append("\n{\n");
			// catch everything in an exception block
			skelClassStr.append("try{\n");
			skelClassStr.append("this.oos = oos;\n");
			skelClassStr.append("this.ois = ois;\n");
			skelClassStr.append("socketOpen = true;\n");
			// catch everything from exception block
			skelClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to establish communication streams.\", e); }\n");
			skelClassStr.append("}\n\n");

			// common to all proxy classes, interface function to get set the actual target object involved.
			skelClassStr.append("public void charon_setObject(java.lang.Object obj) throws charon.CharonException");
			skelClassStr.append("\n{\n");
			// catch everything in an exception block
			skelClassStr.append("try{\n");
			skelClassStr.append("this.obj = (" + className + ")obj;");
			// catch everything from exception block
			skelClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to set the target Object.\", e); }\n");
			skelClassStr.append("}\n\n");

			// common to all proxy classes, interface function to get set the actual target object involved.
			skelClassStr.append("public Object charon_getTargetObject()");
			skelClassStr.append("\n{\n");
			skelClassStr.append("return obj;");
			skelClassStr.append("}\n\n");

			//Generate proxy for each method defined in the interface.
			for(int i=0; i<mthds.length; i++)
			{
				if ( ((mthds[i].getModifiers()) & (Member.PUBLIC)) == Member.PUBLIC)
				  skelClassStr.append("public");
			  else
				  skelClassStr.append("      ");

				skelClassStr.append(" ");
				// we are not returning anything from skelton classes via function returns.
				skelClassStr.append(" void ");
				skelClassStr.append(" ");


			  skelClassStr.append(mthds[i].getName());
			  skelClassStr.append("_"+i);
				skelClassStr.append("()");

				/*
				// we will generate our own sequential argument names
				for(int j=0; j<params.length; j++)
				{
				  skelClassStr.append(params[j].getName());
				  skelClassStr.append(" arg_"); skelClassStr.append(j);
				  if(j!=params.length-1) skelClassStr.append(",");
				}
				skelClassStr.append(")");
				*/


				// specify required exceptions.
				Class exceptions[] = mthds[i].getExceptionTypes();
        if(exceptions.length > 0) skelClassStr.append(" throws ");
				for(int j=0; j<exceptions.length; j++)
				{
				  skelClassStr.append(exceptions[j].getName());
				  if(j!=exceptions.length-1) System.out.print(",");
				}

				// body of proxy method.
				skelClassStr.append("\n{\n");

				// catch everything in an exception block
				skelClassStr.append("try{\n");

				Class params[] = mthds[i].getParameterTypes();
				// we will unmarshal each of the arguments from client first.
				for(int j=0; j<params.length; j++)
				{
					String argType = params[j].getName();
				  skelClassStr.append(argType);

				  skelClassStr.append(" arg_"); skelClassStr.append(j);
				  skelClassStr.append(" = ");

				  if(! params[j].isPrimitive())
					   skelClassStr.append("(" + argType + ")" + "ois.readObject();");
					else if(argType.equals("int"))
					  skelClassStr.append("ois.readInt();");
					else if(argType.equals("boolean"))
					  skelClassStr.append("ois.readBoolean();");
					else if(argType.equals("byte"))
					  skelClassStr.append("ois.readByte();");
					else if(argType.equals("char"))
					  skelClassStr.append("ois.readChar();");
					else if(argType.equals("short"))
					  skelClassStr.append("ois.readShort();");
					else if(argType.equals("double"))
					  skelClassStr.append("ois.readDouble();");
					else if(argType.equals("long"))
					  skelClassStr.append("ois.readLong();");
					else if(argType.equals("float"))
					  skelClassStr.append("ois.readFloat();");

					skelClassStr.append("\n");
				}
				  
				// specify the original return types.
				String returnType = mthds[i].getReturnType().getName();
				if(! returnType.equals("void"))
				{
			    skelClassStr.append(returnType);
				  skelClassStr.append(" __ret_arg = ");
				}
				// and call the function on the target object.
				skelClassStr.append("obj.");
			  skelClassStr.append(mthds[i].getName());
				skelClassStr.append("(");
				for(int j=0; j<params.length; j++)
				{
				  skelClassStr.append("arg_"); skelClassStr.append(j);
				  if(j!=params.length-1) skelClassStr.append(",");
				}
				skelClassStr.append(");\n");

				// Tell the other side that the execution was fine ...
				skelClassStr.append("oos.writeInt(CHARON_MTHD_EXEC_OK);\n");

				// if the function had a return type, return the value.
				if(! returnType.equals("void"))
				{
					if(!mthds[i].getReturnType().isPrimitive())
					  skelClassStr.append("oos.writeObject(__ret_arg);");
					else if(returnType.equals("int"))
					  skelClassStr.append("oos.writeInt(__ret_arg);");
					else if(returnType.equals("boolean"))
					  skelClassStr.append("oos.writeBoolean(__ret_arg);");
					else if(returnType.equals("byte"))
					  skelClassStr.append("oos.writeByte(__ret_arg);");
					else if(returnType.equals("char"))
					  skelClassStr.append("oos.writeChar(__ret_arg);");
					else if(returnType.equals("short"))
					  skelClassStr.append("oos.writeShort(__ret_arg);");
					else if(returnType.equals("double"))
					  skelClassStr.append("oos.writeDouble(__ret_arg);");
					else if(returnType.equals("long"))
					  skelClassStr.append("oos.writeLong(__ret_arg);");
					else if(returnType.equals("float"))
					  skelClassStr.append("oos.writeFloat(__ret_arg);");
				}
				skelClassStr.append("\n");
				skelClassStr.append("oos.flush();\n");

				// catch everything from exception block
				skelClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method.\", e); }\n");

				// end of proxy method
				skelClassStr.append("\n}\n");

				skelClassStr.append("\n");
			}

			// close function common to all proxy objects.
			skelClassStr.append("public void charon_close() throws charon.CharonException");
			skelClassStr.append("\n{\n");
			// catch everything in an exception block
			skelClassStr.append("try{\n");
			skelClassStr.append("socketOpen = false;\n");
			skelClassStr.append("\n}\ncatch(Exception e){ throw new charon.CharonException(\"An unexpected error occured in proxy class method. unable to perform cleanup as part of closing.\", e); }\n");
			skelClassStr.append("}\n\n");

			// generic destructor
			skelClassStr.append("protected void finalize()");
			skelClassStr.append("\n{\n");
			// catch everything in an exception block
			skelClassStr.append("try{\n");
			skelClassStr.append("if(socketOpen) charon_close();\n");
			skelClassStr.append("\n}\ncatch(Exception e){ e.printStackTrace(); }\n");
			skelClassStr.append("}\n\n");


			// end of proxy class
			skelClassStr.append("}\n");

			return skelClassStr;
	}


  public static void main(String args[])
	{
		try
		{
	    Class cls = Class.forName(args[0]);

			FileOutputStream fos_proxy = new FileOutputStream(args[1] + "/" + cls.getSimpleName()+"_Proxy.java");
			fos_proxy.write(buildProxy(cls).toString().getBytes());
			fos_proxy.close();

			FileOutputStream fos_skel = new FileOutputStream(args[1] + "/" + cls.getSimpleName()+"_Skel.java");
			fos_skel.write(buildSkel(cls).toString().getBytes());
			fos_skel.close();


			/*
			System.out.println(buildProxy(cls));
			System.out.println(buildSkel(cls));
			*/

		}
		catch(ClassNotFoundException e)
		{
		  e.printStackTrace();
		}
		catch(FileNotFoundException e)
		{
		  e.printStackTrace();
		}
		catch(IOException e)
		{
		  e.printStackTrace();
		}
	
	}

}
