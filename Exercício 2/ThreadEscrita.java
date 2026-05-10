public class ThreadEscrita implements Runnable {
    public ThreadSafeArrayList<Integer> arrei;

    public ThreadEscrita(ThreadSafeArrayList<Integer> array){
        arrei = array;
    }

    @Override
    public void run() {
        for(int i = 0; i<10; i++){
            arrei.addElement(i);

        }
        arrei.arraySize();

        System.out.println("Thread de escrita finalizada");
    }


}
