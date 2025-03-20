package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.FetcherState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FetcherStateRepository extends JpaRepository<FetcherState, FetcherState.Id> {
}
