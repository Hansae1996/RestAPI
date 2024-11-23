package com.example.controller;

import com.example.model.Car;
import com.example.service.CarService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;


@RestController
@RequestMapping("/api")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * 차 등록
     * @param car
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Car> registerCar(@Valid @RequestBody Car car) {
        // Valid으로 유효성 검사 후 집입
        Car registeredCar = carService.registerCar(car);
        return ResponseEntity.ok(registeredCar);
    }

    /**
     * 모든 차 조히
     * @return
     */
    @GetMapping("/getAllCars")
    public ResponseEntity<Object> getAllCars() {
        return carService.getAllCars();
    }

    /**
     * category 검색
     * @param category
     * @return
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Object> getCarsByCategory(@PathVariable String category) {
        return carService.getCarsByCategory(category);
    }

    /**
     * 자동차 검색
     * @param manufacturer
     * @param model
     * @param productionYear
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<Object> getCarsByManufacturerAndModel(String manufacturer,String model,
                                                                Integer productionYear, String categories) {
        // 필수 파라미터 체크: 세 개 중 하나라도 존재해야 함
        if (manufacturer == null && model == null && productionYear == null && categories == null) {
            return ResponseEntity
                    .badRequest()  // 실패 응답 (400 Bad Request)
                    .body("manufacturer, model, productionYear 3개중 하나는 필수");
        }

        List<Car> cars = new ArrayList<Car>();
        // 실제 서비스 로직 실행

        //category인 경우
        if(categories != null){
            return carService.getCarsByCategory(categories);
        }else{
            cars = carService.getCarsByManufacturerAndModel(manufacturer, model, productionYear);
        }
        // 결과 반환
        return ResponseEntity.ok(cars);
    }

    /**
     * 차 정보 수정
     * @param carId
     * @param updatedCar
     * @return
     */
    @PutMapping("/update/{carId}")
    public ResponseEntity<Object> updateCar(@PathVariable Long carId, @RequestBody Car updatedCar) {
        return carService.updateCar(carId, updatedCar);
    }
}