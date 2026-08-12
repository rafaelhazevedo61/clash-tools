package br.com.clash.cwlexporter;

import br.com.clash.cwlexporter.config.ClashProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClashProperties.class)
public class ClashCwlExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClashCwlExporterApplication.class, args);
    }
}
