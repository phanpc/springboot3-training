/*
 * @Author: phanpc@gmail.com 
 * @Date: 2022-11-13 09:24:27 
 * @Last Modified by: phanpc@gmail.com
 * @Last Modified time: 2024-11-13 10:06:36
 */
package com.phanpc.asyncservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.phanpc.asyncservice.dao.entity.Car;
import com.phanpc.asyncservice.service.CarService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@RequestMapping("/api/car")
public class CarController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);

    @Autowired
    private CarService carService;

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody ResponseEntity uploadFile(@RequestParam(value = "files") MultipartFile[] files) {
        LOGGER.info("uploadFile begin");
        try {
            for (final MultipartFile file : files) {
                //phanpc: this call is async processing
                carService.saveCars(file.getInputStream());
            }
            // phanpc: this log will be printed before the async task is completed, 
            // this means the async task (carService.saveCars) is running in background
            LOGGER.info("uploadFile end");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.GET, consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    CompletableFuture<ResponseEntity> getAllCars() {
        //phanpc: this is call to async task
        return carService.getAllCars().<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleGetCarFailure);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/union", method = RequestMethod.GET, consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity getAllCars_union() {
        //phanpc: this is call to async tasks
        //three task run concurrently
        try {
            LOGGER.info("getAllCars_union begin");
            CompletableFuture<List<Car>> cars1 = carService.getAllCars();
            CompletableFuture<List<Car>> cars2 = carService.getAllCars();
            CompletableFuture<List<Car>> cars3 = carService.getAllCars();

            LOGGER.info("getAllCars_union 2");
            CompletableFuture.allOf(cars1, cars2, cars3).join();
            List<Car> totalCar = cars1.get();
            totalCar.addAll(cars2.get());
            totalCar.addAll(cars3.get());            
            
            //cars1.<ResponseEntity>thenApply(ResponseEntity::ok);
            
            LOGGER.info("getAllCars_union 3, total car={}", totalCar.size());
            
            return ResponseEntity.ok(totalCar);

            //LOGGER.info("getAllCars_union end");
        } catch (Exception e1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }        
    }

    private static Function<Throwable, ResponseEntity<? extends List<Car>>> handleGetCarFailure = throwable -> {
        LOGGER.error("Failed to read records: {}", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
}
