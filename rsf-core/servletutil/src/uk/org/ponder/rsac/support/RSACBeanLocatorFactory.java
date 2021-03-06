/*
 * Created on Sep 18, 2005
 */
package uk.org.ponder.rsac.support;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.rsac.RSACResourceLocator;
import uk.org.ponder.springutil.GenericAndReasonableApplicationContext;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * This code, grievously stolen from Andrew Thornton, hides away all the
 * unpleasant munging required to obtain a "Generic" Spring application context
 * using standard Spring resource location semantics.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class RSACBeanLocatorFactory {
  private RSACBeanLocatorImpl rsacbeanlocator = null;

  /**
   * Creates a RequestScopeAppContextPool from the given config locations and
   * parent Application Context
   * 
   * @param configLocation
   * @param parent
   * @return a new pool
   */
  public static ConfigurableApplicationContext readContext(String[] configLocations,
      ApplicationContext parent) {
    GenericApplicationContext initialContext = new GenericAndReasonableApplicationContext();
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(
        initialContext);

    Resource[] resources = new Resource[0];

    if (configLocations != null) {
      for (int i = 0; i < configLocations.length; ++i) {
        String location = configLocations[i];
        try {
          Resource[] newresources = parent.getResources(location);
          resources = (Resource[]) ArrayUtil.concat(resources, newresources);
        }
        catch (Exception e) {
          throw UniversalRuntimeException
              .accumulate(e, "Bad configuration location " + location
                  + " for RSACBeanLocator");
        }
      }
    }
    beanDefinitionReader.loadBeanDefinitions(resources);
    initialContext.setParent(parent);
    return initialContext;
  }
  
  public void setRSACResourceLocator(RSACResourceLocator resourcelocator) {
    ConfigurableApplicationContext cac = readContext(resourcelocator.getConfigLocations(),
        resourcelocator.getApplicationContext());
    rsacbeanlocator = new RSACBeanLocatorImpl();
    rsacbeanlocator.setBlankContext(cac);
  }

  public RSACBeanLocatorImpl getRSACBeanLocator() {
    return rsacbeanlocator;
  }

}
