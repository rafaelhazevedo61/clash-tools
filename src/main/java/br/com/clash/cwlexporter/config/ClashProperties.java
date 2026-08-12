package br.com.clash.cwlexporter.config;

import br.com.clash.cwlexporter.model.Clan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
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

    @Data
    public static class Api {
        @NotBlank
        private String baseUrl;
        @NotBlank
        private String bearerToken;
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
