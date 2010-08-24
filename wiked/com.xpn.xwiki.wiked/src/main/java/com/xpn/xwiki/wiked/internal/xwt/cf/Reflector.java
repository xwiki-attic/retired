
package com.xpn.xwiki.wiked.internal.xwt.cf;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Reflector {

    private Class objectClass;
    private Map propertyValues;
    private Map propertySetters;
    private Map fields;
    
    public Reflector(Class objectClass) throws IntrospectionException {
        this.objectClass = objectClass;
        this.propertyValues = new HashMap();
        this.propertySetters = new HashMap();
        this.fields = new HashMap();
        BeanInfo bi = Introspector.getBeanInfo(objectClass);
        PropertyDescriptor[] descs = bi.getPropertyDescriptors();
        for (int i = 0; i < descs.length; i++) {
            PropertyDescriptor descriptor = descs[i];
            Method setter = descriptor.getWriteMethod();
            if (setter != null) {
                this.propertySetters.put(descriptor.getName(), setter);
            }
        }
        Field[] fields = objectClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            this.fields.put(fields[i].getName(), fields[i]);
        }
        
    }

    public void setProperties(NamedNodeMap attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            Object value = narrowType(attr);
            if (value != null) {
                this.propertyValues.put(attr.getNodeName(), value);
            }
        }
    }

    public void setProperties(Map properties) {
        this.propertyValues.clear();
        Iterator i = properties.keySet().iterator();
        while (i.hasNext()) {
            String key = (String)i.next();
            setProperty(key, properties.get(key));
        }
    }

    public void setProperty(String key, Object value) {
        Object propertyValue = narrowType(key, value);
        this.propertyValues.put(key, 
            (propertyValue != null) ? propertyValue : value);
    }
    
    public Object createObject() throws InstantiationException, 
    	IllegalAccessException, InvocationTargetException {
        Object object = this.objectClass.newInstance();
        modifyObject(object);
        return object;
    }

    public void modifyObject(Object object) throws IllegalAccessException, 
    	InvocationTargetException {
        System.out.println("Object "+object);
        Iterator keys = this.propertyValues.keySet().iterator();
        while (keys.hasNext()) {
            String propertyName = (String)keys.next();
            Object propertyValue = this.propertyValues.get(propertyName);
            Method setter = (Method)this.propertySetters.get(propertyName);
            if (setter != null) {
                if (propertyValue != null) {
                    System.out.println("- setting "+propertyName+" = "+
                       propertyValue+" ("+propertyValue.getClass().getName()+")");
                    setter.invoke(object, new Object[] {propertyValue});
                }
                continue;
            } else {
	            Field field = (Field)this.fields.get(propertyName);
	            if (field != null) {
	                if (propertyValue != null) {
                        System.out.println("- setting "+propertyName+" = "+
                           propertyValue+" ("+propertyValue.getClass().getName()+")");
	                    field.set(object, propertyValue);
	                }
	                continue;
	            }
            }
        }
    }        
    
    private Object narrowType(Node node) {
        return narrowType(node.getNodeName(), node.getNodeValue());
    }

    private Object narrowType(String key, Object value) {
        if (this.propertySetters.containsKey(key)) {
            Method setter = (Method)this.propertySetters.get(key);
            return narrowTo(value, setter.getParameterTypes()[0]);
        } else if (this.fields.containsKey(key)) {
            Field field = (Field)this.fields.get(key);
            return narrowTo(value, field.getType());
        } 
        
        return null;
    }
    
    private Object narrowTo(Object value, Class targetClass) {
        if (String.class.equals(targetClass)) {
            return value;
        }
        if (targetClass.isInstance(value)) {
            return value;
        }
        if (targetClass.isPrimitive()) {
            if (Integer.TYPE.equals(targetClass)) {
                targetClass = Integer.class; 
            }
        }
        try {
            Constructor c = targetClass.getConstructor(new Class[] {String.class});
            return c.newInstance(new Object[] {value});
        } catch (Exception ex) {
            return null;
        }
    }
    
}
