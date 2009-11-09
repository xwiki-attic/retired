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
import java.io.IOException;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

public class GlobalIndexUpdater extends Thread
{

	private String indexDir;

	private QueryParser queryParser;

	private Analyzer analyzer;

	private IndexSearcher searcher;

   	private IndexReader reader;

	private IndexWriter writer;
	
	private SlavesInfo slavesInfo;

	private boolean exist = false;

	private IndexSearchServer searchServer;

	public GlobalIndexUpdater(String indexDir,Analyzer analyzer,SlavesInfo slavesInfo)
	{
		this.indexDir = indexDir;
		this.analyzer = analyzer;
		this.slavesInfo = slavesInfo;
	}

	public void run()
	{
		//run main loop;
		try{
			runMainLoop();
		}
		catch(Exception e)
		{
		}
	}

	public void init()
	{
		queryParser = new QueryParser("title",this.analyzer);
		//Make SlavesInfo remotely available
		//

		if(this.indexDir != null)
		{
			File f = new File(indexDir);
			if (!f.isDirectory()) {
				f.mkdirs();
				// this.needInitialBuild = true;
			}
			if (!IndexReader.indexExists(f)) {
				try{
					openWriter(true);
					addToIndex("1","1");
					writer.flush();
					closeWriter();
				}
				catch(Exception e)
				{
				}
				// this.needInitialBuild = true;
			}

		}


		

		slavesInfo.init();
		searchServer = new IndexSearchServer(slavesInfo.indexDir,
							slavesInfo.masterInfo.split(":")[0],
							slavesInfo.masterInfo.split(":")[1],
							analyzer);
		searchServer.setServiceName("slavesInfo");
		searchServer.start();

		//Document doc = new Document();
		//doc.add(new Field("temp", "temp", Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	/**
 	* Main loops which takes care of updating the global index
 	*/

	public void runMainLoop()
	{
		while(!this.exist)
		{
			Iterator it = this.slavesInfo.slavesInfo.keySet().iterator();
			while(it.hasNext())
			{
				String slave = ((String)it.next());
				Hits hits = getSlaveDocs(slave,this.slavesInfo.slavesInfo.get(slave));
				if(hits != null)
				{
					updateIndex(hits);
				}
			}
			try{
				Thread.sleep(1000 * 30);
				}
			catch(Exception e){
			}
		}
	}

	private void updateIndex(Hits hits)
	{
		try{
		openSearcher();
		//Let's Delete the doc
		for(int i=0;i<hits.length();i++)
		{
			Document doc = hits.doc(i);
			
			deleteDocFromIndex(doc);
		}
		closeSearcher();
			
		openWriter(false);
		for(int i=0;i<hits.length();i++)
		{
			//add new doc with the help of IndexData
			Document doc = hits.doc(i);
			addDocToIndex(doc);
			if(i == hits.length()-1)
			{
				slavesInfo.updateIndex(doc.get("hostname"),doc.get("timestamp"));
				searchServer.resetServer();
			}
		}

		this.writer.flush();
		closeWriter();	

		}
		catch(Exception e){
		}
	}

	private Hits getSlaveDocs(String slave, String port)
	{
		try{
			Query query = buildSlaveQuery(slave);
			MultiSearcher remotesearcher = new MultiSearcher(new Searchable[]{lookupRemote(slave,port,"XWiki_LuceneMultiSearch")});
			Hits hits = remotesearcher.search(query);
			remotesearcher.close();
			return hits;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private void addDocToIndex(Document doc) throws Exception
	{
		Document newLuceneDoc = new Document();
		addDataToLuceneDocument(newLuceneDoc,doc);
		this.writer.addDocument(newLuceneDoc);
	}

	private void deleteDocFromIndex(Document doc) throws Exception
	{
			BooleanQuery bq = new BooleanQuery();
			Query q1 = queryParser.parse("hostname:"+doc.get("hostname"));
			bq.add(q1,BooleanClause.Occur.MUST);
			TermQuery q2 = new TermQuery(new Term("_docid", doc.get("_docid")));
			bq.add(q2,BooleanClause.Occur.MUST);

			Hits hits = getHits(bq);
		
			if(hits.length() > 0)
			{
				try{
				deleteFromIndex(hits.id(0));
				}
				catch(Exception e)
				{
				}
			}
	}

	private void deleteFromIndex(int id) throws Exception
        {
                this.reader.deleteDocument(id);
        }


	private Query buildSlaveQuery(String hostname)
	{
		try{
		if(slavesInfo.getRecentTimeStamp(hostname) !=null)
		{
			Query query = queryParser.parse("hostname:"+hostname+" AND timestamp:["+
				slavesInfo.getRecentTimeStamp(hostname)+" TO 999999999999999999999]");
		return query;
		}
		else
		{
			Query query = queryParser.parse("hostname:"+hostname);
		return query;
		}
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public void cleanIndex()
	{
		while (writer != null) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		synchronized (this) {
			openWriter(true);
			closeWriter();
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
        }

	private synchronized void openSearcher()
        {
                try {
			if(this.reader==null && this.searcher==null)
			{
                        	this.reader = IndexReader.open(this.indexDir);
                        	this.searcher = new IndexSearcher(this.reader);
			}
			else if(this.reader==null || this.searcher==null)
			{
				//Either reader or searcher is open, close the both and open again.
				this.reader = null;
				this.searcher = null;
                        	this.reader = IndexReader.open(this.indexDir);
                        	this.searcher = new IndexSearcher(this.reader);
			}
			else
			{
				//Both reader and searcher are open. no need to reopen.
				return;
			}
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
	
	public Hits getHits(Query query)
	{
		try{
		if(this.searcher == null)
			openSearcher();
		Hits hits = this.searcher.search(query);
		return hits;
		}
		catch(Exception e)
		{
			return null;
		}
	}

        private Searchable lookupRemote(String ip, String port, String name)  throws Exception {
                  return (Searchable) Naming.lookup("//"+ip+":"+port+"/"+name);
           }

	private void addDataToLuceneDocument(org.apache.lucene.document.Document newLuceneDoc,
			org.apache.lucene.document.Document oldLuceneDoc)
	{
		List list = oldLuceneDoc.getFields();

		for(int i=0;i<list.size();i++)
		{
			String fieldName = ( (Field) list.get(i) ).name();
			String fieldValue = ( (Field) list.get(i) ).stringValue();
			if( fieldName.equals(IndexFields.FULLTEXT) )
			{
				newLuceneDoc.add(new Field(fieldName,
							fieldValue,
							Field.Store.NO,
							Field.Index.TOKENIZED));
			}
			else if( fieldName.equals(IndexFields.DOCUMENT_ID) || fieldName.equals(IndexFields.DOCUMENT_DATE) ||
					fieldName.equals(IndexFields.DOCUMENT_CREATIONDATE) )
			{
				newLuceneDoc.add(new Field(fieldName,
							fieldValue,
							Field.Store.YES,
							Field.Index.UN_TOKENIZED));

			}
			else if(fieldName.equals(IndexFields.TIMESTAMP))
			{
				continue;
			}
			else
			{
				newLuceneDoc.add(new Field(fieldName,
							fieldValue,
							Field.Store.YES,
							Field.Index.TOKENIZED));
			}
		}

	}

	private void addToIndex(String hostname, String timestamp) throws IOException
        {
                org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
                luceneDoc.add(new Field("hostname",hostname,Field.Store.YES,Field.Index.TOKENIZED));
                luceneDoc.add(new Field("timestamp",timestamp,Field.Store.YES,Field.Index.TOKENIZED));
                this.writer.addDocument(luceneDoc);
        }


}
