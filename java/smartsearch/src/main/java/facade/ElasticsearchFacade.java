package facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import com.google.gson.Gson;

import database.elasticsearch.ClientTransport;
import models.ProductItem;

public class ElasticsearchFacade {

	private final Client client;
	private final Gson gJson = new Gson();

	public ElasticsearchFacade() {
		this.client = ClientTransport.getTransport();

		if (this.client == null) {
			System.out.println("Error at get elasticsearch client transport.");
		}
	}

	public List<ProductItem> getProductsPredict(String productItemTitle) {
		if (productItemTitle == null || productItemTitle.length() <= 1) {
			return null;
		}

		QueryBuilder qb = QueryBuilders.matchQuery("title", productItemTitle);

		SearchResponse esResponse = this.client.prepareSearch("products_item").setQuery(qb)
				.addSort("relevance", SortOrder.DESC).execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(esResponse.getHits().getHits());
		List<ProductItem> products = new ArrayList<ProductItem>();
		searchHits.forEach(hit -> products.add(this.gJson.fromJson(hit.getSourceAsString(), ProductItem.class)));

		this.client.close();
		return products;
	}
}
