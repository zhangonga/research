package tech.zg.research.jvm.mateSpace;

import javax.management.MXBean;
import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据区测试
 * -XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=80m（JDK8）
 *
 * @author zg
 */
@MXBean
public class MateSpaceTest {

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
        URL url = new File("D:/workSpace/research/jvm/target/classes/tech/zg/research/jvm/heap").toURI().toURL();
        URL[] urls = {url};
        ClassLoadingMXBean loadingMXBean = ManagementFactory.getClassLoadingMXBean();
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
        while (true) {
            ClassLoader classLoader = new URLClassLoader(urls);
            classLoaders.add(classLoader);
            classLoader.loadClass("OutOfMemoryTest");
            System.out.println("total: " + loadingMXBean.getTotalLoadedClassCount());
            System.out.println("active: " + loadingMXBean.getLoadedClassCount());
            System.out.println("unloaded: " + loadingMXBean.getUnloadedClassCount());
        }
    }
}
