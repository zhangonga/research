package tech.zg.research.jvm.classLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类加载器测试
 *
 * @version V1.0.0
 * @author: 张弓
 * @date: 2019-12-26 15:02
 */
public class ClassLoaderTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        ClassLoader myClassLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") +1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }

                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object object = myClassLoader.loadClass("tech.zg.research.jvm.classLoader.ClassLoaderTest").newInstance();
        System.out.println(object.getClass());
        System.out.println(object instanceof ClassLoaderTest);
    }
}
