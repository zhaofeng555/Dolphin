package com.haojg.spi.car.impl;

import com.haojg.spi.car.Car;

public class BenChi implements Car {
    @Override
    public void drive() {
        System.out.println("奔驰轿车……");
    }
}
