package tech.zg.research.echarts.service;

import tech.zg.research.echarts.bean.DocBean;

import java.util.List;

/**
 * elastic 测试业务层
 *
 * @version V1.0.0
 * @author: 张弓
 */
public interface ElasticService {

    void createIndex();

    void save(List<DocBean> list);

    Iterable<DocBean> findAll();
}
