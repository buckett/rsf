/*
 * Created on 10-Mar-2006
 */
package uk.org.ponder.rsac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import uk.org.ponder.util.RunnableWrapper;

/**
 * Acquire all beans of type BeanFetchBracketer and add them into the RBIMap.
 */
public class BracketerPopulator {

  public static void populateBracketers(ApplicationContext applicationContext,
      Map rbimap) {
    HashMap build = new HashMap();
    String[] names = applicationContext
        .getBeanDefinitionNames(BeanFetchBracketer.class);
    for (int i = 0; i < names.length; ++i) {
      BeanFetchBracketer bracketer = (BeanFetchBracketer) applicationContext
          .getBean(names[i]);
      List bracks = (ArrayList) build.get(bracketer.getTargetBeanName());
      if (bracks == null) {
        bracks = new ArrayList();
        build.put(bracketer.getTargetBeanName(), bracks);
      }
      bracks.add(bracketer.getWrappingBean());
    }

    for (Iterator nameit = build.keySet().iterator(); nameit.hasNext();) {
      String name = (String) nameit.next();
      List wrappers = (List) build.get(name);
      Object[] resolved = new Object[wrappers.size()];
      for (int i = 0; i < wrappers.size(); ++i) {
        Object wrappero = wrappers.get(i);
        if (wrappero instanceof RunnableWrapper) {
          resolved[i] = wrappero;
        }
        else {
          String wrappername = (String) wrappero;
          if (applicationContext.containsBeanDefinition(wrappername)) {
            RunnableWrapper wrapper = (RunnableWrapper) applicationContext
                .getBean(wrappername);
            resolved[i] = wrapper;
          }
          else {
            resolved[i] = wrappername;
          }
        }
      } // end for each wrapper for this target
      RSACBeanInfo rbi = (RSACBeanInfo) rbimap.get(name);
      rbi.fetchwrappers = resolved;
    }

  }

}
