import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Cliente implements Runnable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final int id;
    private final EstadoBarbearia estado;

    public Cliente(int id, EstadoBarbearia estado) {
        this.id     = id;
        this.estado = estado;
    }

    private void log(String msg) {
        System.out.printf("[%s] %s%n", LocalTime.now().format(FMT), msg);
    }

    @Override
    public void run() {
        try {
            // Região crítica: verifica e ocupa cadeira de esper
            estado.mutex.acquire();

            if (estado.esperando.get() < estado.maxCadeiras) {
                int fila = estado.esperando.incrementAndGet();
                log(String.format(
                        "Cliente %d entrou na sala de espera | fila: %d/%d",
                        id, fila, estado.maxCadeiras));

                // acorda um barbeiro
                estado.clientesEsperando.release();
                estado.mutex.release();

                // espera um barbeiro ficar livre
                estado.barbeirosDisponiveis.acquire();
                log(String.format("Cliente %d sentou na cadeira do barbeiro", id));

            } else {
                // se a barbearia estiver lotada o cliente vai embora ────────────────────
                int rejeitados = estado.totalRejeitados.incrementAndGet();
                log(String.format(
                        "Cliente %d foi embora — barbearia lotada! (%d rejeitados até agora)",
                        id, rejeitados));
                estado.mutex.release();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}