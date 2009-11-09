package org.xwiki.eclipse.ui.editors.contentassist;

public enum XwkiAPIObject {

	DOC("doc"),

	CONTEXT("context"),

	REQUEST("request"),

	XWIKI("xwiki");
	
	private String type;

	XwkiAPIObject(String type) {
		this.type = type;
	}
	
	
	public static boolean isValide(String type){				
		for(XwkiAPIObject validType : XwkiAPIObject.values()){			
			 if(validType.type.equals(type)){
				 return true;
			 }
		}		
		return false;
	}
	
	public static XwkiAPIObject getType(String type){
		for(XwkiAPIObject validType : XwkiAPIObject.values()){			
			 if(validType.type.equals(type)){
				 return validType;
			 }
		}		
		return null;
	}
	
}
