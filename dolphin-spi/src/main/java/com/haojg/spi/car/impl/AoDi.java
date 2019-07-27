package com.haojg.spi.car.impl;

import com.haojg.spi.car.Car;

public class AoDi implements Car {
    @Override
    public void drive() {
        System.out.println("奥迪轿车……");
    }
}
