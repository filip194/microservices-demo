package com.microservices.demo.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
// we must use MVC @Controller with Thymeleaf as @RestController won't work because it converts response to JSON/XML
@Controller
public class QueryController {
    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient elasticQueryWebClient) {
        this.elasticQueryWebClient = elasticQueryWebClient;
    }

    @GetMapping("")
    public String index() {
        // with @Controller methods return paths; returns matching index String to render index template
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    /*
    ui.Model class -> is the key of interaction with Thymeleaf templates, because any attribute we put on the model
    can be reached by Thymeleaf template by using Thymeleaf expressions.
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    /*
    @PostMapping -> here we have an action that will called from form on home path, and will return 'home' string and
     render home template.
     */
    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
        log.info("Querying by text: {}", requestModel.getText());

        final ElasticQueryWebClientAnalyticsResponseModel responseModel = elasticQueryWebClient.getDataByText(
                requestModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModel.getElasticQueryServiceResponseModels());
        model.addAttribute("wordCount", responseModel.getWordCount());
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());

        return "home";
    }

}
