package employee;

import entity.Host;
import entity.Keyboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Employee implements Runnable{
    List<Host> hosts;
    List<Keyboard> keyboards;
    private String name;
    private Host host;
    private Keyboard keyboard;

    private int times;
    private final int restTime = 10000;
    private final int workTime = 6000;

    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    public Employee(String name, List<Host> hosts, List<Keyboard> keyboards) {
        this.name = name;
        this.hosts = hosts;
        this.keyboards = keyboards;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void rest() {
        lock2.lock();
        hosts.add(host);
        keyboards.add(keyboard);
        lock2.unlock();
        host = null;
        keyboard = null;
        System.out.println(name + "开始休息,已工作" + ++ times + "次");

        try {
            Thread.sleep(restTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void work() {
        System.out.println(name + "开始使用" + host.getNumber() + "号主机和" + keyboard.getNumber() + "号键盘coding");
    }

    public void run() {
        while(true) {
            try {
                lock1.lock();
                if (!hosts.isEmpty() && !keyboards.isEmpty()) {
                    Host host = hosts.remove(0);
                    setHost(host);
                    Keyboard keyboard = keyboards.remove(0);
                    setKeyboard(keyboard);
                    work();
                    lock1.unlock();
                    try {
                        int time = new Double(Math.random() * 10000 + 5).intValue();
                        Thread.sleep(time);
                        rest();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock1.unlock();
            }
        }
    }

    public static void main(String[] args) {
        int M = 20;
        int N = 10;
        int Y = 20;
        List<Host> hosts = new ArrayList<Host>();
        List<Keyboard> keyboards = new ArrayList<Keyboard>();
        for(int i = 0; i < M; i ++) {
            hosts.add(new Host(i));
        }
        for(int i = 0; i < N; i ++) {
            keyboards.add(new Keyboard(i));
        }
//        ExecutorService executorService = new ThreadPoolExecutor(1, Y, 10,
//                TimeUnit.SECONDS, new LinkedBlockingQueue(20));
        for(int i = 0; i < Y; i ++) {
            Thread thread = new Thread(new Employee("员工" + i, hosts, keyboards));
            thread.start();
//            executorService.execute(new Employee("员工" + i, hosts, keyboards));
        }
    }
}
