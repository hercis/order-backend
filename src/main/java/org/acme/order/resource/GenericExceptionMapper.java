package org.acme.order.resource;

import org.acme.support.AppResponse;
import org.jboss.logging.Logger;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = Logger.getLogger(GenericExceptionMapper.class);

  @Override
  public Response toResponse(Throwable e) {
    LOG.error("", e);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(AppResponse.fromMessage(e.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
