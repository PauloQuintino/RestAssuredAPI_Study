package br.paulo.souza.rest.tests;

import static br.paulo.souza.utils.DateUtils.getDataDiferencaDias;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.paulo.souza.rest.core.BaseTest;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

	private String TOKEN;
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	@Before
	public void login() {
		Map<String,	String> login = new HashMap<>();
		login.put("email", "pauloqfs16@gmail.com");
		login.put("senha", "Paulo010203@");
		
		//extraindo token de autenticação após realizar o login
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
	}
	
	@Test
	public void t01_naoDeveAcessarApiSemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void t02_deveCriarContaComSucesso() {
				;
		//inserindo uma nova conta passando o token no HEADER
		CONTA_ID = given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}		
	
	@Test
	public void t03_deveAlterarContaComSucesso() {				
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\""+CONTA_NAME+"_Alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME+"_Alterada"))
		;
	}
	
	@Test
	public void t04_naoDeveInserirContaComMesmoNome() {				
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\":\""+CONTA_NAME+"_Alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void t05_deveInserirMovimentacaoSucesso() {		
		Movimentacao mov = getMovimentacaoValida();
		
		MOV_ID = given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov) // -> será convertido em JSON
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t06_deveValidarCamposObrigatorios() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void t07_naoDeveInserirMovimentacaoComDataFutura() {		
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(getDataDiferencaDias(2));
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov) // -> será convertido em JSON
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentacao() {	
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.log().all()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveRetornarSaldoConta() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
			//.pathParam("conta_id", CONTA_ID)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("643.00"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimetacao() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	/* *************************************************** */
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setTipo("REC");
		mov.setData_transacao(getDataDiferencaDias(-1));
		mov.setData_pagamento(getDataDiferencaDias(5));
		mov.setDescricao("Pagamento conta");
		mov.setEnvolvido("Banco");
		mov.setValor(643f);
		mov.setStatus(true);
		return mov;
	}
	
}
