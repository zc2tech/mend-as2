package de.mendelson.util.systemevents.search;

import de.mendelson.util.systemevents.SystemEvent;
import java.io.IOException;
import java.io.StringReader;
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
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
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
 * @version $Revision: 16 $
 */
public class ServerSideEventSearch {

    private final static String TAG_PATH = "path";
    private final static String TAG_BODY = "body";
    private final static String TAG_SUBJECT = "subject";
    private final static String TAG_SEVERITY = "severity";
    private final static String TAG_ORIGIN = "origin";
    private final static String TAG_CATEGORY = "category";
    private final static String TAG_TYPE = "type";
    private final static String TAG_ID = "id";
    private final static String TAG_USER = "user";
    private final static String TAG_ORIGINHOST = "originhost";
    private final static String TAG_TIMESTAMP = "timestamp";

    private final static int MIN_TOKEN_LENGTH = 20;
    private final static int MAX_TOKEN_LENGTH = 20;

    public ServerSideEventSearch() {
    }

    /**
     * Generates a list of dates that include the start date and the end date of
     * the filter.
     */
    private List<Date> generateSearchDatesFromFilter(ServerSideEventFilter filter) {
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
     * Performs a server side search on the system events. This will create or
     * use an index for each event day first and then perform the search using
     * lucene
     *
     * @param filter The filter to filter the events
     */
    public synchronized List<SystemEvent> performSearch(ServerSideEventFilter filter) {
        DateFormat DAILY_SUBDIR_FORMAT = new SimpleDateFormat("yyyyMMdd");
        List<SystemEvent> resultList = new ArrayList<SystemEvent>();
        //create a list of dates
        List<Date> searchDateList = this.generateSearchDatesFromFilter(filter);
        //add all index reader of the date range
        try {
            List<IndexReader> indexReaderList = new ArrayList<IndexReader>();
            for (Date searchDate : searchDateList) {
                String formattedSearchTime = DAILY_SUBDIR_FORMAT.format(searchDate);
                String indexDirStr = "log/" + formattedSearchTime + "/events/index";
                boolean today = formattedSearchTime.equals(DAILY_SUBDIR_FORMAT.format(new Date()));
                //if the search date is today the index always have to recreated in a temp dir. The reason is that
                //more events are up to come for today....
                if (today) {
                    indexDirStr = "log/" + formattedSearchTime + "/events/index_tmp";
                    //this directory is useless tomorrow and then the standard index directory will be used. Anyway
                    //it makes no sense to create the index directory for todays events because then the later searches will
                    //think that the index is complete - but it is possible that more events happen today
                }
                //skip the index generation process for this date if there is no event directory available
                if (!Files.exists(Paths.get("log", formattedSearchTime, "events"))) {
                    continue;
                }
                try {
                    if (today) {
                        //if the search date is today the index always have to recreated - new events will happen
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
            IndexReader[] indexReaderArray = (IndexReader[]) indexReaderList.toArray(
                    new IndexReader[indexReaderList.size()]);
            //setup multiple index reader - one for each date. The search will be performed over all index files
            //as the multireader merges the index files of the search days
            try (MultiReader multiReader = new MultiReader(indexReaderArray, true)) {
                IndexSearcher searcher = new IndexSearcher(multiReader);
                Query query = this.buildQueryFromFilter(filter);
                SortField timestampSortField = new SortedNumericSortField(TAG_TIMESTAMP, SortField.Type.LONG, true);
                Sort sortByTimestamp = new Sort(timestampSortField);
                //finally perform the search
                TopDocs hits = searcher.search(query, filter.getMaxResults(), sortByTimestamp);
                if (hits.totalHits.value > 0) {
                    for (ScoreDoc scoreDoc : hits.scoreDocs) {
                        Document doc = multiReader.document(scoreDoc.doc);
                        try {
                            resultList.add(this.generateEventFromSingleSearchResult(doc));
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
        Collections.reverse(resultList);
        return (resultList);
    }

    /**
     * If a hit occurred this will create an event object from this hit without
     * parsing the event file again - should be faster
     *
     * @param document
     * @return
     */
    private SystemEvent generateEventFromSingleSearchResult(Document document) {
        SystemEvent event = new SystemEvent(
                document.getField(TAG_SEVERITY).numericValue().intValue(),
                document.getField(TAG_ORIGIN).numericValue().intValue(),
                document.getField(TAG_TYPE).numericValue().intValue());
        event.setBody(document.getField(TAG_BODY).stringValue());
        event.setSubject(document.getField(TAG_SUBJECT).stringValue());
        event.setId(document.getField(TAG_ID).stringValue());
        event.setUser(document.getField(TAG_USER).stringValue());
        event.setProcessOriginHost(document.getField(TAG_ORIGINHOST).stringValue());
        event.setTimestamp(document.getField(TAG_TIMESTAMP).numericValue().longValue());
        return (event);
    }

    private Query buildQueryFromFilter(ServerSideEventFilter filter) {
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        boolean freeTextEntered
                = (filter.getSubjectSearchText() != null && !filter.getSubjectSearchText().trim().isEmpty())
                || (filter.getBodySearchText() != null && !filter.getBodySearchText().trim().isEmpty())
                || (filter.getSearchEventid() != null && !filter.getSearchEventid().trim().isEmpty());
        if (freeTextEntered) {
            BooleanQuery.Builder freeTextSearchBuilder = new BooleanQuery.Builder();
            if (filter.getSubjectSearchText() != null && !filter.getSubjectSearchText().trim().isEmpty()) {
                String subjectSearchText = filter.getSubjectSearchText();
                //cut free text search term for performance reasons
                if (subjectSearchText.length() > MAX_TOKEN_LENGTH) {
                    subjectSearchText = subjectSearchText.substring(0, MAX_TOKEN_LENGTH);
                }
                if (!subjectSearchText.endsWith("*")) {
                    subjectSearchText = subjectSearchText + "*";
                }
                if (!subjectSearchText.startsWith("*")) {
                    subjectSearchText = "*" + subjectSearchText;
                }
                Query subquerySubject = new WildcardQuery(new Term(TAG_SUBJECT, subjectSearchText));
                freeTextSearchBuilder = freeTextSearchBuilder.add(subquerySubject, BooleanClause.Occur.SHOULD);
            }
            if (filter.getBodySearchText() != null && !filter.getBodySearchText().trim().isEmpty()) {
                String bodySearchText = filter.getBodySearchText();
                //cut free text search term for performance reasons
                if (bodySearchText.length() > MAX_TOKEN_LENGTH) {
                    bodySearchText = bodySearchText.substring(0, MAX_TOKEN_LENGTH);
                }
                if (!bodySearchText.endsWith("*")) {
                    bodySearchText = bodySearchText + "*";
                }
                if (!bodySearchText.startsWith("*")) {
                    bodySearchText = "*" + bodySearchText;
                }
                Query subqueryBody = new WildcardQuery(new Term(TAG_BODY, bodySearchText));
                freeTextSearchBuilder = freeTextSearchBuilder.add(subqueryBody, BooleanClause.Occur.SHOULD);
            }
            if (filter.getSearchEventid() != null && !filter.getSearchEventid().trim().isEmpty()) {
                Query subqueryEventId = new TermQuery(new Term(TAG_ID, filter.getSearchEventid()));
                freeTextSearchBuilder = freeTextSearchBuilder.add(subqueryEventId, BooleanClause.Occur.SHOULD);
            }
            Query freeTextQuery = freeTextSearchBuilder.build();
            queryBuilder.add(freeTextQuery, BooleanClause.Occur.MUST);
        }
        Query subquery = IntPoint.newExactQuery(TAG_SEVERITY, SystemEvent.SEVERITY_ERROR);
        if (!filter.getAcceptSeverityError()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        subquery = IntPoint.newExactQuery(TAG_SEVERITY, SystemEvent.SEVERITY_WARNING);
        if (!filter.getAcceptSeverityWarning()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        subquery = IntPoint.newExactQuery(TAG_SEVERITY, SystemEvent.SEVERITY_INFO);
        if (!filter.getAcceptSeverityInfo()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        subquery = IntPoint.newExactQuery(TAG_ORIGIN, SystemEvent.ORIGIN_SYSTEM);
        if (!filter.getAcceptOriginSystem()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        subquery = IntPoint.newExactQuery(TAG_ORIGIN, SystemEvent.ORIGIN_TRANSACTION);
        if (!filter.getAcceptOriginTransaction()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        subquery = IntPoint.newExactQuery(TAG_ORIGIN, SystemEvent.ORIGIN_USER);
        if (!filter.getAcceptOriginUser()) {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST_NOT);
        } else {
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.SHOULD);
        }
        if (filter.getAcceptCategory() != -1) {
            subquery = IntPoint.newExactQuery(TAG_CATEGORY, filter.getAcceptCategory());
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST);
        }
        if (filter.getAcceptType() != -1) {
            subquery = IntPoint.newExactQuery(TAG_TYPE, filter.getAcceptType());
            queryBuilder = queryBuilder.add(subquery, BooleanClause.Occur.MUST);
        }
        BooleanQuery query = queryBuilder.build();
        return (query);
    }

    /**
     * Recreates a search index for system events in the passed directory for
     * the passed event date
     */
    private void recreateIndex(Date date, String indexDirStr) throws IOException {
        Path indexDirPath = Paths.get(indexDirStr);
        //generate index
        FSDirectory indexDir = FSDirectory.open(indexDirPath);
        IndexWriterConfig config = new IndexWriterConfig();
        try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
            DateFormat DAILY_SUBDIR_FORMAT = new SimpleDateFormat("yyyyMMdd");
            Path storageDir = Paths.get("log",
                    DAILY_SUBDIR_FORMAT.format(date),
                    "events");
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(storageDir)) {
                indexWriter.deleteAll();
                for (Path foundEventFile : dirStream) {
                    if (Files.isDirectory(foundEventFile)) {
                        continue;
                    }
                    try {
                        SystemEvent event = SystemEvent.parse(foundEventFile);
                        Document luceneDocument = new Document();
                        luceneDocument.add(new StringField(TAG_PATH, foundEventFile.toAbsolutePath().toString(), Field.Store.YES));
                        //subject - tokenize for free text search
                        Tokenizer subjectTokenizer = new NGramTokenizer(MIN_TOKEN_LENGTH, MAX_TOKEN_LENGTH);
                        String subjectToTokenize = event.getSubject().trim();
                        while (subjectToTokenize.length() < MIN_TOKEN_LENGTH) {
                            subjectToTokenize = subjectToTokenize + " ";
                        }
                        subjectTokenizer.setReader(new StringReader(subjectToTokenize));
                        luceneDocument.add(new TextField(TAG_SUBJECT, subjectTokenizer));
                        luceneDocument.add(new StoredField(TAG_SUBJECT, event.getSubject()));
                        //body - tokenize for free text search
                        Tokenizer bodyTokenizer = new NGramTokenizer(MIN_TOKEN_LENGTH, MAX_TOKEN_LENGTH);
                        String bodyToTokenize = event.getBody().trim();
                        while (bodyToTokenize.length() < MIN_TOKEN_LENGTH) {
                            bodyToTokenize = bodyToTokenize + " ";
                        }
                        bodyTokenizer.setReader(new StringReader(bodyToTokenize));
                        luceneDocument.add(new TextField(TAG_BODY, bodyTokenizer));
                        luceneDocument.add(new StoredField(TAG_BODY, event.getBody()));
                        luceneDocument.add(new IntPoint(TAG_SEVERITY, event.getSeverity()));
                        luceneDocument.add(new StoredField(TAG_SEVERITY, event.getSeverity()));
                        luceneDocument.add(new IntPoint(TAG_ORIGIN, event.getOrigin()));
                        luceneDocument.add(new StoredField(TAG_ORIGIN, event.getOrigin()));
                        luceneDocument.add(new IntPoint(TAG_CATEGORY, event.getCategory()));
                        luceneDocument.add(new StoredField(TAG_CATEGORY, event.getCategory()));
                        luceneDocument.add(new IntPoint(TAG_TYPE, event.getType()));
                        luceneDocument.add(new StoredField(TAG_TYPE, event.getType()));
                        //search for full event id only - use Stringfield and not TextField
                        luceneDocument.add(new StringField(TAG_ID, event.getId(), Field.Store.YES));
                        luceneDocument.add(new TextField(TAG_USER, event.getUser(), Field.Store.YES));
                        luceneDocument.add(new TextField(TAG_ORIGINHOST, event.getProcessOriginHost(), Field.Store.YES));
                        luceneDocument.add(new LongPoint(TAG_TIMESTAMP, event.getTimestamp()));
                        luceneDocument.add(new StoredField(TAG_TIMESTAMP, event.getTimestamp()));
                        luceneDocument.add(new SortedNumericDocValuesField(TAG_TIMESTAMP, event.getTimestamp()));
                        indexWriter.addDocument(luceneDocument);
                    } catch (Throwable e) {
                        //ignore - it is no system event that has been found
                        e.printStackTrace();
                    }
                }
            }
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
