/*
 * @Author: phanpc@gmail.com 
 * @Date: 2022-11-13 09:25:30 
 * @Last Modified by: phanpc@gmail.com
 * @Last Modified time: 2024-11-13 09:55:39
 */
package com.phanpc.asyncservice.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull; // Add this import statement
import java.io.Serializable;

@Data
@EqualsAndHashCode
@Entity
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column (name = "ID", nullable = false)
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(nullable=false)
    private String manufacturer;

    @NotNull
    @Column(nullable=false)
    private String model;

    @NotNull
    @Column(nullable=false)
    private String type;

}
