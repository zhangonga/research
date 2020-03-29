package tech.zg.research.lucene.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.document.*;
import tech.zg.research.lucene.annotation.LongPointMark;
import tech.zg.research.lucene.annotation.StoreFieldMark;
import tech.zg.research.lucene.annotation.StringFieldMark;
import tech.zg.research.lucene.annotation.TextFieldMark;

/**
 * 文章
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Article {

    @LongPointMark
    @StoreFieldMark
    private Long id;

    @TextFieldMark(store = 1)
    private String title;

    @TextFieldMark(store = 0)
    private String content;

    @StringFieldMark(store = 1)
    private String author;

    @StoreFieldMark
    private String url;

    public static Article parseDoc(Document document) {
        Article article = Article.builder()
                .id(Long.valueOf(document.get("id")))
                .title(String.valueOf(document.get("title")))
                .content(String.valueOf(document.get("content")))
                .author(String.valueOf(document.get("author")))
                .url(String.valueOf(document.get("url")))
                .build();
        return article;
    }

    public Document createDoc() {
        Document document = new Document();
        document.add(new LongPoint("id", id));
        document.add(new StoredField("id", id));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("content", content, Field.Store.NO));
        document.add(new StringField("author", author, Field.Store.YES));
        document.add(new StoredField("url", url));
        return document;
    }
}
