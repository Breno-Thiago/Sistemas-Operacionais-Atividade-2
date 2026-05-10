# Sistemas Operacionais - Atividade 2

Atividade prática desenvolvida em Java para a disciplina de Sistemas Operacionais.

## Como executar

Entre na pasta do exercício desejado, compile os arquivos Java e execute a classe `Main`:

```bash
cd "Exercício 1"
javac *.java
java Main
```

O mesmo padrão pode ser usado para os demais exercícios, alterando apenas a pasta.

## Exercício 1

Implemente o problema do Barbeiro Dorminhoco.

Imagine que:

- Existem 2 barbeiros.
- A fila pode ter no máximo 10 clientes esperando.
- Caso o cliente chegue e a fila esteja cheia, exiba uma mensagem informativa.

Modele o seu programa para que:

- O corte de cabelo de um cliente demore um tempo aleatório entre 5s e 15s.
- Um novo cliente chegue aleatoriamente entre 4s e 6s.

Adicione um print de tela mostrando a execução do programa.

## Exercício 2

O seu desafio é implementar um `ArrayList` que seja thread safe.

Lembre-se que as operações de consulta não causam condição de corrida umas com as outras, entretanto, as inserções e remoções causam condição de corrida entre elas.

## Exercício 3

Utilizando como base sua implementação thread safe do `ArrayList`, compare o desempenho com a versão original que não é thread safe utilizando apenas 1 thread.

Faça a comparação para os métodos de inserção, busca e remoção, variando o tamanho da lista e mostrando o tempo necessário para realizar a operação com os tamanhos variados da lista.

Adicionalmente, informe quantas operações, inserção, busca e remoção separadamente, podem ser realizadas por segundo em ambas as listas.

Repita os testes, mas agora utilizando 16 threads para comparar sua implementação thread safe com a classe `Vector`.

Cada thread realiza uma quantidade predefinida de operações de inserção, busca e remoção com valores aleatórios.

Informe os valores obtidos nos testes realizados.

## Exercício 4

Na implementação de um Banco de Dados, há uma restrição para que no máximo 10 consultas sejam realizadas simultaneamente, ao passo que apenas 1 operação de escrita, `insert`, `update` ou `delete`, pode ocorrer simultaneamente.

Caso uma 11a consulta tente ser realizada, ela deve ser bloqueada até que alguma consulta finalize.

No momento da operação de escrita, não pode haver consultas no banco de dados.

Implemente uma classe que discipline o acesso ao Banco de dados.

Implemente as 4 operações CRUD:

- Create
- Read
- Update
- Delete

Crie um programa para testar e mostrar o funcionamento da(s) sua(s) classe(s).

Adicione um print de tela mostrando a execução do programa.
