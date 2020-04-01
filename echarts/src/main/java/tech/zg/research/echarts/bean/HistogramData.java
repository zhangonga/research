package tech.zg.research.echarts.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class HistogramData {
    String title;
    List<HistogramDataValue> classAndValue;

    public void setClsAndValue(Map<String, Integer> clsAndValue) {
        Set<Map.Entry<String, Integer>> kvSet = clsAndValue.entrySet();
        if (CollectionUtils.isNotEmpty(kvSet)) {
            for (Map.Entry<String, Integer> entry : kvSet) {
                HistogramDataValue histogramDataValue = new HistogramDataValue(entry.getKey(), entry.getValue());
                if (classAndValue == null) {
                    classAndValue = new ArrayList<HistogramDataValue>();
                }
                classAndValue.add(histogramDataValue);
            }
        }
    }

    @Data
    @AllArgsConstructor
    public class HistogramDataValue {
        private String name;
        private Integer value;
    }
}
