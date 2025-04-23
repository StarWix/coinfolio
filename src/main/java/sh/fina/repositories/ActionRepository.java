package sh.fina.repositories;

import sh.fina.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    @Query("SELECT DISTINCT a.assetSymbol FROM Action a")
    List<String> findDistinctAssetSymbol();
}
