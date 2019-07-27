package com.haojg.spi;

import com.haojg.spi.car.Car;
import com.haojg.spi.loader.HaojgExtensionLoader;
import org.junit.Test;

public class SPITest {

    @Test
    public void test() {
        Car car = HaojgExtensionLoader.getExtensionLoader(Car.class).getExtension("aoDi");

        System.out.println(car);
        car.drive();

        car = HaojgExtensionLoader.getExtensionLoader(Car.class).getExtension("benChi");
        System.out.println(car);
        car.drive();

    }
}
