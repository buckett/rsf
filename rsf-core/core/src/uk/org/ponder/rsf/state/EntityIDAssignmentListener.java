/*
 * Created on 06-Jan-2006
 */
package uk.org.ponder.rsf.state;

public interface EntityIDAssignmentListener {
  public void IDAssigned(Object entity, String oldid, Object newid);
}
