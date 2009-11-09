package com.xpn.p2pxwiki.communication;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;

public abstract class AbstractHandlerStub implements HandlerStub {
	private static final Log log = LogFactory.getFactory().getInstance(AbstractHandlerStub.class);
	
	public abstract Object execute(String function, Object[] params) throws P2PXWikiException;
	public abstract ConnectionFactory getConnectionFactory();
	public abstract String getPeerName();

	public Object getDynamicProxy(final String handlerName, final Class interf, final boolean localObjectMethods) {
		log.debug("Creating dynamic proxy for handler: "+handlerName);
		return Proxy.newProxyInstance(interf.getClassLoader(), new Class[] { interf },
			new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if (localObjectMethods && method.getDeclaringClass().equals(Object.class)) {
						log.debug("Locally invoking method in class Object: "+method.getName());
						return method.invoke(this, args); // call the method on the proxy itself
					}
					// on the class level String methodName = pClass.getName() + "." + pMethod.getName();
					// we don't use any TypeConverter ... would it be useful?
					log.debug("Remotely inviking method: "+method.getName());
					String methodName = handlerName + "." + method.getName();
					return execute(methodName, args);
	            }
			});
    }
}
