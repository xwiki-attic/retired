package com.xpn.p2pxwiki.utils;

import java.lang.reflect.InvocationTargetException;

import com.xpn.p2pxwiki.P2PXWikiException;

public class P2PUtil {
	public static Object getInstance(String className) throws P2PXWikiException {
		return getInstance(className, (Object[])null, (Class[])null);
	}
	
	public static Object getInstance(String className, Object[] params, Class[] paramTypes) throws P2PXWikiException {
		try{
			return Class.forName(className).getConstructor(paramTypes).newInstance(params);
		} catch(ClassNotFoundException ex){
			throw new P2PXWikiException(P2PXWikiException.INSTANCE, ex);
		} catch(NoSuchMethodException ex){
			throw new P2PXWikiException(P2PXWikiException.INSTANCE, ex);
		} catch(InvocationTargetException ex){
			throw new P2PXWikiException(P2PXWikiException.INSTANCE, ex);
		} catch (InstantiationException ex) {
			throw new P2PXWikiException(P2PXWikiException.INSTANCE, ex);			
		} catch(IllegalAccessException ex){
			throw new P2PXWikiException(P2PXWikiException.INSTANCE, ex);
		}
	}
	
	public static Object getSingletonInstance(String className) throws P2PXWikiException {
		try{
			return Class.forName(className).getMethod("getInstance", (Class[])null).invoke(null, (Object[])null);
		} catch(ClassNotFoundException ex){
			throw new P2PXWikiException(P2PXWikiException.SINGLETON_INSTANCE, ex);
		} catch(NoSuchMethodException ex){
			throw new P2PXWikiException(P2PXWikiException.SINGLETON_INSTANCE, ex);
		} catch(InvocationTargetException ex){
			throw new P2PXWikiException(P2PXWikiException.SINGLETON_INSTANCE, ex);			
		} catch(IllegalAccessException ex){
			throw new P2PXWikiException(P2PXWikiException.SINGLETON_INSTANCE, ex);
		}		
	}
}
