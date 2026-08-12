package br.com.clash.cwlexporter.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "player_history")
public class PlayerHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerTag;

    private String playerName;

    private int totalAttackStars;

    private double totalDefenseStars;

    private double totalStars;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private LeagueHistoryEntity league;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerDayDataEntity> days = new ArrayList<>();
}
