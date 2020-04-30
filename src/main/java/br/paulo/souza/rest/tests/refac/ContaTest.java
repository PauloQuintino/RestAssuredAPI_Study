package br.paulo.souza.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.paulo.souza.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContaTest extends BaseTest{
	
	private static Integer CONTA_ID;
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	
	
	@Test
	public void deveCriarContaComSucesso() {
				;
		//inserindo uma nova conta passando o token no HEADER
		CONTA_ID = given()
			.body("{\"nome\":\""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}	
	
	@Test
	public void deveAlterarContaComSucesso() {			
		Integer CONTA_ID = getIDContaPeloNome("Conta para alterar");
		
		given()
			.body("{\"nome\":\"Conta Alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta Alterada"))
		;
	}
	
	@Test
	public void naoDeveInserirContaComMesmoNome() {				
		given()
			.body("{\"nome\":\"Conta mesmo nome\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	/* outros metodos */
	
	public Integer getIDContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
}
