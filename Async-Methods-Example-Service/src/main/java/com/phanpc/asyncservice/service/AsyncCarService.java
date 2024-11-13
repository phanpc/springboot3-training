/*
 * @Author: phanpc@gmail.com 
 * @Date: 2022-11-13 09:28:30 
 * @Last Modified by: phanpc@gmail.com
 * @Last Modified time: 2024-11-13 09:34:49
 */
package com.phanpc.asyncservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.phanpc.asyncservice.dao.entity.Car;
import com.phanpc.asyncservice.dao.repository.CarRepository;

@Service
public class AsyncCarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);
    @Autowired
    private CarRepository carRepository;
    
    @Async
    public CompletableFuture<List<Car>> saveCarsAsync(final InputStream inputStream) throws Exception {
        final long start = System.currentTimeMillis();

        List<Car> cars = parseCSVFile(inputStream);

        LOGGER.info("saveCarsAsync Saving a list of cars of size {} records", cars.size());

        cars = carRepository.saveAll(cars);

        LOGGER.info("saveCarsAsync Elapsed time: {}, num of records={}", (System.currentTimeMillis() - start), cars.size());
        
        return CompletableFuture.completedFuture(cars);
    }
    
    // phanpc: this is synchrnous method that will be called by async method saveCarsAsync
    private List<Car> parseCSVFile(final InputStream inputStream) throws Exception {
        final List<Car> cars=new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line=br.readLine()) != null) {
                    final String[] data=line.split(";");
                    final Car car=new Car();
                    car.setManufacturer(data[0]);
                    car.setModel(data[1]);
                    car.setType(data[2]);
                    cars.add(car);
                }
                return cars;
            }
        } catch(final IOException e) {
            LOGGER.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}
