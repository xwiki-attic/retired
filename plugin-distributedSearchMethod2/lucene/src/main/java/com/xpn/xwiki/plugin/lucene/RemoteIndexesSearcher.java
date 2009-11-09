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
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Sort;

import java.io.IOException;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class RemoteIndexesSearcher
{

	private static HashMap searcherCache = new HashMap();

	private final String SlaveList;

        public int slaveListLength;

        public static int currentSlaveListLength;

	public static String searcherName ;

	public RemoteIndexesSearcher(String slavelist, String searcherName)
	{
		this.SlaveList = slavelist;
		this.searcherName = searcherName;
		setSlaveListLength(this.SlaveList);
		createSearcher(this.SlaveList);
		ReInstantiate reInstantiate = new ReInstantiate(this);
		reInstantiate.start();
	}


        private void setSlaveListLength(String slavelist)
	{
		String[] slaves = StringUtils.split(slavelist, ",");
		this.slaveListLength = slaves.length;
	}


	/**
	* search multiple remote index <strong> parallel  </strong>.
	*/
	public Hits search(Query query, Sort sort) throws IOException
	{
		ParallelMultiSearcher searcher =
                                (ParallelMultiSearcher) searcherCache.get(this.searcherName);

		if(searcher == null)
		{
			Searchable []remoteSearchables = createRemoteSearchers(this.SlaveList);
			searcher = new ParallelMultiSearcher(remoteSearchables);
			searcherCache.put(this.searcherName, searcher);
		}

		Hits hits;

		try{
			hits = (sort == null) ? searcher.search(query) : searcher.search(query, sort);
		}
		catch(Exception e)
		{
			resetSearcher(this.searcherName);
			hits = search(query, sort);
		}
		return hits;
		
	}

        private void createSearcher(String SlaveList)
	{
		try{
		Searchable []remoteSearchables = createRemoteSearchers(this.SlaveList);
                ParallelMultiSearcher searcher = new ParallelMultiSearcher(remoteSearchables);
                searcherCache.put(this.searcherName, searcher);
		}catch(Exception e){}
	}

	/**
	* Creates Searchables over remotely available indexes.
	*/
	private Searchable[] createRemoteSearchers(String slavelist)
	{
		String[] slaves = StringUtils.split(slavelist, ",");
		List<Searchable> remoteSearcherList = new ArrayList<Searchable>();
		int count =0;
		for (int i = 0; i < slaves.length; i++) 
		{
			try{
				remoteSearcherList.add(lookupRemote(slaves[i],"XWiki_LuceneMultiSearch"));
				count++;
			}
			catch(Exception e) {}
		}
		this.currentSlaveListLength = count;
		return remoteSearcherList.toArray(new Searchable[remoteSearcherList.size()]);
	}

	private Searchable lookupRemote(String IPandPort, String name)
                throws Exception {
                        return (Searchable) Naming.lookup("//"+IPandPort+"/"+name);
                }

	/**
	* Redefines the searcher used in search().
	* Redefining search is required/called when one the remotely available is index is updated or one of the slave is down.
	*/
	public void resetSearcher(String searcherName) 
        {
                try{
                  Searchable []remoteSearchables = createRemoteSearchers(this.SlaveList);
                  ParallelMultiSearcher searcher = new ParallelMultiSearcher(remoteSearchables);
		  if( searcherCache.containsKey(((String)searcherName)) )
			((ParallelMultiSearcher)searcherCache.get(searcherName)).close();
                  searcherCache.put(searcherName, searcher);
                }
                catch(Exception e){}
        }

	/**
	* A Thread which Redefines the searcher for every 1 minute.
	*/
        private class ReInstantiate extends Thread
	{
		private RemoteIndexesSearcher remoteIndexesSearcher;

		private final int resetSearcherInterval = 30;

		private int count = 0;

		public ReInstantiate(RemoteIndexesSearcher remoteIndexesSearcher)
		{
			this.remoteIndexesSearcher = remoteIndexesSearcher;
		}
		
		public void run()
		{
			while(true)
			{
				try{
				Thread.sleep(this.resetSearcherInterval * 1000);
				this.count++;
				if(this.count == 10000)
					this.count = 0;

				//if( this.count % 2 ==1 )
				if( this.remoteIndexesSearcher.currentSlaveListLength == this.remoteIndexesSearcher.slaveListLength)
				{
					continue;
				}
				else
				{
					this.remoteIndexesSearcher.resetSearcher(this.remoteIndexesSearcher.searcherName);
				}
				}catch(Exception e){}
			}
		}
	}
}
