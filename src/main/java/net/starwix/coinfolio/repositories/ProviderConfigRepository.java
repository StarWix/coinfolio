package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.ProviderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderConfigRepository extends JpaRepository<ProviderConfig, Integer> {
    List<ProviderConfig> findAll();
}
