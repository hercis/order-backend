package org.acme.order.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.amazon.lambda.http.model.ApiGatewayAuthorizerContext;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequest;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import io.quarkus.amazon.lambda.http.model.CognitoAuthorizerClaims;
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

  @Test
  public void testGetUsername() {
    AwsProxyRequest request = new AwsProxyRequest();
    request.setPath("/order");
    request.setHttpMethod("GET");
    request.setRequestContext(new AwsProxyRequestContext());
    request.getRequestContext().setAuthorizer(new ApiGatewayAuthorizerContext());
    request.getRequestContext().getAuthorizer().setClaims(new CognitoAuthorizerClaims());
    request.getRequestContext().getAuthorizer().getClaims().setClaim("cognito:username", "test");

    // System.out.println(request.toString());

    given()
        .body(request)
        .contentType("application/json")
        .accept("text/plain")
        .when()
        .post("/_lambda_")
        .then()
        .statusCode(200)
        .body("body", is("test"));
  }
}
