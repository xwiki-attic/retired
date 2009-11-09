package org.xwiki.rest;

import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.restlet.data.MediaType;
import org.xwiki.rest.model.XStreamFactory;

import com.thoughtworks.xstream.XStream;
import com.xpn.xwiki.test.AbstractXWikiComponentTestCase;

public abstract class AbstractHttpTest extends AbstractXWikiComponentTestCase
{
    protected HttpClient httpClient;

    protected XStream xstream;

    protected Random random;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        httpClient = new HttpClient();
        xstream = XStreamFactory.getXStream();
        random = new Random();
    }

    protected String getFullUri(String relativeUri)
    {
        return String.format("%s%s", TestConstants.REST_API_ENTRYPOINT, relativeUri);
    }

    public abstract void testRepresentation() throws Exception;

    protected GetMethod executeGet(String uri) throws Exception
    {
        GetMethod getMethod = new GetMethod(uri);
        httpClient.executeMethod(getMethod);

        return getMethod;
    }

    protected GetMethod executeGet(String uri, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executeGet(uri);
    }

    protected PutMethod executePut(String uri, Object object) throws Exception
    {
        PutMethod putMethod = new PutMethod(uri);
        RequestEntity entity =
            new StringRequestEntity(xstream.toXML(object), MediaType.APPLICATION_XML.toString(), "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected PutMethod executePut(String uri, Object object, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executePut(uri, object);
    }

    protected DeleteMethod executeDelete(String uri) throws Exception
    {
        DeleteMethod deleteMethod = new DeleteMethod(uri);
        httpClient.executeMethod(deleteMethod);

        return deleteMethod;
    }

    protected DeleteMethod executeDelete(String uri, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executeDelete(uri);
    }

    protected String getUriPatternForResource(Class< ? extends XWikiResource> resourceClass) throws Exception
    {
        XWikiResource resource =
            (XWikiResource) getComponentManager().lookup(XWikiResource.class.getName(), resourceClass.getName());

        String uriPattern = resource.getUriPattern();

        getComponentManager().release(resource);

        return uriPattern;
    }

}
