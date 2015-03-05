// -------------------------------// adapted from Kevin T. Manley// CSE 593// -------------------------------package G13Components;import java.io.*;// Resource manager data itempublic abstract class RMItem implements Serializable, Cloneable{   protected boolean isDltd; //This item is marked for deletion.   protected boolean isModfd;    RMItem() { super(); isDltd = isModfd = false; }   public boolean isDeleted()   { return isDltd; }   public void mark4Deletion()   { isModfd=isDltd = true; }   public boolean isModified()   { return isModfd; }   public void markAsModified()   { isModfd = true; }   public void resetFlags()   { isModfd = isDltd = false; }   protected Object clone() throws CloneNotSupportedException   { return super.clone(); }   public RMItem copy() throws CloneNotSupportedException   { return (RMItem)super.clone(); }}