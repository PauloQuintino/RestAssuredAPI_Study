package br.paulo.souza.rest.tests.refac.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.paulo.souza.rest.core.BaseTest;
import br.paulo.souza.rest.tests.refac.AuthTest;
import br.paulo.souza.rest.tests.refac.ContaTest;
import br.paulo.souza.rest.tests.refac.MovimentacaoTest;
import br.paulo.souza.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContaTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class
})

public class SuiteTeste extends BaseTest{
	@BeforeClass
	public static void login() {
		Map<String,	String> login = new HashMap<>();
		login.put("email", "pauloqfs16@gmail.com");
		login.put("senha", "Paulo010203@");
		
		//extraindo token de autenticação após realizar o login
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		//passando o token de autenticação no header no inicío
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		//resetando a aplicação antes dos testes
		RestAssured.get("/reset").then().statusCode(200);
	}
}
