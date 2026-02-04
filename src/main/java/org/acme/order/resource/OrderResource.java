package org.acme.order.resource;

import org.acme.order.api.CreateOrderRequest;
import org.acme.order.service.OrderService;

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
  public Response handle(@Valid CreateOrderRequest input) {
    System.out.println("Received order creation request");
    return Response.ok().build();
  }
}
