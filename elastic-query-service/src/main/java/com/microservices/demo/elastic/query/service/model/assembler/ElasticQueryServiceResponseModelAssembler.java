package com.microservices.demo.elastic.query.service.model.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.api.ElasticDocumentController;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.transformer.ElasticToResponseModelTransformer;

@Component
// RepresentationModelAssemblerSupport HATEOAS class
public class ElasticQueryServiceResponseModelAssembler
        extends RepresentationModelAssemblerSupport<TwitterIndexModel, ElasticQueryServiceResponseModel>
{
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public ElasticQueryServiceResponseModelAssembler(
            ElasticToResponseModelTransformer elasticToResponseModelTransformer)
    {
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
    }

    /**
     * We should get something like this as output:
     * {
     *      "id": "1316814210379079682",
     *      "text": “test",
     *      "createdAt": "2020-10-15T20:52:27",
     *      "_links": {
     *          "self": {
     *              "href": "http://localhost:8183/elastic-query-service/documents/1316814210379079682"
     *          },
     *          "documents": {
     * 	            "href": "http://localhost:8183/elastic-query-service/documents"
     *          }
     *      }
     * }
     * @param twitterIndexModel
     * @return ElasticQueryServiceResponseModel
     */
    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel)
    {
        final ElasticQueryServiceResponseModel responseModel = elasticToResponseModelTransformer.getResponseModel(
                twitterIndexModel);

        responseModel.add(linkTo(methodOn(ElasticDocumentController.class).getDocumentById(
                (twitterIndexModel.getId()))).withSelfRel());

        responseModel.add(linkTo(ElasticDocumentController.class).withRel("documents"));
        return responseModel;
    }

    public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> twitterIndexModels)
    {
        return twitterIndexModels.stream().map(this::toModel).collect(Collectors.toList());
    }
}

