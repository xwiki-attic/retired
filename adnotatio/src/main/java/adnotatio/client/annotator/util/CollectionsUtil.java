/**
 * 
 */
package adnotatio.client.annotator.util;

import java.util.Comparator;
import java.util.List;

/**
 * This is an utility class containing the
 * {@link CollectionsUtil#search(List, Object, Comparator)} method used to
 * search a given object in an ordered list in the GWT environment.
 * 
 * @author kotelnikov
 */
public class CollectionsUtil {

    /**
     * Search the position of the given object in the list of ordered objects.
     * To compare objects between them the given comparator is used. This method
     * returns the position <code>pos</code> of the object in the list or if
     * there is no such an object in the list then this method returns
     * <code>-(pos+1)</code> where <code>pos</code> is the position where
     * the object should be inserted.
     * 
     * <pre>
     * This code shows how this class can be used:
     * 
     * List list = ...
     * int pos = CollectionsUtil.search(list, "abc", new Comparator() {
     *     public int compare(T o1, T o2) {
     *          String s1 = (String) o1;
     *          String s2 = (String) o2;
     *          return s1.compare(s2);
     *     }
     * });
     * if (pos >= 0) {
     *      System.out.println("The object was found in the " 
     *          + pos 
     *          + " position");
     * } else {
     *      pos = -(pos + 1);
     *      System.out.println("The object should be inserted in the " 
     *          + pos 
     *          + " position");
     * }
     * </pre>
     * 
     * @param list the sorted list of objects
     * @param value the value to check
     * @param c the comparator used to compare objects from the list and the
     *        given object
     * @return the position of the object in the list or <code>-(pos+1)</code>
     *         where <code>pos</code> is the position where the object should
     *         be inserted
     */
    public static int search(List list, Object value, Comparator c) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Object midVal = list.get(mid);
            int cmp = c.compare(midVal, value);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1); // key not found
    }

}
