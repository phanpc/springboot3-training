package com.phanpc.asyncservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.phanpc.asyncservice.dao.entity.Car;
import com.phanpc.asyncservice.dao.repository.CarRepository;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private AsyncCarService asyncCarService;

    //@Async
    public void saveCars(final InputStream inputStream) throws Exception {
//        CompletableFuture<List<Car>> cars = 
        // phanpc: muốn call 1 phương thức mà chạy async phải call qua tên 1 service, nếu call với con trỏ "this" 
        // thì method đó sẽ ko chạy async được
        asyncCarService.saveCarsAsync(inputStream);
    }
    
    @Async
    public CompletableFuture<List<Car>> getAllCars() {

        LOGGER.info("Request to get a list of cars");

        final List<Car> cars = carRepository.findAll();
        return CompletableFuture.completedFuture(cars);
    }
}
