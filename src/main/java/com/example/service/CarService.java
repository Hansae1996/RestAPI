package com.example.service;

import com.example.config.CarSpecification;
import com.example.controller.CarController;
import com.example.model.Car;
import com.example.model.Category;
import com.example.repository.CarRepository;
import com.example.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CarService(CarRepository carRepository, CategoryRepository categoryRepository) {
        this.carRepository = carRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Car registerCar(Car car) {

        if (car.getCategories() == null || car.getCategories().isEmpty() ||
                car.getCategories().stream().anyMatch(category -> category.getName() == null || category.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("차량에 반드시 유효한 카테고리가 있어야 합니다.");
        }
        // 기존 카테고리가 있으면 가져오고 없으면 새로운 카테고리 추가
        List<Category> categories = car.getCategories().stream()
                .map(category -> saveCategoryIfNotExists(category.getName()))
                .collect(Collectors.toList());
        car.setCategories(categories);
        return carRepository.save(car);
    }

    public ResponseEntity<Object> getAllCars() {
        List<Car> cars = new ArrayList<Car>();
        try {
            cars = carRepository.findAll();
            return ResponseEntity.ok(cars);
        }catch (Exception e) {
            logger.error(e.getMessage());
            // 예외 발생 시 500 에러로 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> getCarsByCategory(String category) {
        List<Car> cars = new ArrayList<Car>();
        try {
            cars = carRepository.findByCategoriesName(category);
            return ResponseEntity.ok(cars);
        }catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 500 에러로 응답
            logger.error("An error occurred while updating the car: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    public List<Car> getCarsByManufacturerAndModel(String manufacturer, String model, Integer productionYear) {
        return carRepository.findByManufacturerAndModelAndProductionYear(manufacturer, model, productionYear);
    }

    @Transactional
    public ResponseEntity<Object> updateCar(Long carId, Car updatedCar) {
        try {
            // carId로 기존 Car를 조회
            Car existingCar = carRepository.findById(carId)
                    .orElseThrow(() -> new RuntimeException("Car not found with id " + carId));

            // 기존 Car의 필드 업데이트 (변경된 필드만 업데이트하도록 추가 조건을 넣을 수도 있음)
            if (updatedCar.getManufacturer() != null) {
                existingCar.setManufacturer(updatedCar.getManufacturer());
            }
            if (updatedCar.getModel() != null) {
                existingCar.setModel(updatedCar.getModel());
            }
            if (updatedCar.getProductionYear() != null) {
                existingCar.setProductionYear(updatedCar.getProductionYear());
            }

            // 카테고리 업데이트: 카테고리 이름으로 존재하는 카테고리 찾고 연결
            if (updatedCar.getCategories() != null && !updatedCar.getCategories().isEmpty()) {
                List<Category> updatedCategories = updatedCar.getCategories().stream()
                        .map(category -> categoryRepository.findByName(category.getName())
                                .orElseGet(() -> categoryRepository.save(category))) // 존재하지 않으면 저장
                        .collect(Collectors.toList());
                existingCar.setCategories(updatedCategories);
            }

            // status 업데이트 (있다면 업데이트, 없으면 디폴트 "대기" 사용)
            if (updatedCar.getStatus() != null) {
                existingCar.setStatus(updatedCar.getStatus());
            } else {
                existingCar.setStatus(Car.CarStatus.대기);  // status가 null이면 기본값 "대기" 설정
            }

            // 업데이트된 Car를 저장
            carRepository.save(existingCar);
            return ResponseEntity.ok(existingCar);
        } catch (Exception e) {
            // 예외 발생 시 500 에러로 응답
            logger.error("An error occurred while updating the car: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    public void saveCars() {
        // 중복된 카테고리 이름을 제거한 후 카테고리 저장
        List<String> categoryNames = List.of("미니SUV", "준중형 SUV", "대형RV", "중형 트럭", "준중형 SUV", "대형 RV", "경형 RV", "중형 SUV");
        List<Category> categories = categoryNames.stream()
                .distinct()  // 중복된 카테고리 이름 제거
                .map(name -> saveCategoryIfNotExists(name))  // saveCategoryIfNotExists 메서드를 활용하여 중복되지 않게 처리
                .collect(Collectors.toList());

        // Category 먼저 저장
        categoryRepository.saveAll(categories);

        // 각 차를 Car 객체로 매핑하여 저장
        List<Car> cars = List.of(
                new Car("현대", "코나", 2024, List.of(categories.get(0))),  // 미니SUV
                new Car("현대", "아이오닉", 2024, List.of(categories.get(1))),  // 준중형 SUV
                new Car("현대", "스타리아", 2022, List.of(categories.get(2))),  // 대형RV
                new Car("현대", "포터", 2024, List.of(categories.get(3))),  // 중형 트럭
                new Car("현대", "투싼", 2023, List.of(categories.get(1))),  // 준중형 SUV
                new Car("KIA", "카니발", 2021, List.of(categories.get(2))),  // 대형 RV
                new Car("KIA", "레이", 2022, List.of(categories.get(6))),  // 경형 RV
                new Car("KIA", "봉고3", 2023, List.of(categories.get(3))),  // 중형 트럭
                new Car("KIA", "쏘렌토", 2024, List.of(categories.get(4)))   // 중형 SUV
        );

        // Car 데이터 저장
        carRepository.saveAll(cars);
    }

    public Category saveCategoryIfNotExists(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category category = new Category(name);
                    return categoryRepository.save(category);
                });
    }
}
