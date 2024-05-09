package com.demo.security.filter;

import com.demo.security.config.RedisComponent;
import com.demo.security.dto.ApiKey;
import com.demo.security.util.AppConstants;
import com.demo.security.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisComponent redisComponent;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> apiKeysHeader = exchange.getRequest().getHeaders().get("gatewayKey");
        log.info("api key : {} ", apiKeysHeader);
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : null;
        if (routeId == null || CollectionUtils.isEmpty(apiKeysHeader) || !isAuthorized(routeId, apiKeysHeader.get(0))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ApiKey not matching with our record");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private boolean isAuthorized(String routeId, String apiKey) {
        Object apiKeyObject = redisComponent.hGet(AppConstants.RECORD_KEY, apiKey);
        if (apiKeyObject != null) {
            ApiKey key = MapperUtils.mapObject(apiKeyObject, ApiKey.class);
            return key.getServices().contains(routeId);
        }
        return false;
    }
}
