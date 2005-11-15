/*
 * Created on Nov 1, 2005
 */
package uk.org.ponder.rsf.processor;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.rsf.componentprocessor.ComponentProcessor;
import uk.org.ponder.rsf.components.UIBound;
import uk.org.ponder.rsf.components.UIComponent;
import uk.org.ponder.rsf.components.UIParameter;
import uk.org.ponder.rsf.state.FossilizedConverter;

/** Fetches values from the request bean model that are referenced via EL
 * value bindings, if such have not already been set. Will also compute the
 * fossilized binding for this component (not a completely cohesively coupled
 * set of functions, but we are accumulating quite a lot of little processors). 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class ValueFixer implements ComponentProcessor {
  private BeanLocator beanlocator;
  private BeanModelAlterer alterer;
  public void setBeanLocator(BeanLocator beanlocator) {
    this.beanlocator = beanlocator;
  }
  public void setModelAlterer(BeanModelAlterer alterer) {
    this.alterer = alterer;
  }
  // This dependency is here so we can free FC from instance wiring cycle on 
  // RenderSystem. A slight loss of efficiency since this component may never
  // be rendered - we might think about "lazy processors" at some point...
  private FossilizedConverter fossilizedconverter;

  public void setFossilizedConverter(FossilizedConverter fossilizedconverter) {
    this.fossilizedconverter = fossilizedconverter;
  }
  
  public void processComponent(UIComponent toprocesso) {
    if (toprocesso instanceof UIBound) {
      UIBound toprocess = (UIBound) toprocesso;
      if (toprocess.valuebinding != null && toprocess.acquireValue() == null) {
        // a bound component ALWAYS contains a value of the correct type.
        Object oldvalue = toprocess.acquireValue();
        Object flatvalue = alterer.getFlattenedValue(toprocess.valuebinding, beanlocator, oldvalue.getClass());
        toprocess.updateValue(flatvalue);
      }
      if (toprocess.fossilize && toprocess.fossilizedbinding == null) {
        UIParameter fossilized = fossilizedconverter.computeFossilizedBinding(toprocess);
        toprocess.fossilizedbinding = fossilized;
      }
    }
  }

}
