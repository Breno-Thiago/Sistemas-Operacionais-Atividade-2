//IMPLEMENTAÇÃO DO ARRAYLIST THREAD-SAFE
//         UTILIZANDO SEMÁFOROS
//Por: Arthur Felipe Dantas Melo - 202400028730
//     Breno Thiago Argemiro Santos - 202400028786
//     Beatriz Baroni Sousa - 202400028768
//     Gustavo Gomes Tavares - 202400028848

import java.util.ArrayList;
import java.util.concurrent.Semaphore; //uso dos semáforos

public class ThreadSafeArrayList<T> {

    private final ArrayList<T> array; //ArrayList padrão

    //Atributos de proteção
    private final Semaphore write = new Semaphore(1); //Semáforo para controle da escrita
    private final Semaphore readCountProtect = new Semaphore(1); // Semáforo para o contador de leituras
    private int readCount = 0;

    //construtor para definir tipo
    public ThreadSafeArrayList(Class<T> cls){
        this.array = new ArrayList<T>();
    }


    //função de leitura de item em determinado índice
    public T getElement(int index){
        try{
            //semáforo bloqueia a modificação de readcount
            readCountProtect.acquire();
            readCount++;

            //se não houver mais nenhuma thread realizando leitura, bloqueia a escrita
            //caso contrário, a escrita já estava bloqueada
            if(readCount == 1) write.acquire();

            //libera a modificação de readcount
            readCountProtect.release();

            //lógica de obtenção do item
            try{
                if(index >= 0 && index < array.size()){
                    return array.get(index);
                }
                return null;//
            }
            //finally é executado antes do return, para diminuir o readcount, utilizando o semáforo
            finally {
                readCountProtect.acquire();
                readCount--;
                if(readCount == 0) write.release();

                readCountProtect.release();
            }
        }
        //caso não seja possível prosseguir, a thread de leitura é interrompida
        catch (InterruptedException e ){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Read thread interrupted", e);
        }
    }

    //função de adição de novo elemento ao fim da lista
    public void addElement(T element){
        try {
            //semáforo de escrita bloqueia outras escritas e operação é realizada
            write.acquire();
            try {
                array.add(element);
            }
            finally {
                write.release(); //semáforo de escrita é liberado
            }
        }

        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Write Thread interrupted", e);
        }
    }


    //OUTRAS FUNÇÕES QUE ENVOLVEM ESCRITA ABAIXO (seguindo a mesma lógica de this.addElement())

    //Adiciona um elemento em um índice específico da lista
    public void setElement(int index, T element ){
        try{
            write.acquire();
            try {
                array.set(index, element);
            }
            finally {
                write.release();
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Write Thread interrupted", e);
        }
    }

    //remove um elemento de um índice específico da lista
    public T removeElement(int index){
        try{
            write.acquire();
            T result;
            try {
                result = array.remove(index);
            } finally {
                write.release();
            }
            return result;
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Write/Remove Thread interrupted", e);
        }
    }

    //OUTRAS FUNÇÕES QUE ENVOLVEM ESCRITA ABAIXO (seguindo a mesma lógica de this.getElement())

    //Retorna a quantidade de itens na lista
    public int arraySize(){
        try{
            readCountProtect.acquire();
            readCount++;

            if(readCount == 1) write.acquire();

            readCountProtect.release();
            try {
                return array.size();
            }
            finally {
                readCountProtect.acquire();
                readCount--;

                if(readCount == 0) write.release();

                readCountProtect.release();
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Read Thread interrupted", e);
        }
    }

    //retorna o índice de um elemento específico na lista
    public int indexOf(T element){
        try{
            readCountProtect.acquire();
            readCount++;
            if(readCount == 1) write.acquire();

            readCountProtect.release();
            try {
                return array.indexOf(element);
            }finally {
                readCountProtect.acquire();
                readCount--;
                if (readCount == 0)  write.release();

                readCountProtect.release();
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Read Thread interrupted", e);
        }
    }

    //retorna um valor lógico 1 caso a lista esteja vazia
    public boolean isEmpty(){
        try{
            readCountProtect.acquire();
            readCount++;
            if(readCount == 1){
                write.acquire();
            }
            readCountProtect.release();
            try{
                return array.isEmpty();
            }
            finally {
                readCountProtect.acquire();
                readCount--;
                if (readCount == 0) write.release();

                readCountProtect.release();
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Read Thread interrupted", e);
        }
    }
}
