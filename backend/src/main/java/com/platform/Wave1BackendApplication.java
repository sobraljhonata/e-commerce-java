package com.platform;

import com.platform.iam.config.JwtSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Repositórios W1 ainda são em memória; JPA/PostgreSQL fica no classpath para evolução — excluído do
 * auto-config até haver persistência real.
 */
@SpringBootApplication(
        scanBasePackages = "com.platform",
        exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties(JwtSecurityProperties.class)
public class Wave1BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Wave1BackendApplication.class, args);
    }
}
