package tech.zg.research.lucene;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
        Analyzer analyzer = new IKAnalyzer();
        // 写入索引的配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        // 指定写入索引的目录和配置
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);
        Document document = article.createDoc();
        indexWriter.addDocument(document);
        indexWriter.commit();
        indexWriter.close();
    }

    public void search(String query, String queryStr) throws IOException, ParseException {
        //Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
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
}
