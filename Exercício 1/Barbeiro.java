import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Barbeiro implements Runnable {

    private static final int MIN_CORTE = 5_000;
    private static final int MAX_CORTE = 15_000;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final int id;
    private final EstadoBarbearia estado;

    public Barbeiro(int id, EstadoBarbearia estado) {
        this.id     = id;
        this.estado = estado;
    }

    private void log(String msg) {
        System.out.printf("[%s] %s%n", LocalTime.now().format(FMT), msg);
    }

    @Override
    public void run() {
        log(String.format("Barbeiro %d abre a barbearia e vai dormir...", id));
        estado.barbeirosprontos.countDown();

        while (true) {
            try {
                // Dorme até um cliente aparecer
                estado.clientesEsperando.acquire();

                //Região crítica: retira cliente da fila de espera
                estado.mutex.acquire();
                estado.esperando.decrementAndGet();
                estado.barbeirosDisponiveis.release(); // libera o cliente para sentar
                estado.mutex.release();

                // Corta o cabelo em um tempo aleatório entre 5 e 15 segundos
                int tempo = MIN_CORTE + (int)(Math.random() * (MAX_CORTE - MIN_CORTE + 1));
                log(String.format(
                        "Barbeiro %d cortando cabelo (~%ds) | fila: %d",
                        id, tempo / 1000, estado.esperando.get()));

                Thread.sleep(tempo);

                int total = estado.totalAtendidos.incrementAndGet();
                log(String.format(
                        "Barbeiro %d terminou o corte | total atendidos: %d",
                        id, total));

            } catch (InterruptedException e) {
                // não tem mais ninguem querendo cortar cabelo
                log(String.format("Barbeiro %d encerrando expediente.", id));
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}