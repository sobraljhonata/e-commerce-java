package com.platform.bdd.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.catalog.adapters.in.web.CreateProductRequest;
import com.platform.catalog.adapters.in.web.UpdateProductRequest;
import com.platform.catalog.adapters.out.persistence.InMemoryProductRepository;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/** BDD Catalog: stack Spring + Security + JWT (mesmo padrão do IAM admin). */
public class CatalogProductStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private InMemoryProductRepository productRepository;

  private final ObjectMapper mapper = new ObjectMapper();

  private String bearerToken;
  private int lastStatus;
  private JsonNode lastJson;
  private String lastProductId;
  private int lastGetStatus;
  private JsonNode lastGetJson;
  private int lastListStatus;
  private JsonNode lastListJson;
  private int lastPatchStatus;
  private JsonNode lastPatchJson;

  @Before
  public void reset() {
    productRepository.clear();
    bearerToken = null;
    lastStatus = 0;
    lastJson = null;
    lastProductId = null;
    lastGetStatus = 0;
    lastGetJson = null;
    lastListStatus = 0;
    lastListJson = null;
    lastPatchStatus = 0;
    lastPatchJson = null;
  }

  @Dado("que o operador está autenticado como admin de plataforma")
  public void loginComoAdmin() throws Exception {
    var res =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new LoginRequest(
                                InMemoryAdminUserRepository.SEED_EMAIL,
                                InMemoryAdminUserRepository.SEED_PASSWORD))))
            .andReturn()
            .getResponse();
    JsonNode json = mapper.readTree(res.getContentAsString());
    bearerToken = json.get("accessToken").asText();
  }

  @Quando("cadastra um produto com nome {string} e preço {double}")
  public void cadastraProduto(String nome, double preco) throws Exception {
    var res =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(nome, BigDecimal.valueOf(preco), true))))
            .andReturn()
            .getResponse();
    lastStatus = res.getStatus();
    String body = res.getContentAsString();
    lastJson = body.isBlank() ? null : mapper.readTree(body);
  }

  @Então("a API de produtos responde 201")
  public void responde201() {
    assertEquals(201, lastStatus);
  }

  @Então("o tenant do produto é o do usuário seed")
  public void tenantDoProdutoIgualSeed() {
    assertNotNull(lastJson);
    assertEquals(
        InMemoryAdminUserRepository.SEED_TENANT_ID.toString(), lastJson.get("tenantId").asText());
  }

  @E("que um produto foi cadastrado com nome {string} e preço {double}")
  public void cadastraParaConsulta(String nome, double preco) throws Exception {
    var res =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(nome, BigDecimal.valueOf(preco), true))))
            .andReturn()
            .getResponse();
    assertEquals(201, res.getStatus());
    JsonNode json = mapper.readTree(res.getContentAsString());
    lastProductId = json.get("id").asText();
  }

  @Quando("consulta esse produto pelo id do cadastro")
  public void consultaPorId() throws Exception {
    var res =
        mockMvc
            .perform(
                get("/api/admin/products/" + lastProductId)
                    .header("Authorization", "Bearer " + bearerToken))
            .andReturn()
            .getResponse();
    lastGetStatus = res.getStatus();
    String body = res.getContentAsString();
    lastGetJson = body.isBlank() ? null : mapper.readTree(body);
  }

  @Então("a resposta da consulta de produto é 200")
  public void consulta200() {
    assertEquals(200, lastGetStatus);
  }

  @Então("o nome do produto retornado é {string}")
  public void nomeProdutoConsulta(String nomeEsperado) {
    assertNotNull(lastGetJson);
    assertEquals(nomeEsperado, lastGetJson.get("name").asText());
  }

  @E("que dois produtos foram cadastrados para listagem")
  public void cadastraDoisParaListagem() throws Exception {
    postProduto("Alpha", 1.0);
    assertEquals(201, lastStatus);
    postProduto("Beta", 2.0);
    assertEquals(201, lastStatus);
  }

  private void postProduto(String nome, double preco) throws Exception {
    var res =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(nome, BigDecimal.valueOf(preco), true))))
            .andReturn()
            .getResponse();
    lastStatus = res.getStatus();
    String body = res.getContentAsString();
    lastJson = body.isBlank() ? null : mapper.readTree(body);
  }

  @Quando("lista os produtos")
  public void listaProdutos() throws Exception {
    var res =
        mockMvc
            .perform(get("/api/admin/products").header("Authorization", "Bearer " + bearerToken))
            .andReturn()
            .getResponse();
    lastListStatus = res.getStatus();
    String body = res.getContentAsString();
    lastListJson = body.isBlank() ? null : mapper.readTree(body);
  }

  @Então("a resposta da listagem de produtos é 200")
  public void listagem200() {
    assertEquals(200, lastListStatus);
  }

  @Então("a listagem contém {int} itens do tenant seed")
  public void listagemTamanhoETenant(int esperado) {
    assertNotNull(lastListJson);
    assertEquals(esperado, lastListJson.size());
    for (int i = 0; i < lastListJson.size(); i++) {
      assertEquals(
          InMemoryAdminUserRepository.SEED_TENANT_ID.toString(),
          lastListJson.get(i).get("tenantId").asText());
    }
  }

  @Quando("atualiza o produto cadastrado com nome {string} preço {double} e ativo {string}")
  public void atualizaProduto(String nome, double preco, String ativoStr) throws Exception {
    boolean ativo = Boolean.parseBoolean(ativoStr);
    var res =
        mockMvc
            .perform(
                patch("/api/admin/products/" + lastProductId)
                    .header("Authorization", "Bearer " + bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new UpdateProductRequest(nome, BigDecimal.valueOf(preco), ativo))))
            .andReturn()
            .getResponse();
    lastPatchStatus = res.getStatus();
    String body = res.getContentAsString();
    lastPatchJson = body.isBlank() ? null : mapper.readTree(body);
  }

  @Então("a resposta da atualização de produto é {int}")
  public void atualizacaoStatus(int esperado) {
    assertEquals(esperado, lastPatchStatus);
  }

  @Então("o nome na resposta da atualização é {string}")
  public void nomeNaAtualizacao(String esperado) {
    assertNotNull(lastPatchJson);
    assertEquals(esperado, lastPatchJson.get("name").asText());
  }
}
