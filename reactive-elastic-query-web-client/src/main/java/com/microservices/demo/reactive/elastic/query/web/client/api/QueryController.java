package com.microservices.demo.reactive.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class QueryController {
    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient elasticQueryWebClient) {
        this.elasticQueryWebClient = elasticQueryWebClient;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @PostMapping(value = "/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
        Flux<ElasticQueryWebClientResponseModel> responseModel = elasticQueryWebClient.getDataByText(requestModel);
        responseModel = responseModel.log();
        // second parameter, 1, is to set the buffer size to 1, so we will see results one by one on the web browser
        final IReactiveDataDriverContextVariable reactiveData = new ReactiveDataDriverContextVariable(responseModel, 1);
        model.addAttribute("elasticQueryClientResponseModels", reactiveData);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());

        log.info("Returning from reactive client controller for text: {}", requestModel.getText());
        return "home";
    }
}

