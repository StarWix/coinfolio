package sh.fina.repositories;

import sh.fina.entities.ProviderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderConfigRepository extends JpaRepository<ProviderConfig, Integer> {
    List<ProviderConfig> findAll();
}
