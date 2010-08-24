/*
 * Created on 2005-aug-04
 * 
 */
package xunit.utility;

import java.lang.reflect.*;
import java.util.Vector;

/**
 *  Reflection Class Provides Unitility Methods to Lookup Classes and 
 *  Create Methods, Contructors, and Parameters Lists
 * 
 *  This Class will be used with DataGenerationAPI to dynamically figure out
 *  Paramters, so that Automated Test Cases can be Generated
 * 
 *  @author Abdul Haseeb  
 */
public class Reflection {

    /**
     * Class For which the Reflection is Performed <code>_class</code>
     */
    private Class _class;
    
    /**
     * List of Methods <code>declaredMethod</code>
     */
    private Method declaredMethod[];
    
    /**
     * List of Constructors <code>declaredConstructor</code>
     */
    private Constructor declaredConstructor[];    
    
    /**
     * List of Fields <code>declaredFields</code>
     */
    private Field declaredFields[];

    /**
     * @return List of Constuctors
     */
    public Constructor[] getDeclaredConstructor() {
        return declaredConstructor;
    }
    
    /**
     * @return List of Methods
     */
    public Method[] getDeclaredMethod() {
        return declaredMethod;
    }

    /**
     * @return List of Fields
     */
    public Field[] getDeclaredFields() {
        return declaredFields;
    }

    /**
     * @param MethodName MethodsName for which the Method it to be Searched For in the Given Class
     * @return
     */
    public Method getMethod(String MethodName) {
        for (int i = 0; i < declaredMethod.length; i++) {
            if ( MethodName.equals(declaredMethod[i].getName())){
                return declaredMethod[i];
            }
        }
        return null;
    }
    
    /**
     * @param MethodName MethodName for which the Modifier is to be retrieved
     * @return
     */
    public String getMethodModifier(String MethodName) {
        Method method = getMethod(MethodName);
        if (method!=null) return Modifier.toString(method.getModifiers());
        return null;
    }

    /**
     * @param MethodName MethodName for which the ReturnType is to be retrieved
     * @return
     */
    public Class getMethodReturnType (String MethodName) {
        Method method = getMethod(MethodName);
        if (method!=null) return method.getReturnType();
        return null;
    }

    /**
     * @param MethodName MethodName for which the Method Parameters are to be retrieved
     * @return
     */
    public Class[] getMethodParameters(String MethodName) {
        Method method = getMethod(MethodName);
        if (method!=null) return method.getParameterTypes();
        return null;
    }
    
    /**
     * @return Constructor parameters 
     */
    public Vector getConstructorParameters() {
        Vector paramVector = new Vector();
        for (int i = 0; i < declaredConstructor.length; i++) {
            paramVector.add(declaredConstructor[i].getParameterTypes());
        }
        return paramVector;
    }    

    /**
     * @param FieldName
     * @return Field by FieldName
     * @throws Throwable
     */
    public Field getField(String FieldName) throws Throwable{
        return (_class.getField(FieldName));        
    }
    
    /**
     * @param member
     * @return Declaring Class for the given Member
     */
    protected Class getDeclaringClass(Member member){
        return (member.getDeclaringClass());
    }

    /**
     * @param member
     * @return Declaring Class for the given Member
     */
    protected String getDeclaringClassString(Member member){
        return ((Class)(member.getDeclaringClass())).toString();
    }
    
    /**
     * Load Class from Class Path,
     * This has to be Overridden to access XWikiDatabse to get Class
     * 
     * @param ClassPath
     * @throws Throwable
     */
    public Reflection(String ClassPath)throws Throwable {
        _class = Class.forName(ClassPath);

        /* 
         * Populate the Lists
         */
        _class.getDeclaredMethods();
        _class.getConstructors();
        _class.getFields();
    }
    
    /**
     * Constructor: pass the Loaded Class
     * e.g. Loaded from the XWikiDatabase
     * 
     * @param cls
     * @throws Throwable
     */
    public Reflection(Class cls)throws Throwable {
        _class = cls;
        
        /*
         *  Populate the Lists
         */
        _class.getDeclaredMethods();
        _class.getConstructors();
        _class.getFields();
    }
}
