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
        luceneDemo.search("title", "云计算");
    }
}
