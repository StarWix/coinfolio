package sh.fina.repositories;

import sh.fina.entities.FetcherState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FetcherStateRepository extends JpaRepository<FetcherState, FetcherState.Id> {
}
