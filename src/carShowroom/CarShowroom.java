package carShowroom;

public class CarShowroom {
    final static int amountOfIterrations = 10;
    final static int manufactoringTime = 300;

    public static void main(String[] args) {
        Cars cars = new Cars();
        Runnable runnable = () -> {
            for (int i = 0; i < amountOfIterrations; i++) {
                String customer = Thread.currentThread().getName();
                synchronized (cars) {
                    System.out.println(customer + " пришел в салон");
                    if (cars.isShowroomEmpty()) {
                        try {
                            System.out.println("Пока нет свободных машин");
                            cars.wait();
                        } catch (InterruptedException e) {
                            System.out.println("Больше машин не будет, " + customer + " пошел домой пешком!");
                            return;
                        }
                    }
                    System.out.println(customer + " купил " + cars.buyCar() + " и уехал!");
                }
            }
        };

        Thread client01 = new Thread(runnable, "Покупатель 01");
        Thread client02 = new Thread(runnable, "Покупатель 02");
        Thread client03 = new Thread(runnable, "Покупатель 03");

        Thread showroom = new Thread(() -> {
            for (int i = 0; i < amountOfIterrations; i++) {
                synchronized (cars) {
                    System.out.println("Привезли в шоу-рум " + cars.orderCar());
                    cars.notify();
                }
                try {
                    Thread.sleep(manufactoringTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
            checkAndStopClient(client01);
            checkAndStopClient(client02);
            checkAndStopClient(client03);
        });

        showroom.start();
        client01.start();
        client02.start();
        client03.start();

    }

    private static void checkAndStopClient(Thread client) {
        if(client.getState() == Thread.State.WAITING){
            client.interrupt();
        }
    }
}
