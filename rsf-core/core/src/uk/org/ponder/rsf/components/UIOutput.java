/*
 * Created on Jul 27, 2005
 */
package uk.org.ponder.rsf.components;

/**
 * A UIOutput holds a single String value, which may or may not be bound to an
 * EL value binding. It may peer with essentially any tag, since its operation
 * is to replace the tag body with the <code>text</code> value it holds, if
 * this value is not null.
 * <p>
 * The central prototype of all components.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class UIOutput extends UIBoundString {
  /**
   * Constructs a text field which is bound to a particular path in the bean
   * model. If the initial value is set to <code>null</code> (recommended), it
   * will be fetched automatically from the bean model during fixup.
   */
  public static UIOutput make(UIContainer parent, String ID, String initvalue,
      String binding) {
    UIOutput togo = new UIOutput();
    if (initvalue != null) {
      togo.setValue(initvalue);
    }
    togo.valuebinding = ELReference.make(binding);
    togo.ID = ID;
    parent.addComponent(togo);
    return togo;
  }

  /**
   * Constructs an unbound text field with the specified initial value. This
   * will not interact with the bean model in any way.
   */
  public static UIOutput make(UIContainer parent, String ID, String initvalue) {
    return make(parent, ID, initvalue, null);
  }

  /**
   * This constructor creates an output component that will simply select a
   * message already present in the template, free from any further interaction
   * with the code.
   */
  public static UIOutput make(UIContainer parent, String ID) {
    return make(parent, ID, null, null);
  }

  /** Constructor for a standalone UIOutput that will be added to the tree
   * later.
   */
  public static UIOutput make(String initvalue) {
    UIOutput togo = new UIOutput();
    if (initvalue != null) {
      togo.setValue(initvalue);
    }
    return togo;
  }
}
