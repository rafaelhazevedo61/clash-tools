package br.com.clash.cwlexporter.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "player_day_data")
public class PlayerDayDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer day;

    private Integer attackStars;

    private Double defenseStars;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerHistoryEntity player;
}
