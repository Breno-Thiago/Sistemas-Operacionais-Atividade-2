public class ThreadLeitura implements  Runnable{
    public ThreadSafeArrayList<Integer> arrei;

    public ThreadLeitura(ThreadSafeArrayList<Integer> array){
        arrei = array;
    }
    @Override
    public void run() {
        for(int i = 0; i<20; i++) {
            System.out.printf("%d (%s)%n", arrei.getElement(i),Thread.currentThread().getName());

        }
        System.out.printf("Tamanho do array: %d%n", arrei.arraySize());
        System.out.println("Thread de Leitura finalizada");
    }
}
