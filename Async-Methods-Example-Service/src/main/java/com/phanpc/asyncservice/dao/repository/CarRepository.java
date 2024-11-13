/*
 * @Author: phanpc@gmail.com 
 * @Date: 2022-11-13 09:28:12 
 * @Last Modified by:   phanpc@gmail.com 
 * @Last Modified time: 2024-11-13 09:28:12 
 */
package com.phanpc.asyncservice.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phanpc.asyncservice.dao.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

}
