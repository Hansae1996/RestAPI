package com.example.config;

import com.example.model.Car;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

    // 제조사로 필터링
    public static Specification<Car> hasManufacturer(String manufacturer) {
        return (root, query, criteriaBuilder) -> {
            if (manufacturer == null) {
                return criteriaBuilder.conjunction(); // null일 경우 모든 값을 허용
            }
            return criteriaBuilder.equal(root.get("manufacturer"), manufacturer);
        };
    }

    // 모델로 필터링
    public static Specification<Car> hasModel(String model) {
        return (root, query, criteriaBuilder) -> {
            if (model == null) {
                return criteriaBuilder.conjunction(); // null일 경우 모든 값을 허용
            }
            return criteriaBuilder.equal(root.get("model"), model);
        };
    }

    // 생산 연도로 필터링
    public static Specification<Car> hasProductionYear(Integer productionYear) {
        return (root, query, criteriaBuilder) -> {
            if (productionYear == null) {
                return criteriaBuilder.conjunction(); // null일 경우 모든 값을 허용
            }
            return criteriaBuilder.equal(root.get("productionYear"), productionYear);
        };
    }
}
