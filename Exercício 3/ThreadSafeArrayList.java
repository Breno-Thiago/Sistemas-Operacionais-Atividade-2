import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ThreadSafeArrayList<T> {
    private final ArrayList<T> array;
    private final Semaphore write = new Semaphore(1);
    private final Semaphore readCountProtect = new Semaphore(1);
    private int readCount = 0;

    public ThreadSafeArrayList() {
        this.array = new ArrayList<T>();
    }

    public void addElement(T element) {
        try {
            write.acquire();
            try {
                array.add(element);
            } finally {
                write.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread de escrita interrompida", e);
        }
    }

    public T getElement(int index) {
        try {
            startRead();
            try {
                if (index >= 0 && index < array.size()) {
                    return array.get(index);
                }
                return null;
            } finally {
                finishRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread de leitura interrompida", e);
        }
    }

    public int indexOf(T element) {
        try {
            startRead();
            try {
                return array.indexOf(element);
            } finally {
                finishRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread de leitura interrompida", e);
        }
    }

    public boolean removeValue(T element) {
        try {
            write.acquire();
            try {
                return array.remove(element);
            } finally {
                write.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread de remocao interrompida", e);
        }
    }

    public int arraySize() {
        try {
            startRead();
            try {
                return array.size();
            } finally {
                finishRead();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread de leitura interrompida", e);
        }
    }

    private void startRead() throws InterruptedException {
        readCountProtect.acquire();
        readCount++;
        if (readCount == 1) {
            write.acquire();
        }
        readCountProtect.release();
    }

    private void finishRead() throws InterruptedException {
        readCountProtect.acquire();
        readCount--;
        if (readCount == 0) {
            write.release();
        }
        readCountProtect.release();
    }
}
