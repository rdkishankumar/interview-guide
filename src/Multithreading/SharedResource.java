package Multithreading;

public class SharedResource {
    private int data = 0;
    public synchronized int getData() {
        //Thread.currentThread().setName("Reader");
        System.out.println(Thread.currentThread().getName() + " Started reading");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " Finished reading| value = " + data);
        return data;
    }
    public synchronized void setData(int data) {
        System.out.println(Thread.currentThread().getName() + " Started writing");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " Finished writing | value = " + data);
        this.data = data;

    }

}
