package tech.zg.research.echarts.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.zg.research.echarts.bean.HistogramData;

import java.util.HashMap;
import java.util.Map;

/**
 * ECharts demo
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Controller
public class EChartsDemoController {

    @GetMapping("/histogram")
    public String histogramPage() {
        return "echarts/histogram";
    }

    @GetMapping("/histogramData")
    @ResponseBody
    public String histogramData() {

        Map<String, Integer> clsAndValue = new HashMap<String, Integer>();
        clsAndValue.put("衬衫", 9);
        clsAndValue.put("羊毛衫", 20);
        clsAndValue.put("雪纺衫", 36);
        clsAndValue.put("裤子", 10);
        clsAndValue.put("高跟鞋", 10);
        clsAndValue.put("袜子", 20);

        HistogramData histogramData = HistogramData.builder()
                .title("销量demo").build();
        histogramData.setClsAndValue(clsAndValue);

        return JSON.toJSONString(histogramData);
    }
}
