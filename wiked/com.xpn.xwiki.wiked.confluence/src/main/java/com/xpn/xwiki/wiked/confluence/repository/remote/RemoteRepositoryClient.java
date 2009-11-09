
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * The XMLRPC Client
 * @author psenicka_ja
 */
public class RemoteRepositoryClient {
    
    private URL url;
    private String name;
    private String password;
    private String token;

    private XmlRpcClient client;

	private static final Object[] NONE = new Object[0];
    
	public RemoteRepositoryClient(URL url, String name, String password) {
        this.url = url;
        this.name = name;
        this.password = password;
        this.client = new XmlRpcClient(url);
    }
        
    public Object execute(String method) throws RepositoryException {
    	return execute(method, null);
    }

    public Object execute(String method, Object[] data) throws RepositoryException {
        try {
            if (this.token == null) {
                login();
            }
        } catch (Exception ex) {
            throw new RepositoryException(ex);
        }
        try {   
            Vector params = new Vector();
            params.add(this.token);
            if (data != null) {
            	params.addAll(Arrays.asList(data));
            }
            return client.execute("confluence1."+method, params);
        } catch (Exception ex) {
            throw new RepositoryException(ex);
        }
    }

	public void login() throws XmlRpcException, IOException {
        Vector data = new Vector();
        data.add(this.name);
        data.add(this.password);
        this.token = (String)this.client.execute("confluence1.login", data);
	}

	public void logout() throws XmlRpcException, IOException {
        if (this.token != null) {
            Vector data = new Vector();
            this.client.execute("logout", data);
        }
    }	
    
}
