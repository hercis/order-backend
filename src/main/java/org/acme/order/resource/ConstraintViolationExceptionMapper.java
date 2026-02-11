package org.acme.order.resource;

import org.acme.support.AppError.ValidationError;
import org.acme.support.AppResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Inject private ObjectMapper mapper;

  @Context private UriInfo uriInfo;

  @Override
  public Response toResponse(ConstraintViolationException e) {
    var errors =
        e.getConstraintViolations().stream()
            .map(v -> ValidationError.from(v.getPropertyPath().toString(), v.getMessage()))
            .toList();
    var response = AppResponse.fromErrors("Validation failed", errors);
    var jsonObj = buildJson(response);
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(jsonObj)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private ObjectNode buildJson(AppResponse error) {
    ObjectNode root =
        mapper
            .createObjectNode()
            .put("message", error.message())
            .set("errors", mapper.valueToTree(error.errors()));
    root.set("timestamp", mapper.convertValue(error.timestamp(), JsonNode.class));
    return root;
  }
}
