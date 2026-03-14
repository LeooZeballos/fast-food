package net.leozeballos.FastFood.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN FETCH m.items")
    List<Menu> findAllWithItems();
}
