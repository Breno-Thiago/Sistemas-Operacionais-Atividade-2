public class Main{
    public static void main(String[] args){
        System.out.println("Olá");
        ThreadSafeArrayList<Integer> array = new ThreadSafeArrayList<>(Integer.class);
        ThreadEscrita w1 = new ThreadEscrita(array);
        ThreadLeitura r2 = new ThreadLeitura(array);
        ThreadEscrita w2 = new ThreadEscrita(array);
        ThreadLeitura r1 = new ThreadLeitura(array);

        new Thread(w1).start();
        new Thread(r2).start();
        new Thread(w2).start();
        new Thread(r1).start();


    }
}

