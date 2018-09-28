package database.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import com.google.gson.Gson;

import models.ProductItem;

public class ElasticsearchFacade {

	private RestHighLevelClient client;
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
		productIndex.setThumbnail(productItem.getThumbnail());
		productIndex.setPictures(productItem.getPictures());

		Gson gJson = new Gson();
		String productItemJson = gJson.toJson(productIndex);

		IndexRequest indexRequest = new IndexRequest("product_items", "_doc", productIndex.getId().toString());
		indexRequest.source(productItemJson, XContentType.JSON);

		try {
			this.client.index(indexRequest);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (this.client != null) {
				try {
					this.client.close();
				} catch (IOException e) {
					System.out.println("Elasticsearch.indexProductItem - Client.close [ERROR]: " + e);
				}				
			}
		}
	}

	public List<ProductItem> getProductsPredict(String productItemTitle) {
		if (productItemTitle == null || productItemTitle.length() <= 1) {
			return null;
		}
		final List<ProductItem> products = new ArrayList<ProductItem>();

//		QueryBuilder qb = QueryBuilders.matchQuery("title", productItemTitle);
//
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//		searchSourceBuilder.query(qb).sort("relevance", SortOrder.DESC).minScore(Float.parseFloat("0.65"));
		
//			searchHits.forEach(hit -> {
//				synchronized (hit) {
//					ProductItem productItem = this.gJson.fromJson(hit.getSourceAsString(), ProductItem.class);
//					productItem.setId(Integer.parseInt(hit.getId()));
//					products.add(productItem);
//				}
//			});

		SearchRequest searchRequest = new SearchRequest("product_items")
				.source(new SearchSourceBuilder().suggest(new SuggestBuilder().addSuggestion("products-suggest",
						SuggestBuilders.completionSuggestion("title.completion").prefix(productItemTitle, Fuzziness.ONE).size(20))));

		try {
			SearchResponse searchResponse = this.client.search(searchRequest);
			if (searchResponse.getSuggest() != null) {				
			CompletionSuggestion compSuggestion = searchResponse.getSuggest().getSuggestion("products-suggest");
			
				compSuggestion.getOptions().forEach(suggestion -> {
					synchronized (suggestion) {
						ProductItem productItem = this.gJson.fromJson(suggestion.getHit().getSourceAsString(), ProductItem.class);
						productItem.setId(Integer.parseInt(suggestion.getHit().getId()));
						products.add(productItem);
					}
				});
			}

		} catch (IOException e) {
			System.out.println("Elasticsearch.getProductsPredict [ERROR]: " + e);
		} finally {
			if (this.client != null) {				
				try {
					this.client.close();
				} catch (IOException e) {
					System.out.println("Elasticsearch.getProductsPredict - Client.close [ERROR]: " + e);
				}
			}
		}

		return products;
	}
}
