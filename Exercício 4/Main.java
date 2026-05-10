//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        BancoDeDados bd = new BancoDeDados();

        System.out.println("Iniciando o sistema de Banco de Dados...\n");

        // 1. Disparar 12 threads de leitura para forçar o limite de 10
        for (int i = 1; i <= 12; i++) {
            final int id = i;
            new Thread(() -> bd.read(id)).start();
        }

        // Dar um pequeno tempo para as consultas começarem
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 2. Disparar operações de escrita (vão ter que esperar as leituras acabarem)
        new Thread(() -> bd.create(100, "Novo Registro A")).start();
        new Thread(() -> bd.update(101, 0, "Registro Atualizado")).start();

        // 3. Disparar mais leituras enquanto as escritas estão na fila/executando
        for (int i = 13; i <= 15; i++) {
            final int id = i;
            new Thread(() -> bd.read(id)).start();
        }
    }
}