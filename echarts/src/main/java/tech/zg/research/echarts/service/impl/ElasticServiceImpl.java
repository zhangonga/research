package tech.zg.research.echarts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import tech.zg.research.echarts.bean.DocBean;
import tech.zg.research.echarts.dao.ElasticRepository;
import tech.zg.research.echarts.service.ElasticService;

import java.util.List;

/**
 * elastic 测试业务层
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Service("elasticService")
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ElasticRepository elasticRepository;

    public void createIndex() {
        elasticsearchRestTemplate.createIndex(DocBean.class);
    }

    public void save(List<DocBean> list) {
        elasticRepository.saveAll(list);
    }

    public Iterable<DocBean> findAll() {
        return elasticRepository.findAll();
    }
}
