package tech.zg.research.echarts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tech.zg.research.echarts.bean.DocBean;
import tech.zg.research.echarts.service.ElasticService;

import java.util.ArrayList;
import java.util.List;

/**
 * elastic 测试接口
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Slf4j
@RestController
@RequestMapping("/elastic")
public class ElasticController {

    @Autowired
    private ElasticService elasticService;

    @GetMapping("/init")
    public void init() {
        elasticService.createIndex();
        List<DocBean> list = new ArrayList<DocBean>();
        list.add(new DocBean(1L, "XX0191", "XX8061", "下一代沃尔沃XC90将国产 门槛进一步下探", 1));
        list.add(new DocBean(2L, "XX0192", "XX8062", "刘强东卸任京东法定代表人、执行董事", 1));
        list.add(new DocBean(3L, "XX0193", "XX8063", "小鹏在云南成立网约车公司 布局移动出行", 2));
        list.add(new DocBean(4L, "XX0194", "XX8064", "能与汉兰达一战？全新一代索兰托静态体验", 2));
        elasticService.save(list);
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<DocBean> queryAll() {
        return elasticService.findAll();
    }
}
