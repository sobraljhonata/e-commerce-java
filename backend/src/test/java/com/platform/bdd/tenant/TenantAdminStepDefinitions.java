package com.platform.bdd.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.tenant.adapters.in.web.CreateTenantRequest;
import com.platform.tenant.adapters.in.web.TenantController;
import com.platform.tenant.adapters.in.web.TenantExceptionHandler;
import com.platform.tenant.adapters.in.web.UpdateTenantStatusRequest;
import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.CreateTenantUseCase;
import com.platform.tenant.application.GetTenantByIdUseCase;
import com.platform.tenant.application.GetTenantBySlugUseCase;
import com.platform.tenant.application.UpdateTenantStatusUseCase;
import com.platform.tenant.domain.Tenant;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/** BDD W1.1: API admin de tenants com stack real (use cases + repositório em memória + MockMvc). */
public class TenantAdminStepDefinitions {

  private InMemoryTenantRepository repository;
  private MockMvc mockMvc;
  private final ObjectMapper mapper = new ObjectMapper();

  private int lastStatus;
  private JsonNode lastJson;
  private String lastTenantId;
  private UUID lastRandomId;

  @Before
  public void reset() {
    repository = new InMemoryTenantRepository();
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    var controller =
        new TenantController(
            new CreateTenantUseCase(repository),
            new GetTenantByIdUseCase(repository),
            new GetTenantBySlugUseCase(repository),
            new UpdateTenantStatusUseCase(repository));
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new TenantExceptionHandler())
            .setValidator(validator)
            .build();
    lastStatus = 0;
    lastJson = null;
    lastTenantId = null;
    lastRandomId = null;
  }

  @Dado("que o catálogo de tenants está vazio")
  public void catalogoVazio() {
    // estado inicial garantido em @Before
  }

  @Dado("que já existe um tenant com slug {string} e nome {string}")
  public void tenantExistente(String slug, String nome) {
    repository.save(Tenant.create(slug, nome));
  }

  @Dado("que existe um tenant com slug {string} e nome {string}")
  public void tenantCriadoViaApi(String slug, String nome) throws Exception {
    postCreateTenant(slug, nome);
    assertEquals(201, lastStatus, () -> "esperado 201 ao preparar tenant, obtido " + lastStatus);
    lastTenantId = lastJson.get("id").asText();
  }

  @Dado("que existe um tenant ativo com slug {string} e nome {string}")
  public void tenantAtivo(String slug, String nome) throws Exception {
    tenantCriadoViaApi(slug, nome);
  }

  @Dado("que existe um tenant inativo com slug {string} e nome {string}")
  public void tenantInativo(String slug, String nome) throws Exception {
    tenantCriadoViaApi(slug, nome);
    patchStatus(false);
    assertEquals(200, lastStatus);
    assertFalse(lastJson.get("active").asBoolean());
  }

  @Quando("o operador cria um tenant com slug {string} e nome de exibição {string}")
  public void quandoOperadorCriaTenant(String slug, String nomeExibicao) throws Exception {
    postCreateTenant(slug, nomeExibicao);
    if (lastStatus == 201 && lastJson != null && lastJson.has("id")) {
      lastTenantId = lastJson.get("id").asText();
    }
  }

  private void postCreateTenant(String slug, String nomeExibicao) throws Exception {
    ResultActions actions =
        mockMvc.perform(
            post("/api/admin/tenants")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateTenantRequest(slug, nomeExibicao))));
    capture(actions);
  }

  @Quando("o operador tenta criar outro tenant com slug {string} e nome {string}")
  public void tentarDuplicar(String slug, String nome) throws Exception {
    postCreateTenant(slug, nome);
  }

  @Quando("o operador consulta o tenant pelo id retornado na criação")
  public void consultarPorId() throws Exception {
    ResultActions actions =
        mockMvc.perform(get("/api/admin/tenants/{id}", UUID.fromString(lastTenantId)));
    capture(actions);
  }

  @Quando("o operador consulta o tenant pelo slug {string}")
  public void consultarPorSlug(String slug) throws Exception {
    ResultActions actions = mockMvc.perform(get("/api/admin/tenants/slug/{slug}", slug));
    capture(actions);
  }

  @Quando("o operador consulta um tenant por um id aleatório")
  public void consultarIdAleatorio() throws Exception {
    lastRandomId = UUID.randomUUID();
    ResultActions actions = mockMvc.perform(get("/api/admin/tenants/{id}", lastRandomId));
    capture(actions);
  }

  @Quando("o operador define o status do tenant como inativo")
  public void desativar() throws Exception {
    patchStatus(false);
  }

  @Quando("o operador define o status do tenant como ativo")
  public void ativar() throws Exception {
    patchStatus(true);
  }

  private void patchStatus(boolean active) throws Exception {
    ResultActions actions =
        mockMvc.perform(
            patch("/api/admin/tenants/{id}/status", UUID.fromString(lastTenantId))
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(new UpdateTenantStatusRequest(active))));
    capture(actions);
  }

  private void capture(ResultActions actions) throws Exception {
    var result = actions.andReturn();
    lastStatus = result.getResponse().getStatus();
    String raw = result.getResponse().getContentAsString();
    if (raw == null || raw.isBlank()) {
      lastJson = null;
    } else {
      lastJson = mapper.readTree(raw);
    }
  }

  @Então("a API responde {int}")
  public void statusEsperado(int esperado) {
    assertEquals(esperado, lastStatus);
  }

  @Então("o corpo contém slug {string}")
  public void corpoContemSlug(String slug) {
    assertNotNull(lastJson);
    assertEquals(slug, lastJson.get("slug").asText());
  }

  @Então("o corpo indica que o tenant está ativo")
  public void corpoAtivo() {
    assertNotNull(lastJson);
    assertTrue(lastJson.get("active").asBoolean());
  }

  @Então("o corpo indica que o tenant está inativo")
  public void corpoInativo() {
    assertNotNull(lastJson);
    assertFalse(lastJson.get("active").asBoolean());
  }

  @Então("o corpo contém um id de tenant válido")
  public void idValido() {
    assertNotNull(lastJson);
    assertNotNull(UUID.fromString(lastJson.get("id").asText()));
  }

  @Então("o corpo de erro tem código {string}")
  public void erroCodigo(String codigo) {
    assertNotNull(lastJson);
    assertEquals(codigo, lastJson.get("code").asText());
  }

  @Então("o corpo de erro referencia o slug {string}")
  public void erroSlug(String slug) {
    assertNotNull(lastJson);
    assertEquals(slug, lastJson.get("slug").asText());
  }

  @Então("o corpo de erro referencia o tenantId consultado")
  public void erroTenantIdConsultado() {
    assertNotNull(lastJson);
    assertEquals(lastRandomId.toString(), lastJson.get("tenantId").asText());
  }
}
