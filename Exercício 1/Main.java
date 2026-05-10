import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final int NUM_BARBEIROS   = 2;
    private static final int MAX_CADEIRAS    = 10;

    //está tudo em milisegundos
    private static final int DURACAO_SIMULACAO  = 60_000;
    private static final int MIN_CHEGADA  = 4_000;
    private static final int MAX_CHEGADA  = 6_000;
    // espera cortar o cabelo de quem já está na cadeira (o corte dura no máximo 15 segundos)
    private static final int ESPERA_FINAL = 20_000;

    // só pra imprimir a timestamp
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static void log(String msg) {
        System.out.printf("[%s] %s%n", LocalTime.now().format(FMT), msg);
    }

    public static void main(String[] args) throws InterruptedException {

        // ── Cabeçalho ────────────────────────────────────────────────────────
        System.out.println("------------------------------------------------------");
        System.out.println("          BARBEARIA DO DORMINHOCO                 ");
        System.out.printf ("  Barbeiros: %-3d  Cadeiras de espera: %-3d        %n",
                NUM_BARBEIROS, MAX_CADEIRAS);
        System.out.println("------------------------------------------------------");
        System.out.println();

        // Cria o estado da babearia
        EstadoBarbearia estado = new EstadoBarbearia(NUM_BARBEIROS, MAX_CADEIRAS);

        // Inicia threads dos barbeiros
        for (int i = 1; i <= NUM_BARBEIROS; i++) {
            Thread t = new Thread(new Barbeiro(i, estado), "Barbeiro-" + i);
            t.setDaemon(true); // encerra junto com a JVM ao terminar o main
            t.start();
        }

        // Aguarda todos os barbeiros estarem prontos para os clientes começarem a chegar
        estado.barbeirosprontos.await();
        log("Barbearia aberta!");
        System.out.println();

        // Fica gerando clientes durante o período da simulação
        long inicio = System.currentTimeMillis();

        while (System.currentTimeMillis() - inicio < DURACAO_SIMULACAO) {
            int id = estado.clienteIdCounter.getAndIncrement();
            new Thread(new Cliente(id, estado), "Cliente-" + id).start();

            // gera os clientes aleatóriamente
            int intervalo = MIN_CHEGADA +
                    (int)(Math.random() * (MAX_CHEGADA - MIN_CHEGADA + 1));
            Thread.sleep(intervalo);
        }

        //Aguarda os últimos cortes terminarem (se tiver alguém cortando o cabelo ainda espera 20 segundos (um corte dura no máximo 15)
        log("Expediente encerrado. Aguardando últimos atendimentos...");
        Thread.sleep(ESPERA_FINAL);

        // ── Resumo final ─────────────────────────────────────────────────────
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("                  RESUMO FINAL                      ");
        System.out.printf ("           Clientes atendidos : %-3d                         %n",
                estado.totalAtendidos.get());
        System.out.printf ("           Clientes rejeitados: %-3d                         %n",
                estado.totalRejeitados.get());
        System.out.printf ("           Total de clientes  : %-3d                        %n",
                estado.clienteIdCounter.get() - 1);
        System.out.println("----------------------------------------------------");
    }
}