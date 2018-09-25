package facades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import com.google.gson.Gson;

import database.elasticsearch.ClientTransport;
import models.ProductItem;

public class ElasticsearchFacade {

	private Client client;
	private static ElasticsearchFacade esFacade;
	private final Gson gJson = new Gson();

	private ElasticsearchFacade() {
		if (this.client == null) {
			this.client = ClientTransport.getTransport();			
		}
	}
	
	public static ElasticsearchFacade getInstance() {
		if (esFacade == null) {
			esFacade = new ElasticsearchFacade();
			return esFacade;
		}
		return esFacade;
	}
	
	public void indexProductItem(ProductItem productItem) {
		if (productItem == null) {
			return;
		}
		
		Gson gJson = new Gson();
		String productItemJson = gJson.toJson(productItem);
		
		IndexResponse response = client.prepareIndex("product_items", "_doc", productItem.getId().toString())
			.setSource(productItemJson, XContentType.JSON)
			.get();
	}

	public List<ProductItem> getProductsPredict(String productItemTitle) {
		if (productItemTitle == null || productItemTitle.length() <= 1) {
			return null;
		}

		QueryBuilder qb = QueryBuilders.matchQuery("title", productItemTitle);

		SearchResponse esResponse = this.client.prepareSearch("product_items").setQuery(qb)
				.addSort("relevance", SortOrder.DESC).execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(esResponse.getHits().getHits());
		List<ProductItem> products = new ArrayList<ProductItem>();
		searchHits.forEach(hit -> {
			synchronized(hit) {
				ProductItem productItem = this.gJson.fromJson(hit.getSourceAsString(), ProductItem.class);
				productItem.setId(Integer.parseInt(hit.getId()));
				products.add(productItem);				
			}
		});

		return products;
	}
}
