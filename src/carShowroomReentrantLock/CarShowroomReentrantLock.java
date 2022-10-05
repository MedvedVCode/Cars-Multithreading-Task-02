package carShowroomReentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CarShowroomReentrantLock {
    final static int amountOfIterrations = 10;
    final static int manufactoringTime = 300;

    public static void main(String[] args) {
        ReentrantLock locker = new ReentrantLock(true);
        Condition condition = locker.newCondition();
        Cars cars = new Cars();

        Runnable clientsOfShowroom = () -> {
            for (int i = 0; i < amountOfIterrations; i++) {
                locker.lock();
                try {
                    String customer = Thread.currentThread().getName();
                    System.out.println(customer + " пришел в салон");
                    while (cars.isShowroomEmpty() && cars.isOpened()) {
                        System.out.printf("?? %s ждет завоз\n", customer);
                        condition.await();
                    }
                    if(cars.isOpened()){
                        System.out.println(customer + " купил " + cars.buyCar() + " и уехал!");
                    }else{
                        System.out.println(customer + " ушел домой. Магазин закрыт");
                        i = amountOfIterrations;
                        Thread.currentThread().interrupt();
                    }
                    condition.signalAll();
                } catch (InterruptedException e) {
                } finally {
                    locker.unlock();
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
                    while (!cars.isShowroomEmpty()) {
                        condition.await();
                    }
                    System.out.println("Привезли в шоу-рум " + cars.orderCar());
                    condition.signalAll();
                    Thread.sleep(manufactoringTime);
                } catch (InterruptedException e) {
                    System.out.println("Что-то сломалось в шоу-руме");
                    return;
                } finally {
                    locker.unlock();
                }
            }
            locker.lock();
            try {
                while (!cars.isShowroomEmpty()) {
                    condition.await();
                }
                cars.setOpened(false);
                condition.signalAll();
            } catch (InterruptedException e){

            }finally {
                locker.unlock();
            }
        });

        showroom.start();
        client01.start();
        client02.start();
        client03.start();
    }
}
