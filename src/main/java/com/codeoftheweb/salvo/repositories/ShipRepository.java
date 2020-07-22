package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByShipType(String Type);
}
