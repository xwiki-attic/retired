/**
 * 
 */
package adnotatio.client.annotator.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the container for ranges giving capabilities to search ranges by a
 * value from these ranges. It is possible to find all ranges intersecting a
 * specified range. Each range is characterized by its begin and end values.
 * <p>
 * Internally this class builds an ordered list of points containing split
 * values and a set of corresponding objects. This list is used to lookup ranges
 * using a simple binary search.
 * </p>
 * 
 * <pre>
 *           [_________A___________]
 *   [_____B_.______]       [______._C_______]  <-- Individual ranges
 *   |       |  [___.__D____.___]  |         |     (A, B, C and D)
 *   |       |  |   |       |   |  |         |
 *   |       |  |   |       |   |  |         |
 * __V_______V__V___V_______V___V__V_________V___   Resulting list of points 
 *   |B      |AB|ABD|AD     |ACD|AC|C        |  <-- formed by projection of 
 *                                                  range borders to one axe.
 *                                                  Each point corresponds to 
 *                                                  a set of ranges.
 * </pre>
 * 
 * @author kotelnikov
 */
public class RangeList {

    /**
     * Objects of this type are used to keep a set of overlapping ranges and the
     * start point of this overlapping.
     */
    private static class Point {

        /**
         * The set of objects associated with this split point
         */
        public final Set objects = new HashSet();

        /**
         * The split position. It corresponds to the start or the end point of
         * some ranges
         */
        public final Comparable value;

        /**
         * The default constructor of this class. Initializes the point.
         * 
         * @param value
         */
        public Point(Comparable value) {
            this.value = value;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return value + "" + objects;
        }
    }

    /**
     * This list contains all individual points defining the full range
     * spectrum. Each new range can split the spectrum in additional parts.
     */
    private List fPoints = new ArrayList();

    /**
     * The default constructor.
     */
    public RangeList() {
        super();
    }

    /**
     * Adds a new object associated with the specified range
     * 
     * @param begin beginning of the range
     * @param end the end of the range
     * @object the object associated with the range
     */
    public void add(Comparable begin, Comparable end, Object object) {
        int beginPos = addPoint(begin);
        int endPos = addPoint(end);
        for (int i = beginPos; i < endPos; i++) {
            Point point = (Point) fPoints.get(i);
            point.objects.add(object);
        }
    }

    /**
     * Returns an index of the point corresponding to the given value. If there
     * is no point corresponding to the specified value then a new point will be
     * created and its index will be returned.
     * 
     * @param value a split value corresponding to begin or end of a range
     * @return the position of the newly inserted point
     */
    private int addPoint(Comparable value) {
        int pos = searchPosition(value);
        if (pos < 0) {
            pos = -(pos + 1);
            Point prev = (Point) (pos > 0 ? fPoints.get(pos - 1) : null);
            Point point = new Point(value);
            fPoints.add(pos, point);
            if (prev != null) {
                point.objects.addAll(prev.objects);
            }
        }
        return pos;
    }

    /**
     * Removes objects from the internal list.
     */
    public void clear() {
        fPoints.clear();
    }

    /**
     * Returns a set of objects associated with ranges containing the specified
     * value
     * 
     * @param value the value for which a set of containing ranges will be
     *        returned
     * @return a set of ranges containing the specified position.
     */
    public Set getRanges(Comparable value) {
        return getRanges(value, value);
    }

    /**
     * Returns a list containing all ranges having intersections with the
     * specified range.
     * 
     * @param begin the begin value of the range
     * @param end the end of the range
     * @return a list containing all ranges having intersections with the
     *         specified range
     */
    public Set getRanges(Comparable begin, Comparable end) {
        int p = searchPosition(begin);
        if (p < 0) {
            p = -(p + 1);
            if (p > 0)
                p--;
        }
        Set set = new HashSet();
        int size = fPoints.size();
        for (int i = p; i < size; i++) {
            Point point = (Point) fPoints.get(i);
            if (point.value.compareTo(end) > 0)
                break;
            set.addAll(point.objects);
        }
        return set;
    }

    /**
     * Removes the object associated with the specified range
     * 
     * @param range the range to remove
     */
    public void remove(Comparable begin, Comparable end, Object object) {
        int beginPos = addPoint(begin);
        int endPos = addPoint(end);
        Point next = null;
        for (int i = endPos; i >= beginPos; i--) {
            Point point = (Point) fPoints.get(i);
            point.objects.remove(object);
            if (next != null && next.objects.equals(point.objects)) {
                fPoints.remove(i + 1);
            }
            next = point;
        }
    }

    /**
     * This method returns the position where a range specified by the given
     * position can be found; if there is no such range then this method returns
     * the "-(pos+1)" value where "pos" is the index where the range should be
     * inserted.
     * 
     * @param list the list where the position is searched
     * @param value the begin value of the range
     * @return the position where a range specified by the given position and
     *         length can be found; if there is no such range then this method
     *         returns the "-(pos+1)" value where "pos" is the index where the
     *         range should be inserted.
     */
    private int searchPosition(Comparable value) {
        int low = 0;
        int high = fPoints.size() - 1;
        while (low <= high) {
            int idx = (low + high) >>> 1;
            Point point = (Point) fPoints.get(idx);
            int cmp = point.value.compareTo(value);
            if (cmp < 0)
                low = idx + 1;
            else if (cmp > 0)
                high = idx - 1;
            else
                return idx; // key found
        }
        return -(low + 1); // key not found
    }

}
