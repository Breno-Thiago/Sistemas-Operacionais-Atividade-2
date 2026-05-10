import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final int[] TAMANHOS = {1_000, 5_000, 10_000};
    private static final int THREADS = 16;
    private static final int OPERACOES_POR_THREAD = 1_000;
    private static final String ARQUIVO_RESULTADO = "resultado_ultima_execucao.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final StringBuilder saida = new StringBuilder();

    public static void main(String[] args) throws InterruptedException, IOException {
        Locale.setDefault(Locale.US);

        imprimir("EXERCICIO 3 - COMPARACAO DE DESEMPENHO");
        imprimir("Data e horario da execucao: " + LocalDateTime.now().format(FMT));
        imprimir("");

        executarTesteUmaThread();

        imprimir("");
        executarTesteDezesseisThreads();

        salvarResultado();
    }

    private static void executarTesteUmaThread() {
        imprimir("1 THREAD - ArrayList original x ThreadSafeArrayList");
        imprimir("Lista A = ArrayList original | Lista B = ThreadSafeArrayList");
        imprimirCabecalho();

        for (int tamanho : TAMANHOS) {
            Resultado arrayInsercao = medir(tamanho, () -> {
                ArrayList<Integer> lista = new ArrayList<Integer>();
                for (int i = 0; i < tamanho; i++) {
                    lista.add(i);
                }
            });

            Resultado safeInsercao = medir(tamanho, () -> {
                ThreadSafeArrayList<Integer> lista = new ThreadSafeArrayList<Integer>();
                for (int i = 0; i < tamanho; i++) {
                    lista.addElement(i);
                }
            });

            imprimirLinha("Insercao", tamanho, arrayInsercao, safeInsercao);

            ArrayList<Integer> arrayBusca = criarArrayList(tamanho);
            ThreadSafeArrayList<Integer> safeBusca = criarThreadSafeArrayList(tamanho);
            int[] valoresBusca = valoresAleatorios(tamanho, tamanho, 10);

            Resultado arrayBuscaResultado = medir(tamanho, () -> {
                for (int valor : valoresBusca) {
                    arrayBusca.indexOf(valor);
                }
            });

            Resultado safeBuscaResultado = medir(tamanho, () -> {
                for (int valor : valoresBusca) {
                    safeBusca.indexOf(valor);
                }
            });

            imprimirLinha("Busca", tamanho, arrayBuscaResultado, safeBuscaResultado);

            ArrayList<Integer> arrayRemocao = criarArrayList(tamanho);
            ThreadSafeArrayList<Integer> safeRemocao = criarThreadSafeArrayList(tamanho);
            ArrayList<Integer> valoresRemocao = valoresEmbaralhados(tamanho, 20);

            Resultado arrayRemocaoResultado = medir(tamanho, () -> {
                for (Integer valor : valoresRemocao) {
                    arrayRemocao.remove(valor);
                }
            });

            Resultado safeRemocaoResultado = medir(tamanho, () -> {
                for (Integer valor : valoresRemocao) {
                    safeRemocao.removeValue(valor);
                }
            });

            imprimirLinha("Remocao", tamanho, arrayRemocaoResultado, safeRemocaoResultado);
        }
    }

    private static void executarTesteDezesseisThreads() throws InterruptedException {
        imprimir("16 THREADS - Vector x ThreadSafeArrayList");
        imprimir("Lista A = Vector | Lista B = ThreadSafeArrayList");
        imprimirCabecalho();

        int totalOperacoes = THREADS * OPERACOES_POR_THREAD;

        for (int tamanho : TAMANHOS) {
            Resultado vectorInsercao = medirComThreads(totalOperacoes, () -> {
                Vector<Integer> lista = criarVector(tamanho);
                executarThreads(threadId -> {
                    Random random = new Random(1000L + threadId);
                    for (int i = 0; i < OPERACOES_POR_THREAD; i++) {
                        lista.add(random.nextInt());
                    }
                });
            });

            Resultado safeInsercao = medirComThreads(totalOperacoes, () -> {
                ThreadSafeArrayList<Integer> lista = criarThreadSafeArrayList(tamanho);
                executarThreads(threadId -> {
                    Random random = new Random(2000L + threadId);
                    for (int i = 0; i < OPERACOES_POR_THREAD; i++) {
                        lista.addElement(random.nextInt());
                    }
                });
            });

            imprimirLinha("Insercao", tamanho, vectorInsercao, safeInsercao);

            Vector<Integer> vectorBusca = criarVector(tamanho);
            ThreadSafeArrayList<Integer> safeBusca = criarThreadSafeArrayList(tamanho);

            Resultado vectorBuscaResultado = medirComThreads(totalOperacoes, () -> {
                executarThreads(threadId -> {
                    Random random = new Random(3000L + threadId);
                    for (int i = 0; i < OPERACOES_POR_THREAD; i++) {
                        vectorBusca.indexOf(random.nextInt(tamanho));
                    }
                });
            });

            Resultado safeBuscaResultado = medirComThreads(totalOperacoes, () -> {
                executarThreads(threadId -> {
                    Random random = new Random(4000L + threadId);
                    for (int i = 0; i < OPERACOES_POR_THREAD; i++) {
                        safeBusca.indexOf(random.nextInt(tamanho));
                    }
                });
            });

            imprimirLinha("Busca", tamanho, vectorBuscaResultado, safeBuscaResultado);

            Vector<Integer> vectorRemocao = criarVector(tamanho + totalOperacoes);
            ThreadSafeArrayList<Integer> safeRemocao = criarThreadSafeArrayList(tamanho + totalOperacoes);

            Resultado vectorRemocaoResultado = medirComThreads(totalOperacoes, () -> {
                executarThreads(threadId -> {
                    ArrayList<Integer> valores = valoresDaThread(threadId, totalOperacoes, 5000L);
                    for (Integer valor : valores) {
                        vectorRemocao.remove(valor);
                    }
                });
            });

            Resultado safeRemocaoResultado = medirComThreads(totalOperacoes, () -> {
                executarThreads(threadId -> {
                    ArrayList<Integer> valores = valoresDaThread(threadId, totalOperacoes, 6000L);
                    for (Integer valor : valores) {
                        safeRemocao.removeValue(valor);
                    }
                });
            });

            imprimirLinha("Remocao", tamanho, vectorRemocaoResultado, safeRemocaoResultado);
        }
    }

    private static void executarThreads(OperacaoThread operacao) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch fim = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    inicio.await();
                    operacao.executar(threadId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    fim.countDown();
                }
            });
            threads[i].start();
        }

        inicio.countDown();
        fim.await();
    }

    private static Resultado medir(int operacoes, Operacao operacao) {
        long inicio = System.nanoTime();
        operacao.executar();
        long fim = System.nanoTime();
        return new Resultado(operacoes, fim - inicio);
    }

    private static Resultado medirComThreads(int operacoes, OperacaoComInterrupcao operacao) throws InterruptedException {
        long inicio = System.nanoTime();
        operacao.executar();
        long fim = System.nanoTime();
        return new Resultado(operacoes, fim - inicio);
    }

    private static ArrayList<Integer> criarArrayList(int tamanho) {
        ArrayList<Integer> lista = new ArrayList<Integer>();
        for (int i = 0; i < tamanho; i++) {
            lista.add(i);
        }
        return lista;
    }

    private static ThreadSafeArrayList<Integer> criarThreadSafeArrayList(int tamanho) {
        ThreadSafeArrayList<Integer> lista = new ThreadSafeArrayList<Integer>();
        for (int i = 0; i < tamanho; i++) {
            lista.addElement(i);
        }
        return lista;
    }

    private static Vector<Integer> criarVector(int tamanho) {
        Vector<Integer> lista = new Vector<Integer>();
        for (int i = 0; i < tamanho; i++) {
            lista.add(i);
        }
        return lista;
    }

    private static int[] valoresAleatorios(int quantidade, int limite, long seed) {
        int[] valores = new int[quantidade];
        Random random = new Random(seed);
        for (int i = 0; i < quantidade; i++) {
            valores[i] = random.nextInt(limite);
        }
        return valores;
    }

    private static ArrayList<Integer> valoresEmbaralhados(int quantidade, long seed) {
        ArrayList<Integer> valores = new ArrayList<Integer>();
        for (int i = 0; i < quantidade; i++) {
            valores.add(i);
        }
        Collections.shuffle(valores, new Random(seed));
        return valores;
    }

    private static ArrayList<Integer> valoresDaThread(int threadId, int totalOperacoes, long seed) {
        ArrayList<Integer> valores = new ArrayList<Integer>();
        int inicio = threadId * OPERACOES_POR_THREAD;
        int fim = inicio + OPERACOES_POR_THREAD;

        for (int i = inicio; i < fim && i < totalOperacoes; i++) {
            valores.add(i);
        }

        Collections.shuffle(valores, new Random(seed + threadId));
        return valores;
    }

    private static void imprimirCabecalho() {
        imprimirFormatado("%-10s %10s %14s %16s %14s %16s%n",
                "Operacao", "Tamanho", "Lista A(ms)", "Lista A(op/s)", "Lista B(ms)", "Lista B(op/s)");
    }

    private static void imprimirLinha(String operacao, int tamanho, Resultado a, Resultado b) {
        imprimirFormatado("%-10s %10d %14.3f %16.0f %14.3f %16.0f%n",
                operacao, tamanho, a.milisegundos(), a.operacoesPorSegundo(),
                b.milisegundos(), b.operacoesPorSegundo());
    }

    private static void imprimir(String texto) {
        System.out.println(texto);
        saida.append(texto).append(System.lineSeparator());
    }

    private static void imprimirFormatado(String formato, Object... valores) {
        String texto = String.format(formato, valores);
        System.out.print(texto);
        saida.append(texto);
    }

    private static void salvarResultado() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_RESULTADO))) {
            writer.print(saida.toString());
        }

        System.out.println();
        System.out.println("Resultado salvo em: " + ARQUIVO_RESULTADO);
    }

    private interface Operacao {
        void executar();
    }

    private interface OperacaoComInterrupcao {
        void executar() throws InterruptedException;
    }

    private interface OperacaoThread {
        void executar(int threadId) throws InterruptedException;
    }

    private static class Resultado {
        private final int operacoes;
        private final long nanos;

        Resultado(int operacoes, long nanos) {
            this.operacoes = operacoes;
            this.nanos = nanos;
        }

        double milisegundos() {
            return nanos / 1_000_000.0;
        }

        double operacoesPorSegundo() {
            return operacoes / (nanos / 1_000_000_000.0);
        }
    }
}
