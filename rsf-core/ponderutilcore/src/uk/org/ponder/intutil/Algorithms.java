/*
 * Created on 26-Sep-2003
 */
package uk.org.ponder.intutil;

import java.util.Random;

/**
 * @author Bosmon
 *
 * The class Algorithms incorporates several useful algorithms culled from the STL.
 */
public class Algorithms {
  public static void random_shuffle(intVector toshuf, int first, int last, Random random) {
    for (int i = first + 1; i < last; ++ i) {
      int swapind = first + random.nextInt(1 + i - first);
      int temp = toshuf.intAt(i);
      toshuf.setIntAt(i, toshuf.intAt(swapind));
      toshuf.setIntAt(swapind, temp);   
    }
  }
  
  public static void random_shuffle(int[] toshuf, Random random) {
    for (int i = 1; i < toshuf.length; ++ i) {
      int swapind = random.nextInt(1 + i);
      int temp = toshuf[i]; 
      toshuf[i] = toshuf[swapind];
      toshuf[swapind] = temp;
    }
  }
  
  public static boolean equals(int[] array1, int[] array2) {
    if (array1.length != array2.length) return false;
    for (int i = 0; i < array1.length; ++ i) {
      if (array1[i] != array2[i]) return false;
    }
    return true;
  }
  
  public static int[] makeIota(int size, int start) {
    int[] togo = new int[size];
    for (int i = 0; i < size; ++ i) {
      togo[i] = start + i;
    }
    return togo;
  }
  /*
  public static void reverse(intIterator first, intIterator last) {
    while (true)
      if (first == last || first == last.prev())
        return;
      else {
        int temp = first.getInt();
        first.setInt(last.getInt());
        iter_swap(__first++, __last);
        first.next();
      }
  }

  template <class _BidirectionalIter, class _Distance>
  _BidirectionalIter __rotate(_BidirectionalIter __first,
                              _BidirectionalIter __middle,
                              _BidirectionalIter __last,
                              _Distance*,
                              const bidirectional_iterator_tag &) {
    if (__first == __middle)
      return __last;
    if (__last  == __middle)
      return __first;

    __reverse(__first,  __middle, bidirectional_iterator_tag());
    __reverse(__middle, __last,   bidirectional_iterator_tag());

    while (__first != __middle && __middle != __last)
      swap (*__first++, *--__last);

    if (__first == __middle) {
      __reverse(__middle, __last,   bidirectional_iterator_tag());
      return __last;
    }
    else {
      __reverse(__first,  __middle, bidirectional_iterator_tag());
      return __first;
    }
  }
*/
}
