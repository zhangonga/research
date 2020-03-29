package tech.zg.research.lucene;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;
import tech.zg.research.lucene.bean.Article;
import tech.zg.research.lucene.common.Constant;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * lucene demo
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Slf4j
public class LuceneDemo {

    /**
     * 测试创建
     * LongPoint long 类型的数据，建立索引
     * StoredField 不分词，不建立索引，文档中存储
     * TextField 分词，建立索引，根据Store.YES/NO 是否存储
     * StringField 不分词，建立索引，根据Store.YES/NO 是否存储
     *
     * @param article
     * @author: 张弓
     * @version V1.0.0
     */
    public void create(Article article) throws Exception {
        // 打开文件系统路径
        FSDirectory fsDirectory = FSDirectory.open(Paths.get(Constant.INDEX_PATH));
        // 创建标准分词器
        //Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        // 写入索引的配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        // 指定写入索引的目录和配置
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);
        Document document = article.createDoc();
        indexWriter.addDocument(document);
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * term 查询，条件不分词
     * 普通的分词查询
     * 多条件查询
     * bool 查询
     * range 查询（数字类型）
     * Exact 查询（数字类型）
     * 查询所有
     *
     * @param query
     * @param queryStr
     * @throws IOException
     * @throws ParseException
     */
    @SuppressWarnings("Duplicates")
    public void search(String query, String queryStr) throws IOException, ParseException {
        //Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.INDEX_PATH)));
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        // 创建一个查询条件解析器
        QueryParser parser = new QueryParser(query, analyzer);
        // 对查询条件进行解析
        Query queryObject = parser.parse(queryStr);
        // 在索引库中进行查找
        TopDocs topDocs = indexSearcher.search(queryObject, 10);

        // 获取查找到的文档ID和得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            Article article = Article.parseDoc(document);
            log.info("输出document结果-{}", JSON.toJSONString(document));
            log.info("输出article结果-{}", JSON.toJSONString(article));
        }
    }

    @SuppressWarnings("Duplicates")
    public void matchAll() throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.INDEX_PATH)));
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            Article article = Article.parseDoc(document);
            log.info("输出document结果-{}", JSON.toJSONString(document));
            log.info("输出article结果-{}", JSON.toJSONString(article));
        }
    }

    @SuppressWarnings("Duplicates")
    public void multiFieldSearch(String queryStr, String... fields) throws IOException, ParseException {
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.INDEX_PATH)));
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query = queryParser.parse(queryStr);
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            Article article = Article.parseDoc(document);
            log.info("输出document结果-{}", JSON.toJSONString(document));
            log.info("输出article结果-{}", JSON.toJSONString(article));
        }
    }

    @SuppressWarnings("Duplicates")
    public void boolQuery(String[] query, String[] queryStr) throws IOException {
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.INDEX_PATH)));
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        if (query == null || query.length < 2 || queryStr == null || queryStr.length < 2) {
            return;
        }
        Query query1 = new TermQuery(new Term(query[0], queryStr[0]));
        Query query2 = new TermQuery(new Term(query[1], queryStr[1]));
        BooleanClause booleanClause1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
        BooleanClause booleanClause2 = new BooleanClause(query2, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = new BooleanQuery.Builder().add(booleanClause1).add(booleanClause2).build();

        TopDocs topDocs = indexSearcher.search(booleanQuery, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            Article article = Article.parseDoc(document);
            log.info("输出document结果-{}", JSON.toJSONString(document));
            log.info("输出article结果-{}", JSON.toJSONString(article));
        }
    }

    public void delete(String delete, String deleteStr) throws IOException, ParseException {
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        FSDirectory fsDirectory = FSDirectory.open(Paths.get(Constant.INDEX_PATH));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);

        // 通过 term 删除
        /*indexWriter.deleteDocuments(new Term(delete, deleteStr));*/

        // 通过 query 删除
        /*QueryParser queryParser = new QueryParser(delete, analyzer);
        Query query = queryParser.parse(deleteStr);
        indexWriter.deleteDocuments(query);*/

        // 条件删除
        Query query = LongPoint.newRangeQuery("id", 0L, 10L);
        // Query query = LongPoint.newExactQuery("id", 0L);
        indexWriter.deleteDocuments(query);

        indexWriter.commit();
        indexWriter.close();
    }

    public void update() throws IOException {
        Analyzer analyzer = new IKAnalyzer(Boolean.TRUE);
        FSDirectory fsDirectory = FSDirectory.open(Paths.get(Constant.INDEX_PATH));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);

    }
}
