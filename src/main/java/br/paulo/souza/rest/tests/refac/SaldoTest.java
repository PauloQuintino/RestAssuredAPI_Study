package br.paulo.souza.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.paulo.souza.rest.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest{
	
	@Test
	public void deveRetornarSaldoConta() {		
		Integer CONTA_ID = getIDContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
		;
	}
	
	
	
	/* outros metodos */
	
	public Integer getIDContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
}
