package tech.zg.research.jvm.box;

/**
 * 自动拆箱封箱
 *
 * @author zg
 */
public class TestBox {

    public static void main(String[] args) {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;

        System.out.println(c == d);
        System.out.println(e == f);
        System.out.println(c == (a + b));
        System.out.println(c.equals(a + b));
        System.out.println(g == (a + b));
        System.out.println(g.equals(a + b));

        //true
        //false
        //true
        //true
        //true
        //false
        // 三个原因
        // 1，Integer、Short、Byte、Character、Long 在装箱时候会判断在 -128 和 127 之间不会创建 Integer Long 类型，而是放入 内部类 IntegerCache LongCache 中
        // Double、Float 除外
        // Boolean 类型每次返回相同的对象
        // 所以 -128,127中间的数据，是存在方法区中的，所以== 为true
        // 2，运算会拆箱
        // 3，equals 会先判断类型
    }
}
