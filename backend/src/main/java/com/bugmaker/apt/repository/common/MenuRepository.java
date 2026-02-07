package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByName(String name);
}
