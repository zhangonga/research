package tech.zg.research.echarts.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import tech.zg.research.echarts.bean.DocBean;

public interface ElasticRepository extends ElasticsearchRepository<DocBean, Long> {
}
