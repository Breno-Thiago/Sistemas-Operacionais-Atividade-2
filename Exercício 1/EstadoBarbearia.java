import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/*
Mantém todo o estado compartilhado da barbearia:
semáforos, mutex e contadores acessados por Barbeiro e Cliente.
 */
public class EstadoBarbearia {

    // ── Configurações ────────────────────────────────────────────────────────
    public final int numBarbeiros;
    public final int maxCadeiras;

    //Garante que todos os barbeiros estejam prontos antes de abrir para clientes
    public final CountDownLatch barbeirosprontos;

    //Semáforos
    // Sinaliza que há um cliente esperando para ser atendido (inicia em 0 = barbeiros dormem)
    public final Semaphore clientesEsperando;

    // Sinaliza que há um barbeiro disponível para o cliente sentar (inicia em 0)
    public final Semaphore barbeirosDisponiveis;

    // Mutex que está protegendo a região crítica
    public final Semaphore mutex;

    // Conta o número de clientes na sala de espera
    public final AtomicInteger esperando     = new AtomicInteger(0);

    // Gera de IDs únicos para cada cliente
    public final AtomicInteger clienteIdCounter = new AtomicInteger(1);

    // conta quanros clientes foram atendidos
    public final AtomicInteger totalAtendidos   = new AtomicInteger(0);

    // Conta quantos clientes foram embora porque a fila estva cheia
    public final AtomicInteger totalRejeitados  = new AtomicInteger(0);

    // ── Construtor ───────────────────────────────────────────────────────────
    public EstadoBarbearia(int numBarbeiros, int maxCadeiras) {
        this.numBarbeiros = numBarbeiros;
        this.maxCadeiras  = maxCadeiras;

        this.barbeirosprontos     = new CountDownLatch(numBarbeiros);
        this.clientesEsperando    = new Semaphore(0);
        this.barbeirosDisponiveis = new Semaphore(0);
        this.mutex                = new Semaphore(1);
    }
}