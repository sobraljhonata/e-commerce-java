package com.platform.bdd.iam.api;

import com.platform.Wave1BackendApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
public class CucumberSpringBootConfiguration {}
