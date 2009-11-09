package org.xwiki.eclipse.ui.editors.parser;

import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.Parser;

public class VelocityParser extends RuntimeInstance {
   
    private boolean fIsInitialized = false;

    private Hashtable fDirectives;

	private List fUserDirectives;

    private Hashtable fMacros = new Hashtable();

	public Collection getUserDirectives() {
		return fUserDirectives;
	}

	public Collection getLibraryMacros() {
		return fMacros.values();
	}

	public boolean isUserDirective(String aName) {
		return fUserDirectives.contains(aName);
	}

    public synchronized void init() throws Exception {
    	if (!fIsInitialized) {
			// Initialize system and user directives
    		initializeDirectives();

    		// Call super implementation last because it calls createNewParser()
    		super.init();
    		fIsInitialized = true;
	   	}
    }

	public Parser createNewParser() {
		Parser parser = super.createNewParser();
		parser.setDirectives(fDirectives);
		return parser;
	}

    private void initializeDirectives() throws Exception {
        fDirectives = new Hashtable();
        
        Properties directiveProperties = new Properties();
       
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(
        		RuntimeConstants.DEFAULT_RUNTIME_DIRECTIVES);
    
        if (inputStream == null)
            throw new Exception("Error loading directive.properties! ");
        
        directiveProperties.load(inputStream);
        
        Enumeration directiveClasses = directiveProperties.elements();
        
        while (directiveClasses.hasMoreElements())
        {
            String directiveClass = (String) directiveClasses.nextElement();
            loadDirective( directiveClass, "System" );
        }
    }

    private void loadDirective( String directiveClass, String caption )
    {    
        try
        {
            Object o = Class.forName( directiveClass ).newInstance();
            
            if ( o instanceof Directive )
            {
                Directive directive = (Directive) o;
                fDirectives.put(directive.getName(), directive);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();   
        }
    }
}
