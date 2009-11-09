/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.plugin.lucene;


import java.io.File;
import java.util.HashMap;
import java.io.IOException;
import java.rmi.Naming;
import java.text.DecimalFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;


public class SlavesInfo extends Thread
{

	public String indexDir;

	public String masterInfo;

	private String slaveslist;

	public HashMap<String, String> slavesInfo = new HashMap<String, String>();

	private IndexWriter writer;

	private Analyzer analyzer;

	private IndexReader reader;

	private IndexSearcher searcher; 

	private QueryParser queryParser;

	public SlavesInfo(String indexDir, String masterInfo, String slaveslist)
	{
		this.indexDir = indexDir;
		this.masterInfo = masterInfo;
		this.slaveslist = slaveslist;
	}

	public void init()
	{
		this.analyzer = new StandardAnalyzer();
		
		this.queryParser = new QueryParser("title",this.analyzer);

		if(this.indexDir != null)
		{
			File f = new File(indexDir);
			if (!f.isDirectory()) {
				f.mkdirs();
			}
			if (!IndexReader.indexExists(f)) {
				try{	
					this.openWriter(true);
					addToIndex("1","1");
					this.writer.flush();
					this.closeWriter();
				}
				catch(Exception e)
				{
				}
			}

		}

		
		String list[] = slaveslist.trim().split(",");
		for(int i=0;i<list.length;i++)
		{
			if(list[i].indexOf(":")>0)
			{
				slavesInfo.put(list[i].split(":")[0],list[i].split(":")[1]);
				updateIndex(list[i].split(":")[0],null);
			}
		}
	
	}


	private void openWriter(boolean flag)
	{
		if (writer != null) {
        	    return;
        	}
		else
		{
			try{
				FSDirectory f = FSDirectory.getDirectory(indexDir);
			        writer = new IndexWriter(f, analyzer, flag);
				writer.setUseCompoundFile(true);
			}
			catch(Exception e)
			{
			}
		}

	}

	private void closeWriter()
	{
		if (this.writer == null) {
			return;
		}

		try {
			this.writer.optimize();
		} catch (IOException e1) {
		}

		try {
			this.writer.close();
		} catch (Exception e) {
		}

		this.writer = null;
		//System.out.println("Writer Closed");	
	}

	public void updateIndex(String hostname, String timestamp)
	{
		//call from GloabalIndexUpdater
		try{

			//delete the doc if exists earlier
			openSearcher();
			boolean flag=false;
			Query query = this.queryParser.parse("hostname:"+hostname);
			Hits hits = this.searcher.search(query);
			if(hits != null){
				if(hits.length()>0)
					deleteFromIndex(hits.id(0));
			}
			closeSearcher();

			//index new doc
			//
			openWriter(false);
			if(timestamp == null)
			{
				addToIndex(hostname,"000000000000000000011");
			}
			else
			{
				addToIndex(hostname,timestamp);
			}
			this.writer.flush();
			closeWriter();
		}
		catch(Exception e)
		{
		}

	}

	private String getFirstTimeStamp(String hostname)
	{
		try{
		Query query = new TermQuery(new Term("hostname", hostname));
		MultiSearcher remotesearcher = new MultiSearcher(new Searchable[]{lookupRemote(hostname)});
                Hits hits = remotesearcher.search(query);
		if(hits.length() > 0)
		{
                        Document doc = hits.doc(0);
			return doc.get("timestamp");
		}
		else
		{
			return "000000000000000000011";
		}
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	private void addToIndex(String hostname, String timestamp) throws IOException
	{
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
		luceneDoc.add(new Field("hostname",hostname,Field.Store.YES,Field.Index.TOKENIZED));
		luceneDoc.add(new Field("timestamp",timestamp,Field.Store.YES,Field.Index.TOKENIZED));
		if(this.writer !=null)
			this.writer.addDocument(luceneDoc);
	}

	private void deleteFromIndex(int id) throws Exception
	{
		this.reader.deleteDocument(id);
	}

	public String getRecentTimeStamp(String hostname)
	{
		//call from GloabalIndexUpdater
		try{
			this.openSearcher();
			Query query = new TermQuery(new Term("hostname", hostname));
			Hits hits = this.searcher.search(query);
			if(hits.length()>0)
			{
				Document doc = hits.doc(0);
				return doc.get("timestamp");
			}
			closeSearcher();
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private synchronized void openSearcher()
	{
		try {
			this.reader = IndexReader.open(this.indexDir);
			this.searcher = new IndexSearcher(this.reader);
		} catch (IOException e) {
		}
	}

	private synchronized void closeSearcher()
	{
		try {
			if (this.searcher != null) {
				this.searcher.close();
			}
			if (this.reader != null) {
				this.reader.close();
			}
		} catch (IOException e) {
		} finally {
			this.searcher = null;
			this.reader = null;
		}
	}

	private Searchable lookupRemote(String hostname)
           throws Exception {
                  return (Searchable) Naming.lookup("//"+hostname+":"+slavesInfo.get(hostname)+"/XWiki_LuceneMultiSearch");
           }

}
