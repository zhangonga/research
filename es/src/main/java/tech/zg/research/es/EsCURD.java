package tech.zg.research.es;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class EsCURD {

    public void create(TransportClient transportClient) throws IOException {
        IndexResponse indexResponse = transportClient.prepareIndex("news", "fulltext", "5")
                .setSource(
                        jsonBuilder().startObject().field("context", "这是一个测试语句").endObject()
                ).get();
        System.out.println(JSON.toJSONString(indexResponse));
    }

    public void query(TransportClient transportClient) {
        GetResponse getResponse = transportClient.prepareGet("news", "fulltext", "5").execute().actionGet();
        System.out.println(JSON.toJSONString(getResponse));
    }

    public void multiQuery(TransportClient transportClient) {
        MultiGetResponse multiGetResponse = transportClient.prepareMultiGet()
                .add("news", "fulltext", "1")
                .add("news", "fulltext", "2")
                .get();

        Iterator<MultiGetItemResponse> itemResponseIterator = multiGetResponse.iterator();
        while (itemResponseIterator.hasNext()) {
            MultiGetItemResponse multiGetItemResponse = itemResponseIterator.next();
            GetResponse getResponse = multiGetItemResponse.getResponse();
            if (getResponse.isExists()) {
                String result = getResponse.getSourceAsString();
                System.out.println(result);
            }
        }
    }

    public void delete(TransportClient transportClient) {
        DeleteResponse deleteResponse = transportClient.prepareDelete("news", "fulltext", "5").get();
        System.out.println(deleteResponse);
    }

    public void deleteByQuery(TransportClient transportClient) {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .filter(QueryBuilders.matchQuery("context", "测试"))
                .source("news")
                .get();
        long deleted = response.getDeleted();
        System.out.println(deleted);
    }


    public void deleteAsync(TransportClient transportClient) {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .filter(QueryBuilders.matchQuery("context", "测试"))
                .source("news")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        long deleted = bulkByScrollResponse.getDeleted();
                        System.out.println(deleted);
                    }

                    public void onFailure(Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    public void searchByRange(TransportClient transportClient) {

        // 主要要使用数字返回，则存储的时候，要指定数字类型，否则按照字符串方式排序
        QueryBuilder queryBuilder = rangeQuery("context")
                .from(100)
                .to(1000)
                .includeLower(true)
                .includeUpper(false);

        SearchResponse searchResponse = transportClient.prepareSearch("news").setQuery(queryBuilder).get();
        System.out.println(JSON.toJSONString(searchResponse));
    }


    /**
     * select team, count(*) as player_count from player group by team;
     *
     * @param transportClient
     */
    public void groupByQuery(TransportClient transportClient) {
        SearchRequestBuilder builder = transportClient.prepareSearch("player_info").setTypes("player");
        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("player_count").field("team");
        builder.addAggregation(teamAgg);

        SearchResponse response = builder.execute().actionGet();
        Map<String, Aggregation> aggMap = response.getAggregations().getAsMap();
        StringTerms terms = (StringTerms) aggMap.get("player_count");

        for (Terms.Bucket bucket : terms.getBuckets()) {
            String team = (String) bucket.getKey();
            long count = bucket.getDocCount();
            System.out.println(team + " : " + count);
        }
    }

    /**
     * select team, position, count(*) as pos_count from player group by team, position
     *
     * @param transportClient
     */
    public void groupByQuery2(TransportClient transportClient) {

        SearchRequestBuilder builder = transportClient.prepareSearch("player_info").setTypes("player");
        // 指定别名和分组字段
        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("team_name").field("team");
        TermsAggregationBuilder posAgg = AggregationBuilders.terms("pos_count").field("position");
        // 添加两个聚合构建器，先按照team分组，再按照position分组
        builder.addAggregation(teamAgg.subAggregation(posAgg));

        // 执行查询
        SearchResponse searchResponse = builder.execute().actionGet();

        // 将查询结果放入map中
        Map<String, Aggregation> aggMap = searchResponse.getAggregations().getAsMap();
        StringTerms terms = (StringTerms) aggMap.get("player_count");

        for (Terms.Bucket bucket : terms.getBuckets()) {
            String team = (String) bucket.getKey();
            Map<String, Aggregation> posMap = bucket.getAggregations().getAsMap();
            StringTerms positions = (StringTerms) posMap.get("pos_count");
            for (Terms.Bucket posBucket : positions.getBuckets()) {
                String pos = (String) posBucket.getKey();
                long count = bucket.getDocCount();
                System.out.println(team + " " + pos + " : " + count);
            }
        }
    }

    /**
     * select team, max(age) as max_age from player group by team
     *
     * @param transportClient
     */
    public void maxQuery(TransportClient transportClient) {

        SearchRequestBuilder builder = transportClient.prepareSearch("player_info").setTypes("player");
        // 指定别名和分组字段
        TermsAggregationBuilder teamAgg = AggregationBuilders.terms("team").field("team");
        MaxAggregationBuilder maxAgeAgg = AggregationBuilders.max("max_age").field("age");
        builder.addAggregation(teamAgg.subAggregation(maxAgeAgg));

        // 执行查询
        SearchResponse searchResponse = builder.execute().actionGet();

        // 将查询结果放入map中
        Map<String, Aggregation> aggMap = searchResponse.getAggregations().getAsMap();
        StringTerms teamTerms = (StringTerms) aggMap.get("team");

        for (Terms.Bucket teamBucket : teamTerms.getBuckets()) {
            String team = (String) teamBucket.getKey();

            Map<String, Aggregation> maxAgeMap = teamBucket.getAggregations().getAsMap();
            InternalMax ages = (InternalMax) maxAgeMap.get("max_age");
            double max = ages.getValue();
            System.out.println(team + " : " + max);
        }
    }
}
