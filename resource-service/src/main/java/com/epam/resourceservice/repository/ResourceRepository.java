package com.epam.resourceservice.repository;

import com.epam.resourceservice.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
