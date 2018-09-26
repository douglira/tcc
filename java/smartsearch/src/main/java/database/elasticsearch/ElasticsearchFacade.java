package database.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import com.google.gson.Gson;

import models.ProductItem;

public class ElasticsearchFacade {

	private TransportClient client;
	private final Gson gJson = new Gson();

	public ElasticsearchFacade() {
		if (this.client == null) {
			this.client = ElasticsearchClient.getTransport();
		}
	}

	public void indexProductItem(ProductItem productItem) {
		if (productItem == null) {
			return;
		}

		ProductItem productIndex = new ProductItem();
		productIndex.setTitle(productItem.getTitle());
		productIndex.setId(productItem.getId());
		productIndex.setMarketPrice(productItem.getMarketPrice());
		productIndex.setMaxPrice(productItem.getMaxPrice());
		productIndex.setMinPrice(productItem.getMinPrice());
		productIndex.setRelevance(productItem.getRelevance());
		productIndex.setViewsCount(productItem.getViewsCount());
		productIndex.setThumbnailPath(productItem.getThumbnailPath());
		productIndex.setPictures(productItem.getPictures());

		Gson gJson = new Gson();
		String productItemJson = gJson.toJson(productIndex);

		this.client.prepareIndex("product_items", "_doc", productItem.getId().toString())
				.setSource(productItemJson, XContentType.JSON).get();
		this.client.close();
	}

	public List<ProductItem> getProductsPredict(String productItemTitle) {
		if (productItemTitle == null || productItemTitle.length() <= 1) {
			return null;
		}

		QueryBuilder qb = QueryBuilders.matchQuery("title", productItemTitle);

		SearchResponse esResponse = this.client.prepareSearch("product_items").setQuery(qb)
				.setMinScore(Float.parseFloat("0.85")).addSort("relevance", SortOrder.DESC).execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(esResponse.getHits().getHits());
		List<ProductItem> products = new ArrayList<ProductItem>();
		searchHits.forEach(hit -> {
			synchronized (hit) {
				ProductItem productItem = this.gJson.fromJson(hit.getSourceAsString(), ProductItem.class);
				productItem.setId(Integer.parseInt(hit.getId()));
				products.add(productItem);
			}
		});

		this.client.close();
		return products;
	}
}
