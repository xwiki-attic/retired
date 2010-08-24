/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
package sandbox;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;

public class XMLtest {
	public static void main(String[] args) {
		Element root = new Element("ROOT");
		Text newline = new Text("");
		Text content1 = new Text("");
		Element border = new Element("Border");
		border.addContent("border");
		Text content2 = new Text("");
		content1.setText("test1");
		newline.setText("\n");
		content2.setText("test2");

		root.addContent(content1);
		root.addContent(newline);
		
		root.addContent(content2);
		Document doc = new Document(root);
		
		XMLOutputter output = new XMLOutputter();
		try{
			output.output(doc, System.out);
			System.out.println("DOC: " + doc.getContent());
			System.out.println("DOC CHILDREN: " + doc.getContent().size());
			System.out.println("ROOT: " + root.getContent());
			System.out.println("ROOT CHILDREN: " + root.getContent().size());
			System.out.println("ROOT CHILDREN: " + root.getText());
			System.out.println(!true + " " + !false);
		}catch (Exception e){
			e.printStackTrace();
			
		}
	}
}
