import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) {

        var test = new Test();

        new Thread(() -> {
            for(int i = 0; i < 3; i ++){
                try {
                    Thread.sleep(100);
                    test.addCount();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for(int i = 0; i < 3; i ++){
                try {
                    Thread.sleep(100);
                    test.addCount();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

class Test{

    private static Integer count = 0;

    public static synchronized void addCount(){
        count++;
        System.out.println(Thread.currentThread() + " - " + count);
    }

    public synchronized void addCount2(){
        count++;
        System.out.println(Thread.currentThread() + " - " + count);
    }

//    public void addCount2(){
//        synchronized (count2){
//            count2++;
//            System.out.println(Thread.currentThread() + " - " + count2);
//        }
//    }
//
//    public synchronized void method2(){
//        // code
//    }

//    private Object object;
//
//    public Test(Object object){
//        this.object = object;
//    }
//
//    public void method2(){
//        synchronized (object){
//            //code
//        }
//    }
}
