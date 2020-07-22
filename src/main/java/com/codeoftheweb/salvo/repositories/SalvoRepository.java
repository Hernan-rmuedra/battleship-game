package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Salvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface SalvoRepository extends JpaRepository<Salvo, Long> {
    List<Salvo> findByTurnNumber(String turnNumber);
}
