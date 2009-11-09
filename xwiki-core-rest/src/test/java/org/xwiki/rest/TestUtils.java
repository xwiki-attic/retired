package org.xwiki.rest;

import org.apache.commons.httpclient.HttpMethod;

/**
 * @version $Id$
 */
public class TestUtils
{
    public static void printHttpMethodInfo(HttpMethod method) throws Exception
    {
        System.out.format("%s %s. Status: %d %s\n", method.getName(), method.getURI(), method.getStatusCode(), method
            .getStatusText());
    }

    public static void banner(String message)
    {
        banner(message, 80);
    }

    public static void banner(String message, int size)
    {
        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print("*");
        }

        System.out.format("\n* %s ", message);

        for (int i = 0; i < (size - message.length() - 3); i++) {
            System.out.print("*");
        }

        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print("*");
        }

        System.out.println();
    }
}
