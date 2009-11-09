package org.xwiki.eclipse.ui.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.text.templates.Template;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * Class which reads the xwiki api proposal for resource files and create the templates
 * @author malaka
 *
 */
public class XwkiAPIResourceReader {

	/**
	 * creates the template list for the given xwiki variable content type 
	 * @param apiType
	 * @return
	 */
	public static List<Template> getXwikiTeplates(String apiType) {
		List<Template> apiList = new ArrayList<Template>();
		try {
			InputStream stream = UIPlugin.getDefault().getBundle().getEntry(
					"xwikiApi/" + apiType + ".txt").openStream();
			DataInputStream in = new DataInputStream(stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(strLine, " ");
				apiList.add(getTemplate(st));
			}

			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return apiList;
	}

	/**
	 * create a template from line in the resource file
	 * @param st line of text from the resource file 
	 * @return
	 */
	private static Template getTemplate(StringTokenizer st) {

		int paremCout = st.countTokens() - 1;

		String contextTypeId = "org.xwiki.eclipse.ui.editors.velocity.xwikiapi";

		String methodName = st.nextToken();
		StringBuffer method = new StringBuffer(methodName); // method signature
		StringBuffer patten = new StringBuffer(methodName); // Paten for the
		// method
		method.append('(');
		patten.append('(');
		while (st.hasMoreTokens()) {
			paremCout -= 2;
			String paramType = st.nextToken();
			String paramName = st.nextToken();
			if (paremCout > 0) {
				method.append(paramType).append(' ').append(paramName).append(
						", ");
				patten.append("${").append(' ').append(paramName).append("}, ");
			} else {
				method.append(paramType).append(' ').append(paramName);
				patten.append("${").append(paramName).append("}");
			}
		}
		method.append(')');
		patten.append(')');
		Template template = new Template(method.toString(), "",
				contextTypeId, patten.toString(), false);
		return template;
	}

}
