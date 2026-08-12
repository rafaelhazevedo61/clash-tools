package br.com.clash.cwlexporter.controller;

import br.com.clash.cwlexporter.service.ClanWarLeagueExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/league")
@RequiredArgsConstructor
public class LeagueExportController {

    private final ClanWarLeagueExportService exportService;

    @GetMapping("/export")
    @ResponseStatus(HttpStatus.OK)
    public ExportResponse exportLeagueFile() {
        try {
            File file = exportService.exportLeagueFile();
            log.info("CLASH-TOOLS-LOG:::::: ARQUIVO GERADO COM SUCESSO em {}", file.getAbsolutePath());
            return new ExportResponse("Arquivo gerado com sucesso.", file.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            log.error("CLASH-TOOLS-LOG:::::: ERRO AO GERAR ARQUIVO", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao exportar arquivo da liga: " + e.getMessage(), e);
        }
    }

    public record ExportResponse(String message, String filePath) {
    }
}
