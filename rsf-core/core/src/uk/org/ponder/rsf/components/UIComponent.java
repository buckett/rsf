/*
 * Created on Jul 27, 2005
 */
package uk.org.ponder.rsf.components;

import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIDecorator;
import uk.org.ponder.rsf.util.RSFUtil;

/**
 * UIComponent is the base of the entire RSF component hierarchy. Components
 * derived from this class may either be containers derived from UIContainer,
 * or else leaf components peering with target dialect tags.
 * Note that Components form a containment hierarchy ONLY to allow nested 
 * repetitive domains. This class is mutually referential with UIContainer.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class UIComponent {
  /** This ID corresponds to the rsf:id in the view template, and is parsed
   * by use of the class SplitID.
   */
  public String ID;
  // fullid is the full path to this component
  // structure: ID1-prefix:ID1-suffix:localID1:ID2-prefix:ID2-suffix:localID2:etc.
  private String fullID;

  /** The algorithm used here must deterministically generate a string ID
   * globally unique within the containing component tree (the View), by 
   * appending path components derived from IDs and local IDs found at each
   * level of UIContainer. This algorithm should be "accessible" to simple 
   * environments such as XSLTs since they will need to operate it to generate
   * inter-component references within a view (for example to express any
   * EL dependencies).
   * <p>
   * The structure of the ID forms colon-separated pairs, one for each
   * container in the path, ending with the rsf:id of any leaf component, e.g.
   * ID1-prefix:local-ID1:ID2-prefix:localID2:etc.
   */
  public String getFullID() {
    if (fullID == null) {
      fullID = RSFUtil.computeFullID(this);
    }
    return fullID;
  }
  
  /** Updates the full ID of this component with the supplied value. This
   * is an "emergency" method to be used only as a last resort. Within RSF
   * it is necessary to ensure that UIBound components arising as direct children
   * of "composite" parents can have their IDs set correctly before value
   * fixup.
   */
  public void updateFullID(String fullID) {
    this.fullID = fullID;
  }
  
  /** Acquires the value of the fullID field without triggering a computation.
   * This is to be used primarily from text fixtures.
   */
  public String acquireFullID() {
    return fullID;
  }
  
  /** The containing parent of this component, or <code>null</code> for the
   * UIContainer representing the view root.
   */
  public UIContainer parent;
  
  /** A list of "decorators" which alter the rendering behaviour of this 
   * component, orthogonal to its binding behaviour. Usually <code>null</code>.
   */
  public DecoratorList decorators;
  
  /** Add the supplied decorator to the list for this component, initialising
   * the list if necessary.
   */
  public UIComponent decorate(UIDecorator decorator) {
    if (decorators == null) {
      decorators = new DecoratorList();
    }
    decorators.add(decorator);
    return this;
  }
  
  /** Returns a short String to aid with identifying this component for debug purposes **/
  
  public String debugString() {
    return "component with ID " + ID + " of " + getClass();
  }
}