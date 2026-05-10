import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

// Classe que disciplina o acesso ao Banco de Dados
class BancoDeDados {

    // Semáforo para controlar o limite de 10 leituras.
    private final Semaphore semaforoLeitura = new Semaphore(10, true);

    // Mutex para garantir que apenas uma operação de escrita ocorra por vez
    private final Semaphore mutexEscrita = new Semaphore(1, true);

    // Simulando uma tabela do banco de dados
    private final List<String> tabela = new ArrayList<>();

    public BancoDeDados() {
        tabela.add("Registro Inicial");
    }

    // operacao de read

    public void read(int idThread) {
        boolean leituraAdquirida = false;
        try {
            semaforoLeitura.acquire(); // Consome 1 permissão
            leituraAdquirida = true;
            System.out.println("CONSULTA Thread " + idThread + " lendo. (Permissões de leitura restantes: " + semaforoLeitura.availablePermits() + ")");

            // Simula o tempo da consulta no banco
            Thread.sleep((long) (Math.random() * 2000 + 1000));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (leituraAdquirida) {
                System.out.println("FIM CONSULTA Thread " + idThread + " terminou a leitura.");
                semaforoLeitura.release(); // Devolve 1 permissão
            }
        }
    }

    // metodo auxiliar

    private void executarEscrita(String nomeOperacao, int idThread, Runnable acao) {
        boolean escritaAdquirida = false;
        boolean leiturasBloqueadas = false;
        try {
            System.out.println("AGUARDANDO Thread " + idThread + " quer fazer " + nomeOperacao + "...");

            mutexEscrita.acquire();      // Bloqueia outros escritores
            escritaAdquirida = true;
            semaforoLeitura.acquire(10); // Pega TODAS as 10 permissões de leitura (bloqueia leitores)
            leiturasBloqueadas = true;

            System.out.println("ESCRITA EXCLUSIVA Thread " + idThread + " INICIOU " + nomeOperacao + ".");

            // Executa a ação no banco (insert, update ou delete)
            acao.run();

            // Simula o tempo de escrita no banco
            Thread.sleep(3000);

            System.out.println("FIM ESCRITA Thread " + idThread + " FINALIZOU " + nomeOperacao + ". Banco liberado.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (leiturasBloqueadas) {
                semaforoLeitura.release(10); // Devolve as 10 permissões (libera leitores)
            }
            if (escritaAdquirida) {
                mutexEscrita.release();      // Libera para outro escritor
            }
        }
    }

    // operacoes CREATE, UPDATE e DELETE

    public void create(int idThread, String dado) {
        executarEscrita("CREATE", idThread, () -> tabela.add(dado));
    }

    public void update(int idThread, int index, String novoDado) {
        executarEscrita("UPDATE", idThread, () -> {
            if (index >= 0 && index < tabela.size()) {
                tabela.set(index, novoDado);
            }
        });
    }

    public void delete(int idThread, int index) {
        executarEscrita("DELETE", idThread, () -> {
            if (index >= 0 && index < tabela.size()) {
                tabela.remove(index);
            }
        });
    }
}
