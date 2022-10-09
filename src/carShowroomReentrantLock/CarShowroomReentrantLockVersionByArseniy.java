package carShowroomReentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CarShowroomReentrantLockVersionByArseniy {
    final static int amountOfIterrations = 10;
    final static int manufactoringTime = 300;
    final static ReentrantLock locker = new ReentrantLock(true);
    final static Condition condition = locker.newCondition();

    public static void main(String[] args) {

        Cars cars = new Cars();

        Runnable clientsOfShowroom = () -> {
            String customer = Thread.currentThread().getName();
            for (int i = 0; i < amountOfIterrations; i++)  {
                locker.lock();
                try {
                    System.out.println(" >> " + customer + " пришел в салон");
                    if (cars.isShowroomEmpty() && cars.isOpened()) {
                        System.out.printf(" ?? %s ждет завоз\n", customer);
                        condition.await();
                    }
                    System.out.println(" -- " + customer + " купил " + cars.buyCar() + " и уехал!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
                    System.out.println("++ Привезли в шоу-рум " + cars.orderCar());
                    condition.signal();
                    Thread.sleep(manufactoringTime);
                } catch (InterruptedException e) {
                    System.out.println("Что-то сломалось в шоу-руме");
                    return;
                } finally {
                    locker.unlock();
                }
            }
        });

        showroom.start();
        client01.start();
        client02.start();
        client03.start();
    }
}
