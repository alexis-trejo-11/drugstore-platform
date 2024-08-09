package at.backend.drugstore.microservice.common_models.Utils;

import io.netty.handler.logging.LogLevel;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class CustomHttpClient {

    private static final String LOGGER_NAME = "reactor.netty.http.client.HttpClient";

    public HttpClient createHttpClient() {
        return HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host("proxy.example.com")
                        .port(8080))
                .wiretap(LOGGER_NAME, LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
                .wiretap(true);
    }
}
