package tech.zg.research.echarts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/map/baidu")
public class BaiduMapController {

    @GetMapping("/page")
    public String page() {
        return "map/baidu";
    }
}
