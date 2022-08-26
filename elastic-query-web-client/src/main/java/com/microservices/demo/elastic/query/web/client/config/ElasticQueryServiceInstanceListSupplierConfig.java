package com.microservices.demo.elastic.query.web.client.config;

/**
 *  Deprecated as we removed @LoadBalancerClient from WebClientConfig and introduced eureka
 */

@Deprecated
//@Configuration
//@Primary // tells Spring to use this class instead of other in-built implementations
public class ElasticQueryServiceInstanceListSupplierConfig
//        implements ServiceInstanceListSupplier
{
//    private final ElasticQueryWebClientConfigData.WebClient webClientConfig;
//
//    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData webClientConfigData)
//    {
//        this.webClientConfig = webClientConfigData.getWebClient();
//    }
//
//    @Override
//    public String getServiceId()
//    {
//        return webClientConfig.getServiceId();
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get(Request request)
//    {
//        return ServiceInstanceListSupplier.super.get(request);
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get()
//    {
//        return Flux.just(webClientConfig.getInstances().stream()
//                .map(instance -> new DefaultServiceInstance(
//                        instance.getId(),
//                        getServiceId(),
//                        instance.getHost(),
//                        instance.getPort(),
//                        false))
//                .collect(Collectors.toList())
//        );
//    }
}

