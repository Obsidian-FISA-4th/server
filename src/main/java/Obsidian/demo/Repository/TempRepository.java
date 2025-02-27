package Obsidian.demo.Repository;

import Obsidian.demo.domain.common.TempEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRepository extends JpaRepository<TempEntity, Long> {}
