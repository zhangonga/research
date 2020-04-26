package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.util.CharsetUtil;
import lombok.Data;

@Data
public class ProtocolBean {

    private int len;
    private byte[] content;

    @Override
    public String toString() {
        return "ProtocolBean{" +
                "len=" + len +
                ", content=" + new String(content, CharsetUtil.UTF_8) +
                '}';
    }
}
