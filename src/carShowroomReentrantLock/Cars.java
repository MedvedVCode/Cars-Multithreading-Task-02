package carShowroomReentrantLock;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Cars {
    final private int indexToRemove = 0;
    final private int lowerBoundOfCarName = 0;
    final private int upperBoundOfCarName = 3;
    private boolean isOpened = true;
    private List<CarName> cars;

    public Cars() {
        cars = new LinkedList<>();
    }

    public CarName orderCar() {
        Random random = new Random();
        CarName carName = CarName.values()[
                random.nextInt(lowerBoundOfCarName, upperBoundOfCarName)];
        cars.add(indexToRemove, carName);
        return carName;
    }

    public boolean isShowroomEmpty() {
        return cars.isEmpty();
    }

    public CarName buyCar() {
        return cars.remove(indexToRemove);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void closeShowroom() {
        isOpened = false;
    }
}
