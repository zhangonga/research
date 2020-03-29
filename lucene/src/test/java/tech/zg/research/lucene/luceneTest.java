package tech.zg.research.lucene;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import tech.zg.research.lucene.bean.Article;

import java.io.IOException;

public class luceneTest {

    @Test
    public void testCreate() throws Exception {
        LuceneDemo luceneDemo = new LuceneDemo();
        Article article = new Article();
        article.setId(1L);
        article.setAuthor("张三");
        article.setTitle("云计算");
        article.setContent("这是一个云计算测试程序，这是一段云计算测试");
        article.setUrl("baidu.com");
        luceneDemo.create(article);
    }

    @Test
    public void testSearch() throws IOException, ParseException {
        LuceneDemo luceneDemo = new LuceneDemo();
        //luceneDemo.search("title", "云计算");
        luceneDemo.search("title", "title:云 OR title:云计算");
        luceneDemo.search("title", "title:云 AND title:云计算");
    }

    @Test
    public void testSearchAll() throws IOException, ParseException {
        LuceneDemo luceneDemo = new LuceneDemo();
        luceneDemo.matchAll();
    }

    @Test
    public void testMultiField() throws IOException, ParseException {
        LuceneDemo luceneDemo = new LuceneDemo();
        String[] fields = {"author", "title"};
        luceneDemo.multiFieldSearch("云计算", fields);
    }

    @Test
    public void testBoolQuery() throws IOException, ParseException {
        LuceneDemo luceneDemo = new LuceneDemo();
        String[] fields = {"author", "title"};
        String[] values = {"张三", "云"};
        luceneDemo.boolQuery(fields, values);
    }

    @Test
    public void testDelete() throws IOException, ParseException {
        LuceneDemo luceneDemo = new LuceneDemo();
        luceneDemo.delete("title", "云计算");
    }
}
