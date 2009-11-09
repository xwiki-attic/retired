package org.xwiki.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.WikiPrinter;
import org.wikimodel.wem.tex.TexSerializer;
import org.wikimodel.wem.xwiki.XWikiParser;

//{image:<image>|width=<width>|height=<height>|align=<align>|halign=<halign>|document=<document>|alt=<alt>|link=|fromIncludingDoc=}

//--wikiurl=http://userguides.xwiki.com/ --username=StephaneLauriere --password=stephane --pagelist-document=XWS_guide.Page_list --output=/home/sebastocha/eclipse.workspace.1/xwiki.xws-guide/main.tex

public class Texifier {

	private String wikiUrl;
	private String pageListDocumentFullName;
	private String password;
	private String username;
	private String output;

	public Texifier(String wikiUrl, String pageListDocumentName,
			String username, String password, String output) {
		this.wikiUrl = wikiUrl + "/xwiki/xmlrpc/confluence";
		this.pageListDocumentFullName = pageListDocumentName;
		this.password = password;
		this.username = username;
		this.output = output;
	}

	public static void main(String[] args) {

		// create the command line parser
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("wikiurl").withDescription(
				"URL of the wiki (Example: http://www.xwiki.org/)")
				.withValueSeparator('=').hasArg().create());

		options.addOption(OptionBuilder.withLongOpt("output").withDescription(
				"Output file.").withValueSeparator('=').hasArg().create());

		options
				.addOption(OptionBuilder
						.withLongOpt("pagelist-document")
						.withDescription(
								"Name of the document containing all pages to be parsed (example: Main.PageList). Page should be listed with '*' bullet.")
						.withValueSeparator('=').hasArg().create());

		options.addOption(OptionBuilder.withLongOpt("password")
				.withDescription("Password").withValueSeparator('=').hasArg()
				.create());

		options.addOption(OptionBuilder.withLongOpt("username")
				.withDescription("Login").withValueSeparator('=').hasArg()
				.create());

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			String wikiUrl = null, username = null, password = null, pageList = null, outputPath = null;

			if (line.hasOption("wikiurl")) {
				wikiUrl = line.getOptionValue("wikiurl");
			}
			if (line.hasOption("username")) {
				username = line.getOptionValue("username");
			}
			if (line.hasOption("password")) {
				password = line.getOptionValue("password");
			}
			if (line.hasOption("pagelist-document")) {
				pageList = line.getOptionValue("pagelist-document");
			}
			if (line.hasOption("output")) {
				outputPath = line.getOptionValue("output");
			}
			if (wikiUrl != null && username != null && password != null
					&& pageList != null && outputPath != null) {

				Texifier texifier = new Texifier(wikiUrl, pageList, username,
						password, outputPath);
				try {
					texifier.texify();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("texifier", options);

			}
		}

		catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}
	}

	public void texify() throws WikiParserException, XmlRpcException,
			IOException {

		XmlRpcClient client = createXmlRpcClient(wikiUrl);

		String token = (String) client.execute("confluence1.login",
				new Object[] { username, password });

		String[] pageNameData = getSpaceAndName(pageListDocumentFullName);

		Map page = (Map) client.execute("confluence1.getPage", new Object[] {
				token, pageNameData[0], pageNameData[1] });

		String content = (String) page.get("content");

		content = content.replaceAll("\\* \\[(.*)\\]\\n", "$1___");
		// System.out.println(content);
		String[] pages = content.split("___");

		FileOutputStream fos = new FileOutputStream(new File(output));
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		for (int i = 0; i < pages.length; i++) {

			pageNameData = getSpaceAndName(pages[i]);
			System.out.println("Processing " + pageNameData[1] + "...");

			page = (Map) client.execute("confluence1.getPage", new Object[] {
					token, pageNameData[0], pageNameData[1] });

			content = (String) page.get("content");

			parse(content, pages[i], bos);

		}
		bos.close();
		fos.close();

	}

	private String[] getSpaceAndName(String page) {
		int idx1 = page.indexOf('.');
		String spaceName = page.substring(0, idx1);
		String documentName = page.substring(idx1 + 1).replaceAll("\\+"," ");
		return new String[] { spaceName, documentName };

	}

	private XmlRpcClient createXmlRpcClient(String xmlRpcUrl)
			throws MalformedURLException {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		URL url = new URL(xmlRpcUrl);
		config.setServerURL(url);
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		return client;
	}

	/**
	 * @param string
	 * @throws WikiParserException
	 * @throws IOException
	 */
	protected void parse(String string, String label, OutputStream out)
			throws WikiParserException, IOException {

		label = "%%%slash%%%label%%%leftbracket%%%" + label
				+ "%%%rightbracket%%%\n\n";
		string = string.replaceAll("1.1 ([^\n]*)", "1.1 $1\n\n" + label);
		StringReader reader = new StringReader(string);
		IWikiParser parser = new XWikiParser();
		IWikiPrinter iwp = new WikiPrinter();
		IWemListener listener = new TexSerializer(iwp);
		parser.parse(reader, listener);
		String result = iwp.toString();
		result = result.replaceAll("%%%slash%%%", "\\\\");
		result = result.replaceAll("%%%leftbracket%%%", "{");
		result = result.replaceAll("%%%rightbracket%%%", "}");
		out.write(result.getBytes());
		out.flush();

	}

}
