package com.example.repository;

import com.example.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//public interface CarRepository extends JpaRepository<Car, Long> {
//    List<Car> findByManufacturerAndModelAndProductionYear(String manufacturer, String model, Integer productionYear);
//    List<Car> findByCategoriesName(String category);
//}
@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    List<Car> findByCategoriesName(String category);

    @Query("SELECT c FROM Car c WHERE " +
            "( :manufacturer IS NULL OR c.manufacturer = :manufacturer ) " +
            "AND ( :model IS NULL OR c.model = :model ) " +
            "AND ( :productionYear IS NULL OR c.productionYear = :productionYear )")
    List<Car> findByManufacturerAndModelAndProductionYear(
            @Param("manufacturer") String manufacturer,
            @Param("model") String model,
            @Param("productionYear") Integer productionYear
    );
}