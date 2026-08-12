package br.com.clash.cwlexporter.repository;

import br.com.clash.cwlexporter.entity.PlayerHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerHistoryRepository extends JpaRepository<PlayerHistoryEntity, Long> {

    List<PlayerHistoryEntity> findByLeagueIdOrderByTotalStarsDesc(Long leagueId);
}
