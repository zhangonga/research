package tech.zg.research.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;

public class AdminApi {

    public void createIndexByParam(TransportClient transportClient) {
        AdminClient adminClient = transportClient.admin();
        IndicesAdminClient indicesAdminClient = adminClient.indices();
        indicesAdminClient.prepareCreate("news").setSettings(
                Settings.builder()
                        // 分区数量
                        .put("index.number_of_shards", 4)
                        // 副本个数
                        .put("index.number_of_replicas", 2)
        ).get();
    }

    /**
     * index 的属性，no表示不建索引不分词，not_analyzed 建索引，不分词，analyzed 即分词，又建索引
     *
     * @param transportClient
     * @throws IOException
     */
    public void setSettingMapping(TransportClient transportClient) throws IOException {
        HashMap<String, Object> setting_map = new HashMap<String, Object>();
        setting_map.put("number_of_shards", 3);
        setting_map.put("number_of_replicas", 2);

        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()

                .field("dynamic", "true")

                .startObject("properties")

                .startObject("id")
                .field("type", "integer")
                .field("store", "yes")
                .endObject()

                .startObject("name")
                .field("type", "string")
                .field("store", "yes")
                .field("index", "analyzed")
                .field("analyzer", "ik_smart")
                .endObject()

                 .endObject()

                .endObject();

        // index = user_info type = user
        CreateIndexRequestBuilder prepareCreate = transportClient.admin().indices().prepareCreate("user_info");
        prepareCreate.setSettings(setting_map).addMapping("user", builder).get();
    }
}
