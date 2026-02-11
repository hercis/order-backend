package org.acme.order.resource;

import org.acme.order.api.CreateOrderRequest;
import org.acme.order.domain.Order;
import org.acme.order.service.OrderService;
import org.acme.support.AppError;
import org.acme.support.AppError.NotFoundError;
import org.acme.support.AppResponse;
import org.acme.support.Result;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/order")
public class OrderResource {

  @Inject OrderService service;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createOrder(@Valid CreateOrderRequest request) {
    Result<Order, AppError> serviceResult = service.create(request);
    Response response =
        serviceResult.fold(
            error ->
                switch (error) {
                  case NotFoundError e ->
                      Response.status(Response.Status.NOT_FOUND)
                          .entity(AppResponse.fromError(e))
                          .build();
                  case AppError e ->
                      Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(AppResponse.fromError("Internal server error", e))
                          .build();
                },
            order -> Response.ok(AppResponse.fromMessage("Order created", order)).build());
    return response;
  }
}
