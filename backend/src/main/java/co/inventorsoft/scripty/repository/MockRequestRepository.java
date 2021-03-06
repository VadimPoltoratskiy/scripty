package co.inventorsoft.scripty.repository;

import co.inventorsoft.scripty.model.entity.MockRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MockRequestRepository extends JpaRepository<MockRequestEntity, Long> {
    MockRequestEntity findByTokenAndMethod(String token, String method);
}
