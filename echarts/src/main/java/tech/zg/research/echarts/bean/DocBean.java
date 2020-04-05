package tech.zg.research.echarts.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * elasticsearch 测试实体类
 *
 * @author zhanggong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(indexName = "ems", type = "_doc", shards = 3, replicas = 1)
public class DocBean {

    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String firstCode;
    @Field(type = FieldType.Keyword)
    private String secondCode;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Integer)
    private Integer type;
}
