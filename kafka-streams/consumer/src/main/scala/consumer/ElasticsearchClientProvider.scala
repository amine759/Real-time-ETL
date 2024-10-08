package consumer

import org.elasticsearch.client.{RestClient, RestClientBuilder, RestHighLevelClient}
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.conn.ssl.{NoopHostnameVerifier, SSLConnectionSocketFactory}
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory

object ElasticsearchClientProvider {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def createClient(config: AppConfig): RestHighLevelClient = {
    // Set up credentials provider with Elasticsearch credentials
    val credentialsProvider = new BasicCredentialsProvider()
    credentialsProvider.setCredentials(
      AuthScope.ANY,
      new UsernamePasswordCredentials(config.elasticsearchUser, config.elasticsearchPassword)
    )

    // Build an SSL context that trusts all certificates
    val sslContext = new SSLContextBuilder()
      .loadTrustMaterial(null, (certificate, authType) => true) // Trust all certificates
      .build()

    // Create SSL socket factory with no hostname verification
    val sslSocketFactory = new SSLConnectionSocketFactory(
      sslContext,
      NoopHostnameVerifier.INSTANCE // Disable hostname verification
    )

    // Configure the RestClientBuilder with SSL and credentials
    val clientBuilder: RestClientBuilder = RestClient.builder(new org.apache.http.HttpHost(
      config.elasticsearchHost,
      config.elasticsearchPort,
      config.elasticsearchScheme
    )).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
      override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
        httpClientBuilder
          .setSSLContext(sslContext) // Use the SSL context that trusts all certificates
          .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // Disable hostname verification
          .setDefaultCredentialsProvider(credentialsProvider) // Set credentials provider
      }
    })

    logger.info("Initializing Elasticsearch client with SSL verification disabled...")
    new RestHighLevelClient(clientBuilder)
  }
}
