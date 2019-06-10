package services.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticsearchClient {
//	private static final Settings SETTINGS_CONNECTION = Settings.builder()
//			.put("client.transport.sniff", true)
//			.put("cluster.name", "docker-cluster")
//			.build();
//	
//	public static TransportClient getTransport() {
//		try {
//			return new PreBuiltTransportClient(SETTINGS_CONNECTION)
//					.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
//		} catch (UnknownHostException e) {
//			System.out.println(e.toString());
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static RestHighLevelClient getTransport() {
		return new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")));
                // new HttpHost("elasticsearch", 9200, "http")));
	}
}
