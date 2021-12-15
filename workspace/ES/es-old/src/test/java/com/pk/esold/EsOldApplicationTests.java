package com.pk.esold;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@SpringBootTest
class EsOldApplicationTests {

    @Autowired
    private TransportClient client;

//    @Value("${intention.inside.pushed.tieba.index}")
//    private String bakTiebaIndex;

    @Test void contextLoads() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        BoolQueryBuilder filter = boolQuery().filter(termQuery("_id", "AXnVaEhtxqKbJFC1za01"));
        SearchResponse searchResponse = client.prepareSearch("daily_query")
                .setTypes("query_info")
                .setQuery(filter)
                .get();
        System.out.println(searchResponse.getHits().getTotalHits());
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getId());
            System.out.println(hit.getSource());
        }
    }

    @Test
    public void insert() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(11);
        map.put("eventDay",20210117);
        map.put("instanceId",736);
        map.put("role",integers);
        map.put("userid",1261619805);
        String s = mapper.writeValueAsString(map);
        IndexResponse indexResponse = client.prepareIndex("daily_query", "query_info","1261619805_736_20210117").setSource(s, XContentType.JSON).get();
    }
}
