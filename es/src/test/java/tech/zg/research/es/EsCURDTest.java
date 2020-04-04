package tech.zg.research.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsCURDTest {

    private TransportClient transportClient;
    private EsCURD esCURD = new EsCURD();


    @Before
    public void testBefore() throws UnknownHostException {
        Settings setting = Settings.builder().put("cluster.name", "zg-es").build();
        transportClient = new PreBuiltTransportClient(setting)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.128"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.129"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.130"), 9300));
    }

    @Test
    public void testCreate() throws IOException {
        esCURD.create(transportClient);
    }

    @Test
    public void testQuery() {
        esCURD.query(transportClient);
    }

    @Test
    public void testMultiQuery() {
        esCURD.multiQuery(transportClient);
    }

    @Test
    public void testDelete() {
        esCURD.delete(transportClient);
    }

    @Test
    public void testDeleteByQuery() {
        esCURD.deleteByQuery(transportClient);
    }

    @Test
    public void testDeleteAsync() throws InterruptedException {
        esCURD.deleteAsync(transportClient);
        Thread.sleep(10000);
    }

    @Test
    public void testSearchByRange(){
        esCURD.searchByRange(transportClient);
    }

    @Test
    public void testGroupByQuery(){
        esCURD.groupByQuery(transportClient);
    }

    @Test
    public void testGroupByQuery2(){
        esCURD.groupByQuery2(transportClient);
    }

    @Test
    public void testMaxQuery(){
        esCURD.maxQuery(transportClient);
    }

    @After
    public void testAfter() {
        transportClient.close();
    }
}
