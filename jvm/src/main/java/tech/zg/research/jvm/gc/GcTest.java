package tech.zg.research.jvm.gc;

/**
 * Gc 测试
 * -verbose:gc -Xmx20m -Xms20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8
 *
 * @author zg
 */
public class GcTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        // 新对象优先进新生代
        // testAllocation();
        // 大对象直接进老年代
        // testPretenureSizeThreshold();
        // 测试活的久的对象进入老年代
        // testTenuringThreshold();
        // 测试活的久的对象进入老年代2
        testTenuringThreshold2();
    }

    /**
     * 测试活的久的对象进入老年代
     * 并不是所有的对象必须等年龄到15了才进入老年代
     * 还有个动态对象年龄判定原则
     * 如果某一个相同年龄的对象的总大小大于 Survivor空间的一半，则大于等于该年龄的对象都进入老年代
     */
    private static void testTenuringThreshold2() {
        byte[] alloc1;
        byte[] alloc2;
        byte[] alloc3;
        byte[] alloc4;

        // alloc1 + alloc2 > survivor/2
        alloc1 = new byte[_1MB / 4];
        alloc2 = new byte[_1MB / 4];

        alloc3 = new byte[4 * _1MB];
        alloc4 = new byte[4 * _1MB];
        alloc4 = null;
        alloc4 = new byte[4 * _1MB];
        alloc4 = null;
        alloc4 = new byte[4 * _1MB];
    }

    /**
     * 测试活的久的对象进入老年代，默认是15
     * 要指定非 Parallel Scavenge GC收集器
     * <p>
     * -XX:MaxTenuringThreshold=1
     */
    private static void testTenuringThreshold() {
        byte[] alloc1;
        byte[] alloc2;
        byte[] alloc3;

        alloc1 = new byte[_1MB / 4];
        alloc2 = new byte[4 * _1MB];
        alloc3 = new byte[4 * _1MB];
        alloc3 = null;
        alloc3 = new byte[4 * _1MB];
        alloc3 = null;
        alloc3 = new byte[4 * _1MB];
        alloc3 = null;
        alloc3 = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xmx20m -Xms20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
     * <p>
     * -XX:PretenureSizeThreshold=3145728 设置大于3MB的对象进入老年代
     * 单位字节 新生代采用 Parallel Scavenge GC时无效
     */
    private static void testPretenureSizeThreshold() {
        byte[] alloc1;
        alloc1 = new byte[4 * _1MB];

        /*
            Heap
            PSYoungGen      total 9216K, used 6527K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
            eden space 8192K, 79% used [0x00000000ff600000,0x00000000ffc5ff68,0x00000000ffe00000)
            from space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
            to   space 1024K, 0% used [0x00000000ffe00000,0x00000000ffe00000,0x00000000fff00000)
            ParOldGen       total 10240K, used 0K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
            object space 10240K, 0% used [0x00000000fec00000,0x00000000fec00000,0x00000000ff600000)
            Metaspace       used 3278K, capacity 4568K, committed 4864K, reserved 1056768K
            class space    used 350K, capacity 392K, committed 512K, reserved 1048576K
        */

    }

    /**
     * -verbose:gc -Xmx20m -Xms20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8
     */
    private static void testAllocation() {
        byte[] alloc1;
        byte[] alloc2;
        byte[] alloc3;
        byte[] alloc4;

        alloc1 = new byte[2 * _1MB];
        alloc2 = new byte[2 * _1MB];
        alloc3 = new byte[2 * _1MB];
        alloc4 = new byte[4 * _1MB];

        /*
        [GC (Allocation Failure) [PSYoungGen: 6363K->783K(9216K)] 6363K->4887K(19456K), 0.0105734 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
        Heap
        PSYoungGen      total 9216K, used 7169K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
        eden space 8192K, 77% used [0x00000000ff600000,0x00000000ffc3c758,0x00000000ffe00000)
        from space 1024K, 76% used [0x00000000ffe00000,0x00000000ffec3cc0,0x00000000fff00000)
        to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
        ParOldGen       total 10240K, used 4104K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
        object space 10240K, 40% used [0x00000000fec00000,0x00000000ff002020,0x00000000ff600000)
        Metaspace       used 3275K, capacity 4568K, committed 4864K, reserved 1056768K
        class space    used 350K, capacity 392K, committed 512K, reserved 1048576K
        */

        // 解析
        /*
           PSYoungGen 可以得出用的是 Parallel Scavenge 垃圾回收器
           新生代大小为 10MB
           PSYoungGen 9MB
           eden 8MB
           survivor from 1MB
           survivor to   1MB

           ParOldGen 10MB
           alloc4 在分配内存时，发现新生代不够，所以分配到了老年代，顺便进行了新生代 Minor GC
        */
    }
}
