package com.example.SpringProject1.util;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IndexHandler {
    private final String INDEX = "cvindex";
    private final RestHighLevelClient client;


    @Autowired
    public IndexHandler(RestHighLevelClient client) throws IOException {
        this.client = client;

        createIndex(INDEX, 1, 1);
    }

    /**
     *
     * @param name : index name
     * @param shards : number of shards
     * @param replicas : number of replicas
     * @throws IOException
     */
    public void createIndex(String name, int shards, int replicas) throws IOException {
        if (!client.indices().exists(new org.elasticsearch.client.indices.GetIndexRequest(name), RequestOptions.DEFAULT)) {
            CreateIndexRequest request = new CreateIndexRequest(name);
            request.settings(Settings.builder().put("index.number_of_shards", shards).put("index.number_of_replicas", replicas));
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            System.out.println("response id: " + createIndexResponse.index());
        } else {
            System.out.println("Index " + name + " already exists");
        }
    }

    /**
     *
     * @param skill : the key word to look for
     * @return  A list of file names containing the skill
     * @throws IOException
     */
    public List<String> search(String skill) throws IOException {
        final String DIR = "src/main/java/com/example/SpringProject1/Docs";
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(QueryBuilders.matchQuery("textContent", skill.toLowerCase()));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(builder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] searchHits = response.getHits().getHits();

        List<CV> res =
                Arrays.stream(searchHits)
                        .map(hit -> new CV(hit.getSourceAsMap().get("filename").toString(), hit.getSourceAsMap().get("content").toString()))
                        .collect(Collectors.toList());

        for (CV e : res) {
            PDFParser.decode(e.getEncoded(), Paths.get(DIR, e.getFilename()).toString());
        }

        return res.stream().map(e -> e.getFilename()).collect(Collectors.toList());
    }

    /**
     *
     * @param file : The file that will be parsed and indexed
     * @throws IOException
     */
    public void handleUpload(MultipartFile file) throws IOException {
        String textContent = PDFParser.parse(file.getBytes());

        IndexRequest indexRequest = new IndexRequest(INDEX);
        String fileEncoded = PDFParser.encodePdfBase64(file.getInputStream());
        StringBuilder fbuilder = new StringBuilder(file.getOriginalFilename());
        fbuilder.insert(fbuilder.indexOf(".pdf"), UUID.randomUUID());
        XContentBuilder builder = XContentFactory
                .jsonBuilder()
                .startObject()
                .field("filename", fbuilder.toString())
                .field("textContent", textContent.toLowerCase())
                .field("content", fileEncoded)
                .endObject();
        indexRequest.source(builder);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("response id: " + indexResponse.getId());
    }
}
