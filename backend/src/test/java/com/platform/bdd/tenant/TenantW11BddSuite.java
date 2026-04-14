package com.platform.bdd.tenant;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Suite JUnit Platform que executa os cenários Cucumber do BC Tenant (W1.1).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/bdd/tenant_admin_w11.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.platform.bdd.tenant")
public class TenantW11BddSuite {}
