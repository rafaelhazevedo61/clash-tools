package br.com.clash.cwlexporter.config;

import br.com.clash.cwlexporter.model.Clan;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Data
@Slf4j
@Validated
@ConfigurationProperties(prefix = "clash")
public class ClashProperties {

    private Api api;
    private String leagueGroupEndpoint;
    private String warEndpoint;
    private Output output;
    private Weights weights;
    @NotEmpty
    private List<Clan> clans;

    @PostConstruct
    public void loadTokenFromFile() {
        if (api == null || api.getTokenFile() == null || api.getTokenFile().isBlank()) {
            return;
        }
        Path path = Paths.get(api.getTokenFile());
        if (Files.exists(path)) {
            try {
                String token = Files.readString(path).trim();
                api.setBearerToken(token);
                log.info("Bearer token carregado do arquivo: {}", path);
            } catch (IOException e) {
                log.error("Erro ao ler arquivo de token: {}", path, e);
            }
        } else {
            log.warn("Arquivo de token não encontrado: {}", path);
        }
    }

    @Data
    public static class Api {
        @NotBlank
        private String baseUrl;
        private String bearerToken;
        private String tokenFile;
    }

    @Data
    public static class Output {
        private String directory;
        private String filenamePrefix;
    }

    @Data
    public static class Weights {
        private double attackStars = 1.0;
        private double defenseStars = 1.0;
    }
}
