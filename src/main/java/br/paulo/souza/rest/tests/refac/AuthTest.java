package br.paulo.souza.rest.tests.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.paulo.souza.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest{
	
	
	@Test
	public void t01_naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification request = (FilterableRequestSpecification) RestAssured.requestSpecification;
		request.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
}
