package com.platform.bdd.catalog;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.platform.Wave1BackendApplication;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
public class CucumberCatalogSpringBootConfiguration {}
