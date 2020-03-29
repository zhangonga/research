package tech.zg.research.jvm.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试堆溢出
 * 启动加参数
 * -verbose:gc -Xms20m -Xmx=20m -xmn=10m -XX:+PrintGCDetails -XX:SurvivorRatio=8
 * -Xms20m -Xmx20m -Xmn10m -XX:+HeapDumpOnOutOfMemoryError
 * 
 * @author : zhanggong
 * @date : 2019/11/27
 * @version : 1.0.0
 */
public class OutOfMemoryTest {

    public static void main(String[] args) {
        List<OutOfMemoryTest> outOfMemoryTests = new ArrayList<OutOfMemoryTest>();
        while (true){
            outOfMemoryTests.add(new OutOfMemoryTest());
        }
    }
}
