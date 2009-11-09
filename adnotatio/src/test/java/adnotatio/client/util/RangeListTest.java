/**
 * 
 */
package adnotatio.client.util;

import java.util.Set;

import adnotatio.client.annotator.util.RangeList;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class RangeListTest extends TestCase {

    /**
     * @param name
     */
    public RangeListTest(String name) {
        super(name);
    }

    /**
     * 
     */
    public void test() {
        RangeList list = new RangeList();
        list.add(10, 20, "a");
        list.add(17, 25, "b");
        list.add(5, 12, "c");
        list.add(11, 18, "d");
        list.add(28, 35, "e");

        test(list, 3);
        test(list, 26);
        test(list, 36);
        test(list, 8, "c");
        test(list, 10, "c", "a");
        test(list, 11, "c", "a", "d");
        test(list, 30, "e");
        test(list, 1, 3);
        test(list, 1, 4, "c");
        test(list, 6, 16, "c", "a", "d", "b");
        test(list, 0, 22, "c", "a", "d", "b");
        test(list, 22, 6, "b", "e");
        test(list, 22, 100, "b", "e");
        test(list, 22, 3, "b");

        // Remove "a" and check
        list.remove(10, 20, "a");
        test(list, 3);
        test(list, 26);
        test(list, 36);
        test(list, 8, "c");
        test(list, 10, "c");
        test(list, 11, "c", "d");
        test(list, 30, "e");
        test(list, 1, 3);
        test(list, 1, 4, "c");
        test(list, 6, 16, "c", "d", "b");
        test(list, 0, 22, "c", "d", "b");
        test(list, 22, 6, "b", "e");
        test(list, 22, 100, "b", "e");
        test(list, 22, 3, "b");

        // Add a new point and check
        list.add(0, 50, "X");
        test(list, 3, "X");
        test(list, 26, "X");
        test(list, 36, "X");
        test(list, 8, "X", "c");
        test(list, 10, "X", "c");
        test(list, 11, "X", "c", "d");
        test(list, 30, "X", "e");
        test(list, 1, 3, "X");
        test(list, 1, 4, "X", "c");
        test(list, 6, 16, "X", "c", "d", "b");
        test(list, 0, 22, "X", "c", "d", "b");
        test(list, 22, 6, "X", "b", "e");
        test(list, 22, 100, "X", "b", "e");
        test(list, 22, 3, "X", "b");

    }

    private void test(RangeList list, int begin, int len, String... names) {
        Set<?> result = list.getRanges(begin, begin + len);
        assertNotNull(result);
        assertEquals(names.length, result.size());
        for (String name : names) {
            assertTrue(result.contains(name));
        }
    }

    private void test(RangeList list, int pos, String... names) {
        test(list, pos, 0, names);
    }

    public void testX() {
        RangeList list = new RangeList();
        Object o = new Object();
        list.add(14, 14 + 4, o);
        Set set = list.getRanges(16);
        assertNotNull(set);
        assertEquals(1, set.size());
        assertTrue(set.contains(o));
    }
}
