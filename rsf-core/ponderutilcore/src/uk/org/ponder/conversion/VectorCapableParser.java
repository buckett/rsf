/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.conversion;

import java.util.Enumeration;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.iterationutil.Denumeration;
import uk.org.ponder.iterationutil.EnumerationConverter;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.mapping.ContainerTypeRegistry;
import uk.org.ponder.stringutil.StringList;

/**
 * An "aggregating" object parser/renderer that is capable of dealing with
 * vector (array, list) values by repeated invokation of a LeafObjectParser for
 * each parse/render.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
// Should convert this into an interface, but there is not really any kind of
// alternative implementation - all customizability is at the scalarparser
// level.
public class VectorCapableParser {
  private GeneralLeafParser scalarparser;
  private ContainerTypeRegistry ctr;
  private ReflectiveCache reflectiveCache;

  public void setScalarParser(GeneralLeafParser scalarparser) {
    this.scalarparser = scalarparser;
  }

  public void setReflectiveCache(ReflectiveCache reflectiveCache) {
    this.reflectiveCache = reflectiveCache;
  }
  
  /**
   * Determines whether the supplied object is some kind of "List of Strings"
   * type, either String[] or StringList.
   * @param o object to inspect
   * @return true if o is some kind of List Of Strings, otherwise false
   */
  public static boolean isLOSType(Object o) {
    return ArrayUtil.stringArrayClass.isAssignableFrom(o.getClass())
        || o instanceof StringList;
  }

  public void setContainerTypeRegistry(ContainerTypeRegistry ctr) {
    this.ctr = ctr;
  }
  
  /**
   * Converts some form of multiple Strings into some form of multiple Objects.
   * 
   * @param stringlist Some form of String collection - either a String[] array,
   *          or any kind of Collection with String elements. {@link isLOSType}
   *          will return true for this argument.
   * @param target Some kind of collection or array
   *          {@link EnumerationConverter#isDenumerable} will return true for
   *          this argument. The elements of this "collection" will be filled
   *          with parsed values.
   * @param elemtype type
   * @return Either a List or Array of Object according to whether the second
   *         parameter  represents a Class or is null. 
   */
  public Object parse(Object stringlist, Object target, Class elemtype) {
    // int size = EnumerationConverter.getEnumerableSize(stringlist);
    // Object togo = arraytype == null? new ArrayList(size) :
    // Array.newInstance(arraytype, size);
    Denumeration denum = EnumerationConverter.getDenumeration(target, reflectiveCache);
    Class containeetype = ctr.getContaineeType(target);
    if (containeetype != null) {
      elemtype = containeetype;
    }
    for (Enumeration elemenum = EnumerationConverter.getEnumeration(stringlist); elemenum
        .hasMoreElements();) {
      String elem = (String) elemenum.nextElement();
      Object converted = scalarparser.parse(elemtype, elem);
      denum.add(converted);
    }
    return target;
  }

  /**
   * Converts some form of multiple objects into some form of multiple Strings.
   * 
   * @param torenders Either some form of Object array or a Collection.
   * @param toreceive The object into which the rendered objects are to be placed as
   *          Strings - either a String[] array (of the correct size!) or a
   *          Collection.
   * @param resolver the BeanResolver
   */
  // This code will go into ValueFixer.
  public void render(Object torenders, Object toreceive, BeanResolver resolver) {
    Denumeration denum = EnumerationConverter.getDenumeration(toreceive, reflectiveCache);
    for (Enumeration rendenum = EnumerationConverter.getEnumeration(torenders); rendenum
        .hasMoreElements();) {
      Object torender = rendenum.nextElement();
      String rendered = resolver == null ? scalarparser.render(torender)
          : resolver.resolveBean(torender);
      denum.add(rendered);
    }
  }

}
