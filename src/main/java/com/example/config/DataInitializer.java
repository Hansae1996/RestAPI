package com.example.config;

import com.example.model.Car;
import com.example.service.CarService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 초기 실행 시 DB 삽입용
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CarService carService;

    public DataInitializer(CarService carService) {
        this.carService = carService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 이미 데이터가 있으면 실행하지 않도록 확인
        // 예시: 데이터베이스에 차 정보가 없으면 추가하기

        ResponseEntity<Object> response = carService.getAllCars();
        // 상태 코드가 200 OK인 경우
        if (response.getStatusCode() == HttpStatus.OK) {

            List<Car> cars = (List<Car>) response.getBody();

            if (cars != null && cars.isEmpty()) {
                carService.saveCars();  // 데이터 삽입
                System.out.println("데이터 초기 설정 완료");
            }
        }
    }
}

