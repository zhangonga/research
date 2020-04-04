package tech.zg.research.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AdminApiTest {

    private TransportClient transportClient;
    private AdminApi adminApi = new AdminApi();


    @Before
    public void testBefore() throws UnknownHostException {
        Settings setting = Settings.builder().put("cluster.name", "zg-es").build();
        transportClient = new PreBuiltTransportClient(setting)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.128"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.129"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.130"), 9300));
    }

    @Test
    public void testCreateIndexByParam() {
        adminApi.createIndexByParam(transportClient);
    }

    @After
    public void testAfter() {
        transportClient.close();
    }
}
