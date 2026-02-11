package org.acme.order.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OrderResourceTest {

  @Test
  public void testCreateOrder() {
    String json =
        """
            {
              "customerId": "c1",
              "items": [
                { "productId": "p1", "quantity": 2, "price": 10.0 }
              ]
            }""";

    given()
        .contentType("application/json")
        .body(json)
        .when()
        .post("/order")
        .then()
        .statusCode(200)
        .body("entity.orderId", notNullValue(), "entity.customerId", containsString("c1"));
  }
}
