package org.example.repository;

import org.example.model.CustomerAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerActionRepository extends JpaRepository<CustomerAction,Long> {
}
