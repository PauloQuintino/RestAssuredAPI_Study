package br.paulo.souza.rest.tests.refac;

import static br.paulo.souza.utils.DateUtils.getDataDiferencaDias;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.paulo.souza.rest.core.BaseTest;
import br.paulo.souza.rest.tests.Movimentacao;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest{
		
	@Test
	public void t05_deveInserirMovimentacaoSucesso() {		
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void t06_deveValidarCamposObrigatorios() {		
		given()
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
			.pathParam("id", getIDContaPeloNome("Conta para extrato"))
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimetacao() {	
		
		Integer MOV_ID = getIDMovimentacaoPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	/* *************************************************** */
	
	public Integer getIDContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public Integer getIDMovimentacaoPelaDescricao(String descricao) {
		return RestAssured.get("/transacoes?descricao="+descricao).then().extract().path("id[0]");
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(getIDContaPeloNome("Conta para movimentacoes"));
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
