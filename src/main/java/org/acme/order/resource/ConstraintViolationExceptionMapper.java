package org.acme.order.resource;

import java.util.ArrayList;

import org.acme.support.AppError.ErrorInfo;
import org.acme.support.AppError.ErrorResponse;

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
    var errors = new ArrayList<ErrorInfo>();
    e.getConstraintViolations()
        .forEach(
            v -> {
              String field = v.getPropertyPath().toString();
              String message = v.getMessage();
              errors.add(ErrorInfo.of(field, message));
            });
    var response = ErrorResponse.fromErrors("Validation failed", errors);
    var json = buildJson(response);
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(json)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private ObjectNode buildJson(ErrorResponse error) {
    ObjectNode root =
        mapper
            .createObjectNode()
            .put("message", error.message())
            .set("errors", mapper.valueToTree(error.errors()));
    root.set("timestamp", mapper.convertValue(error.timestamp(), JsonNode.class));
    return root;
  }
}
