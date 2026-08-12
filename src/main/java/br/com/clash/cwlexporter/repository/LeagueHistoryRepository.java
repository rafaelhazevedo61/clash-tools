package br.com.clash.cwlexporter.repository;

import br.com.clash.cwlexporter.entity.LeagueHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueHistoryRepository extends JpaRepository<LeagueHistoryEntity, Long> {

    List<LeagueHistoryEntity> findByClanTagOrderByGeneratedAtDesc(String clanTag);

    List<LeagueHistoryEntity> findByClanTagAndSeasonOrderByGeneratedAtDesc(String clanTag, String season);

    List<LeagueHistoryEntity> findAllByOrderByGeneratedAtDesc();

    @Modifying
    @Query("DELETE FROM LeagueHistoryEntity")
    void deleteAllHistories();
}
