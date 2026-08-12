package br.com.clash.cwlexporter.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "league_history")
public class LeagueHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clanTag;

    private String clanName;

    private String season;

    private String filePath;

    @CreationTimestamp
    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerHistoryEntity> players = new ArrayList<>();
}
