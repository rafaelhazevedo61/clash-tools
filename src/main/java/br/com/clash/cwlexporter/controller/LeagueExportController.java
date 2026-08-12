package br.com.clash.cwlexporter.controller;

import br.com.clash.cwlexporter.service.ClanWarLeagueExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequestMapping("/api/league")
@RequiredArgsConstructor
public class LeagueExportController {

    private final ClanWarLeagueExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<Resource> exportLeagueFile() {
        try {
            File file = exportService.exportLeagueFile();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new FileSystemResource(file));
        } catch (IOException | InterruptedException e) {
            log.error("Erro ao exportar arquivo da liga", e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Erro ao exportar arquivo da liga: " + e.getMessage(), e);
        }
    }
}
