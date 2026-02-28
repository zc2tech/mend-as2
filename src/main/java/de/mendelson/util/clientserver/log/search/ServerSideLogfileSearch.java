//$Header: /as2/de/mendelson/util/clientserver/log/search/ServerSideLogfileSearch.java 12    20/02/25 13:41 Heller $
package de.mendelson.util.clientserver.log.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Contains all required methods for a server side system event search - either
 * by state, type, category or also free text search
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public abstract class ServerSideLogfileSearch {

    private final int minHeaderParameter;

    protected ServerSideLogfileSearch(int minHeaderParameter) {
        this.minHeaderParameter = minHeaderParameter;
    }

    /**
     * Generates a list of dates that include the start date and the end date of
     * the filter.
     */
    private List<Date> generateSearchDatesFromFilter(ServerSideLogfileFilter filter) {
        List<Date> searchDateList = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(filter.getStartDate());
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        searchDateList.add(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        while (calendar.getTime().before(new Date(filter.getEndDate()))) {
            searchDateList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return (searchDateList);
    }

    /**
     * Performs a server side search on log files. This will create or use an
     * index for each event day first and then perform the search using lucene
     *
     * @param filter The filter to filter the log lines
     */
    public synchronized List<Logline> performSearch(ServerSideLogfileFilter filter) {
        //dont use DateTimeFormatter here - this class does not like references to calendars
        final DateFormat DAILY_SUB_DIR_FORMAT = new SimpleDateFormat("yyyyMMdd");
        List<Logline> resultList = new ArrayList<Logline>();
        //create a list of dates
        List<Date> searchDateList = this.generateSearchDatesFromFilter(filter);
        //add all index reader of the date range
        try {
            List<IndexReader> indexReaderList = new ArrayList<IndexReader>();
            for (Date searchDate : searchDateList) {
                String formattedSearchTime = DAILY_SUB_DIR_FORMAT.format(searchDate);
                String indexDirStr = "log/" + formattedSearchTime + "/index";
                boolean today = DAILY_SUB_DIR_FORMAT.format(new Date()).equals(
                        formattedSearchTime);
                //if the search date is today the index always have to recreated in a temp dir. The reason is that
                //more log entries are up to come for today....
                if (today) {
                    indexDirStr = "log/" + formattedSearchTime + "/index_tmp";
                    //this directory is useless tomorrow and then the standard index directory will be used. Anyway
                    //it makes no sense to create the index directory for todays log lines because then the later searches will
                    //think that the index is complete - but it is possible that more log lines happen today
                }
                //skip the index generation process for this date if there is no log directory available for the day
                if (!Files.exists(Paths.get("log", formattedSearchTime))) {
                    continue;
                }
                try {
                    if (today) {
                        //if the search date is today the index always have to recreated - new log lines will happen
                        this.recreateIndex(searchDate, indexDirStr);
                    }
                    //read the index of the search day
                    FSDirectory indexDir = FSDirectory.open(Paths.get(indexDirStr));
                    IndexReader indexReader = DirectoryReader.open(indexDir);
                    indexReaderList.add(indexReader);
                } catch (Exception e) {
                    //if there is an error the index must be recreated..
                    this.recreateIndex(searchDate, indexDirStr);
                    try {
                        FSDirectory indexDir = FSDirectory.open(Paths.get(indexDirStr));
                        IndexReader indexReader = DirectoryReader.open(indexDir);
                        indexReaderList.add(indexReader);
                    } catch (Exception ex) {
                        //ok the system is unable to create/read the index - ignore this, there will be no search
                        //result for this day
                    }
                }
            }
            IndexReader[] indexReaderArray = (IndexReader[]) indexReaderList.toArray(new IndexReader[indexReaderList.size()]);
            //setup multiple index reader - one for each date. The search will be performed over all index files
            //as the multireader merges the index files of the search days
            try (MultiReader multiReader = new MultiReader(indexReaderArray, true)) {
                IndexSearcher searcher = new IndexSearcher(multiReader);
                Query query = this.buildQueryFromFilter(filter);
                SortField msSortField = new SortField(Logline.KEY_MILLISECS, SortField.Type.STRING, true);
                Sort sortByMillisecs = new Sort(msSortField);
                //finally perform the search
                TopDocs hits = searcher.search(query, filter.getMaxResults(), sortByMillisecs);
                if (hits.totalHits.value > 0) {
                    for (ScoreDoc scoreDoc : hits.scoreDocs) {
                        Document doc = multiReader.document(scoreDoc.doc);
                        try {
                            resultList.add(this.generateLoglineFromSingleSearchResult(doc));
                        } catch (Throwable e) {
                            //ignore this - it is possible that a corrupted index prevent the
                            //regeneration of the object
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //sort by millisecs and if they are equal sort by sequence number
        Collections.sort(resultList);
        return (resultList);
    }

    protected abstract Logline generateLoglineFromSingleSearchResult(Document document);

    protected abstract Query buildQueryFromFilter(ServerSideLogfileFilter filter);

    protected abstract Logline instanciateLogline(String[] headerValues, String logMessage);

    /**
     * Recreates a search index for log lines in the passed directory for the
     * passed event date
     */
    private void recreateIndex(Date date, String indexDirStr) throws IOException {
        //dont use DateTimeFormatter here - this class does not like references to calendars
        final DateFormat DAILY_SUB_DIR_FORMAT = new SimpleDateFormat("yyyyMMdd");
        int logFileCount = 0;
        Path indexDirPath = Paths.get(indexDirStr);
        //generate index
        FSDirectory indexDir = FSDirectory.open(indexDirPath);
        IndexWriterConfig config = new IndexWriterConfig();
        try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
            Path storageDir = Paths.get("log", DAILY_SUB_DIR_FORMAT.format(date));
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(storageDir)) {
                indexWriter.deleteAll();
                for (Path foundLogfile : dirStream) {
                    if (Files.isDirectory(foundLogfile)) {
                        continue;
                    }
                    logFileCount++;
                    try (BufferedReader reader = Files.newBufferedReader(foundLogfile)) {
                        String line = "";
                        while (line != null) {
                            line = reader.readLine();
                            try {
                                if (line != null && !line.isEmpty() && line.startsWith("[")) {
                                    int headerEndIndex = line.indexOf("]");
                                    if (headerEndIndex > 0) {
                                        String header = line.substring(1, headerEndIndex);
                                        String logMessage = line.substring(headerEndIndex + 1);
                                        String[] headerValues = header.split(",");
                                        if (headerValues.length >= this.minHeaderParameter) {
                                            Logline lineInstance = this.instanciateLogline(headerValues, logMessage);
                                            Document luceneDocument = lineInstance.generateLuceneDocument();
                                            lineInstance.addAdditionalFieldsToDocument(luceneDocument);
                                            indexWriter.addDocument(luceneDocument);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        //ignore - it is no log line in the right format that has been found
                        e.printStackTrace();
                    }
                }
            }
            //finally rewrite the index
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
