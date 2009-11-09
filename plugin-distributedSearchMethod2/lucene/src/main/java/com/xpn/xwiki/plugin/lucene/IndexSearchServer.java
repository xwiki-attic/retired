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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.RemoteSearchable;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.ArrayList;

public class IndexSearchServer extends Thread
{
    private String port = null;
    
    private String hostname = null;

    private String indexDirs = null;

    private String serviceName = null;

    private Analyzer analyzer;

    private Searcher[] searchables;

    private Searcher multiSearcher;

    private RemoteSearchable multiImpl;

    public IndexSearchServer(String indexDirs, String hostname, String port, Analyzer analyzer)
    {
	this.indexDirs = indexDirs;
	this.hostname = hostname;
	this.port = port;
	this.analyzer = analyzer;
    }

    public void run()
    {
	try{
	this.searchables = createSearchers(this.indexDirs);
	
	LocateRegistry.createRegistry(Integer.parseInt(this.port));

	this.multiSearcher = new MultiSearcher(this.searchables);
	
 	this.multiImpl = new RemoteSearchable(this.multiSearcher);

	Naming.rebind("//"+this.hostname+":"+this.port+"/"+this.serviceName,this.multiImpl);

	System.out.println("Lucene Search Server started with the name "+ this.serviceName);
	}
	catch(Exception e){
                        System.out.println(e.toString());
                }
	
    }

    private Searcher[] createSearchers(String indexDirs) throws Exception
    {
        String[] dirs = StringUtils.split(indexDirs, ",");
        List<IndexSearcher> searchersList = new ArrayList<IndexSearcher>();
        for (int i = 0; i < dirs.length; i++) {
            try {
                if (!IndexReader.indexExists(dirs[i])) {
                    // If there's no index there, create an empty one; otherwise the reader
                    // constructor will throw an exception and fail to initialize
                    new IndexWriter(dirs[i], analyzer).close();
                }
                IndexReader reader = IndexReader.open(dirs[i]);
                searchersList.add(new IndexSearcher(reader));
            } catch (IOException e) {
               // LOG.error("cannot open index " + dirs[i], e);
            }
        }

        return searchersList.toArray(new Searcher[searchersList.size()]);
    }

   private void closeSearcher(Searcher[] searchables)
   {
	try{
		for(int i=0; i<searchables.length; i++)
		{
			try{
				((IndexSearcher)searchables[i]).getIndexReader().close();
			}
			catch(Exception e)
			{
				System.out.println("Unable to close IndexReader ");
			}
			try{
				searchables[i].close();
			}
			catch(Exception e)
			{
				System.out.println("Unable to close IndexSearcher ");
			}
		}
	}
	catch(Exception e){
	}
	
   }

   /**
   * Restart the search server, to use the updated index.
   */
   public void resetServer()
   {
      try{

	closeSearcher(this.searchables);
	this.multiSearcher.close();
	this.multiImpl.close();

      this.searchables = createSearchers(this.indexDirs);
      
      this.multiSearcher = new MultiSearcher(this.searchables);
      
      this.multiImpl = new RemoteSearchable(this.multiSearcher);

      //Naming.unbind("//"+this.hostname+":"+this.port+"/XWiki_LuceneMultiSearch");
      Naming.unbind("//"+this.hostname+":"+this.port+"/"+this.serviceName);
      Naming.rebind("//"+this.hostname+":"+this.port+"/"+this.serviceName,multiImpl);

      }
      catch(Exception e){
                      System.out.println(e.toString());
              }
      
   }

   public void setServiceName(String serviceName)
   {
	this.serviceName = serviceName;
   }

}
