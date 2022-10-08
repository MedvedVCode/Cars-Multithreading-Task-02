package carShowroomReentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarShowroomReentrantLock {
    final static int amountOfIterrations = 10;
    final static int manufactoringTime = 300;

    public static void main(String[] args) {
        Lock locker = new ReentrantLock(true);
        Condition condition = locker.newCondition();
        Cars cars = new Cars();

        Runnable clientsOfShowroom = () -> {
            String customer = Thread.currentThread().getName();
            locker.lock();
            while (cars.isOpened()) {
                try {
                    System.out.println(customer + " пришел в салон");
                    while (cars.isShowroomEmpty() && cars.isOpened()) {
                        System.out.printf("?? %s ждет завоз\n", customer);
                        condition.await();
                    }
                    if (cars.isOpened() && !cars.isShowroomEmpty()) {
                        System.out.println(customer + " купил " + cars.buyCar() + " и уехал!");
                    } else if (!cars.isOpened()){
                        condition.signal();
                        locker.unlock();
                    }
                } catch (InterruptedException e) {
                    System.out.println(customer + " расстроился");
                }
            }
        };

        Thread client01 = new Thread(clientsOfShowroom, "Модест");
        Thread client02 = new Thread(clientsOfShowroom, "Васян");
        Thread client03 = new Thread(clientsOfShowroom, "Анатолий Иванович");

        Thread showroom = new Thread(() -> {
            for (int i = 0; i < amountOfIterrations; i++) {
                locker.lock();
                try {
                    if (i == amountOfIterrations - 1) {
                        cars.closeShowroom();
                        System.out.println("Салон закрывается ");
                    } else {
                        System.out.println("Привезли в шоу-рум " + cars.orderCar());
                    }
                    condition.signal();
                    Thread.sleep(manufactoringTime);
                } catch (InterruptedException e) {
                    System.out.println("Что-то сломалось в шоу-руме");
                    return;
                } finally {
                    locker.unlock();
                }
            }
        }, "Шоу-рум");

        showroom.start();
        client01.start();
        client02.start();
        client03.start();
    }
}
