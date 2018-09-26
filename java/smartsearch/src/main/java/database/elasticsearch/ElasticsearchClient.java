package database.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticsearchClient {
	private static final Settings SETTINGS_CONNECTION = Settings.builder()
			.put("client.transport.sniff", true)
			.put("cluster.name", "docker-cluster")
			.build();
	
	public static TransportClient getTransport() {
		try {
			return new PreBuiltTransportClient(SETTINGS_CONNECTION)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return null;
	}
}
