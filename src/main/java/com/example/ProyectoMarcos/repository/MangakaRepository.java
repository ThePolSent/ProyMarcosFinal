package com.example.ProyectoMarcos.repository;

import com.example.ProyectoMarcos.model.Mangaka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MangakaRepository extends JpaRepository<Mangaka, Long> {

}
