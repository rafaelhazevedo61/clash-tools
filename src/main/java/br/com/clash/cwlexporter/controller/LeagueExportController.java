package br.com.clash.cwlexporter.controller;

import br.com.clash.cwlexporter.service.ClanWarLeagueExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
@RequestMapping("/api/league")
@RequiredArgsConstructor
public class LeagueExportController {

    private final ClanWarLeagueExportService exportService;

    @GetMapping("/export")
    public void exportLeagueFile(HttpServletResponse response) {
        try {
            File file = exportService.exportLeagueFile();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            response.setHeader("Content-Length", String.valueOf(file.length()));
            Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException | InterruptedException e) {
            log.error("Erro ao exportar arquivo da liga", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao exportar arquivo da liga: " + e.getMessage(), e);
        }
    }
}
