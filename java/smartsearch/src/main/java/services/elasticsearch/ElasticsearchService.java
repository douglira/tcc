package services.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import com.google.gson.Gson;

import services.elasticsearch.ElasticsearchClient;
import models.ProductItem;

public class ElasticsearchService {

    private RestHighLevelClient client;

    public ElasticsearchService() {
        this.client = ElasticsearchClient.getTransport();
    }

    public void indexProductItem(ProductItem productItem) {
        if (productItem == null) {
            return;
        }

        ProductItem productIndex = new ProductItem();
        productIndex.setTitle(productItem.getTitle());
        productIndex.setId(productItem.getId());
        productIndex.setBasePrice(productItem.getBasePrice());
        productIndex.setMaxPrice(productItem.getMaxPrice());
        productIndex.setMinPrice(productItem.getMinPrice());
        productIndex.setRelevance(productItem.getRelevance());
        productIndex.setViewsCount(productItem.getViewsCount());
        productIndex.setThumbnail(productItem.getThumbnail());
        productIndex.setPictures(productItem.getPictures());
        productIndex.setStatus(productItem.getStatus());
        productIndex.setCreatedAt(productItem.getCreatedAt());
        productIndex.setUpdatedAt(productItem.getUpdatedAt());

        Gson gJson = new Gson();
        String productItemJson = gJson.toJson(productIndex);

        IndexRequest indexRequest = new IndexRequest("product_items", "_doc", String.valueOf(productIndex.getId()));
        indexRequest.source(productItemJson, XContentType.JSON);

        try {
            this.client.index(indexRequest, RequestOptions.DEFAULT);
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

    public List<ProductItem> getProductsItemPredict(String productItemTitle) {
        Gson gJson = new Gson();
        if (productItemTitle == null || productItemTitle.isEmpty()) {
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


        try {
            SearchRequest searchRequest = new SearchRequest("product_items")
                    .source(new SearchSourceBuilder().suggest(new SuggestBuilder().addSuggestion("products-suggest",
                            SuggestBuilders.completionSuggestion("title.completion").prefix(productItemTitle, Fuzziness.ONE).size(20))));
            
            SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getSuggest() != null) {
                CompletionSuggestion compSuggestion = searchResponse.getSuggest().getSuggestion("products-suggest");

                compSuggestion.getOptions().forEach(suggestion -> {
                    synchronized (suggestion) {
                        ProductItem productItem = gJson.fromJson(suggestion.getHit().getSourceAsString(), ProductItem.class);
                        productItem.setId(Integer.parseInt(suggestion.getHit().getId()));
                        products.add(productItem);
                    }
                });
            }

        } catch (IOException err) {
            err.printStackTrace();
            System.out.println("Elasticsearch.getProductsItemPredict [ERROR](1): " + err);
        } finally {
            if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.getProductsItemPredict - [ERROR](2): " + e);
                }
            }
        }

        return products;
    }

    public ArrayList<ProductItem> getProductsItemHomepage(int page, int perPage) {
        Gson gJson = new Gson();
        ArrayList<ProductItem> products = new ArrayList<ProductItem>();
        try {
            int from = (page - 1) * perPage;

            SearchRequest searchRequest = new SearchRequest("product_items")
                    .source(new SearchSourceBuilder().from(from).size(perPage));

            SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);
            List<SearchHit> searchHits = Arrays.asList(searchResponse.getHits().getHits());

            searchHits.forEach(hit -> {
                synchronized (hit) {
                    ProductItem productItem = gJson.fromJson(hit.getSourceAsString(), ProductItem.class);
                    productItem.setId(Integer.parseInt(hit.getId()));
                    products.add(productItem);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Elasticsearch.getProductsItemHomepage - [ERROR](1): " + e);
        } finally {
            if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.getProductsItemHomepage - [ERROR](2): " + e);
                }
            }
        }

        return products;
    }
    
    public void updateProductItemViewsCount(Integer productItemId, Integer viewsCount) {
    	try {
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("viewsCount", viewsCount);
    		
    		this.client.update(new UpdateRequest("product_items", "_doc", String.valueOf(productItemId))
    				.script(new Script(
    						ScriptType.INLINE, 
    						"painless",
    						"ctx._source.viewsCount = params.viewsCount",
    						parameters)), RequestOptions.DEFAULT);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
            System.out.println("Elasticsearch.updateProductItemViewsCount - [ERROR](1): " + e);
    	} finally {
    		if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.updateProductItemViewsCount - [ERROR](2): " + e);
                }
            }
    	}
    }
    
    public void updateProductItemPrices(ProductItem productItem) {
    	try {
    		XContentBuilder builder = XContentFactory.jsonBuilder();
    		
    		builder.startObject();
    		builder.field("basePrice", productItem.getBasePrice());
    		builder.field("maxPrice", productItem.getMaxPrice());
    		builder.field("minPrice", productItem.getMinPrice());
    		builder.endObject();
    		
    		this.client.update(
    				new UpdateRequest("product_items", "_doc", String.valueOf(productItem.getId()))
    				.doc(builder), RequestOptions.DEFAULT);
    	} catch (Exception e) {
    		e.printStackTrace();
            System.out.println("Elasticsearch.updateProductItemPrices - [ERROR](1): " + e);
    	} finally {
    		if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.updateProductItemPrices - [ERROR](2): " + e);
                }
            }
    	}
    }

    public void updateProductItemRelevance(ProductItem productItem) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();

            builder.startObject();
            builder.field("relevance", productItem.getRelevance());
            builder.endObject();

            this.client.update(
                    new UpdateRequest("product_items", "_doc", String.valueOf(productItem.getId())).doc(builder),
                    RequestOptions.DEFAULT
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Elasticsearch.updateProductItemRelevance - [ERROR](1): " + e);
        } finally {
            if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.updateProductItemRelevance - [ERROR](2): " + e);
                }
            }
        }
    }
    
    public void inactiveProductItem(Integer productItemId) {
    	try {
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("status", "INACTIVE");
    		
    		this.client.update(new UpdateRequest("product_items", "_doc", String.valueOf(productItemId))
    				.script(new Script(
    						ScriptType.INLINE, 
    						"painless",
    						"ctx._source.status = params.status",
    						parameters)), RequestOptions.DEFAULT);
    	} catch (Exception e) {
    		e.printStackTrace();
            System.out.println("Elasticsearch.inactiveProductItem - [ERROR](1): " + e);
    	} finally {
    		if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.inactiveProductItem - [ERROR](2): " + e);
                }
            }
    	}
    }
    
    public void deleteProductItem(Integer productItemId) {
    	try {
    		DeleteRequest deleteRequest = new DeleteRequest("product_items", "_doc", String.valueOf(productItemId));
    		
    		this.client.delete(deleteRequest, RequestOptions.DEFAULT);
    	} catch (Exception e) {
    		e.printStackTrace();
            System.out.println("Elasticsearch.deleteProductItem - [ERROR](1): " + e);
    	} finally {
    		if (this.client != null) {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Elasticsearch.deleteProductItem - [ERROR](2): " + e);
                }
            }
    	}
    }
}
