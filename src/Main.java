import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        //--------------------------- VARIÁVEIS DE CONTROLE
        int pontuacaoAlvo = 100;
        int pontuacao = 0;
        int Cronometro = 0;
        //int velocidadeAmbiente = 20;
        int velocidadeInimigos = 0;
        //int controleVelocidade = -5;
        //int contadorVelociade = 0;


        // Variáveis de controle de geração
        int numPlayers = 20; // Número de PlayerIA
        int quantidadeVivos = numPlayers;
        int geracaoAtual = 0;
        int totalGeracao = 30;

        //Controle Inimigos
        int maxInimigos = 100;
        int inimigosCriados = 0;

        int limiteProximidade = 80; // Defina um limite adequado para a proximidade
        RedeNeuralTeste2 melhorRede = null;
        //--------------------------- VARIÁVEIS DE CONTROLE FIM

//        Player player = new Player(30, 30, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
//        janela.adicionarObjeto(player);
//        player.adicionarListener();

        // Criação de um vetor para armazenar múltiplos PlayerIA
        List<PlayerIA> player2List = new ArrayList<>();
        List<PlayerIA> coleta = new ArrayList<>(); //Coleta pontuações de PlayerIA
        List<RedeNeuralTeste2> redesNeurais = new ArrayList<>();
        List<RedeNeuralTeste2> redesNeuraisArmazenadas = new ArrayList<>();
        List<RedeNeuralTeste2> redesNeuraisArmazenadas2 = new ArrayList<>();


        List<Inimigo> inimigos = new ArrayList<>(); //Armazena inimigos

        // Inicializa os PlayerIA e redes neurais
        inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela);

        // Criar um pool de threads para os jogadores IA
        ExecutorService executor = Executors.newFixedThreadPool(4); // Ajuste conforme necessário

        // Geração de múltiplos blocos de chão
        int larguraChao = 500; // Largura do chão
        int alturaChao = 200;   // Altura do chão
        int numeroDeChao = 3;  // Número de blocos de chão
        Chao[] chaoBlocos = new Chao[numeroDeChao];

        for (int i = 0; i < numeroDeChao; i++) {
            chaoBlocos[i] = new Chao(i * larguraChao, 400, larguraChao, alturaChao); // Posição inicial dos blocos
            janela.adicionarObjeto(chaoBlocos[i]);
        }

        JLabel pontuacaoLabel = new JLabel("Pontuacao: 0");
        pontuacaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pontuacaoLabel.setForeground(Color.BLACK);
        pontuacaoLabel.setBounds(30, 30, 200, 30);
        janela.addComponentToGamePanel(pontuacaoLabel);

        JLabel cronometoLabel = new JLabel("Cronometro: 0");
        cronometoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cronometoLabel.setForeground(Color.BLACK);
        cronometoLabel.setBounds(50, 50, 200, 30);
        janela.addComponentToGamePanel(cronometoLabel);


        while (geracaoAtual < totalGeracao) {
            for (int i = 0; i < maxInimigos; i++) {

                // Criar inimigos a cada 200 unidades do cronômetro, sem depender de 'i'
                if (Cronometro >= (inimigosCriados + 1) * 100) {

                    velocidadeInimigos = aumentaVelocidade(Cronometro);
                    //criarInimigos2(inimigos, movimento, sensores, janela); // Cria inimigos
                    //criarInimigos2(inimigos, movimento, sensores, janela, Cronometro);
                    criarInimigos3(inimigos, movimento, sensores, janela, Cronometro, velocidadeInimigos);

                    inimigosCriados++; // Incrementa o contador de inimigos criados
                }

                // Acesso aos inimigos da lista inimigos
                if (i < inimigos.size()) {
                    Inimigo inimigo = inimigos.get(i); // Agora acessamos o inimigo pela lista

                    if (inimigo != null) {

                        if (Cronometro >= i * 50) {
                            inimigo.atualizar();
                        }

//                        if (sensores.verificarColisao(player, inimigo)) {
//                            janela.removerObjeto(inimigo);
//                            inimigos.remove(i); // Remove o inimigo da lista
//                            System.gc();
//
//                            System.out.println("Colisão detectada! Inimigo removido.");
//                        } else if (inimigo.getRect().x < player.getRect().x) {
//                            pontuacao++;
//                            System.out.println("Pontuação: " + pontuacao);
//                            pontuacaoLabel.setText("Pontuacao: " + pontuacao);
//
//                            if (inimigo.getRect().x < -inimigo.getRect().width) {
//                                janela.removerObjeto(inimigo);
//                                inimigos.remove(i); // Remove o inimigo da lista
//                                System.gc();
//                            }
//                        }

                        // Interação com cada PlayerIA
                        for (int j = 0; j < player2List.size(); j++) {
                            PlayerIA playerIA = player2List.get(j);
                            playerIA.apertaF();
                            playerIA.levantar();


                            // Analisar proximidade e usar rede neural
                            if (sensores.analisarProximidade(playerIA, inimigo, limiteProximidade)) {
                                double[] entradas = {playerIA.getX(), playerIA.getY(), inimigo.getX(), inimigo.getY(), inimigo.getAltura(), inimigo.getLargura(), velocidadeInimigos};
                                RedeNeuralTeste2 redeNeural = redesNeurais.get(j);

                                // Ajusta os pesos da rede neural dependendo das coordenadas e dimensões do inimigo
                                int fatorCondicao = redeNeural.identificarInimigo(entradas);
                                redeNeural.ajustarPesosPorCondicao(entradas, fatorCondicao);

                                // Calcula as saídas da rede neural
//                                double[] saidas = redeNeural.calcularSaida(entradas);
//                                int acao = 0;
//                                if (saidas[0] > saidas[1]) {
//                                    acao = 0;
//                                }else {acao = 1;}
//
//                                // Executa a ação correspondente
//                                switch (acao) {
//                                    case 0:
//                                        playerIA.apertarEspaco(); // Pular
//                                        break;
//                                    case 1:
//                                        playerIA.apertarS(); // Abaixar
//                                        break;
//                                    case 2:
//                                        playerIA.apertarDireita(); // Mover para a direita
//                                        break;
//                                    case 3:
//                                        playerIA.apertarEsquerda(); // Mover para a esquerda
//                                        break;
//                                }

                                //int tipoInimigo = redeNeural.identificarInimigo(entradas);
                                switch (fatorCondicao) {
                                    case 1:
                                        playerIA.apertarEspaco(); // Pular
                                        break;
                                    case 2:
                                        playerIA.apertarS(); // Abaixar
                                        break;
                                    case 3:
                                        playerIA.apertarDireita();
                                        break;
                                    case 4:
                                        playerIA.apertarEsquerda();
                                        break;
                                }

                                // Incrementa a pontuação
                                playerIA.incrementarPontuacao(1);
                                redeNeural.incrementarPontuacao(1);

                                // Verifica colisão com PlayerIA
                                if (sensores.verificarColisao(playerIA, inimigo)) {
                                    coleta.add(playerIA);
                                    redesNeuraisArmazenadas.add(redesNeurais.get(j));
                                    RedeNeuralTeste2.salvarDadosEmArquivo(redesNeurais);
                                    janela.removerObjeto(playerIA);
                                    player2List.remove(j);
                                    redesNeurais.remove(j);
                                    quantidadeVivos--;
                                    j--; // Ajusta o índice após remoção
                                    System.gc();
                                }
                            }
                        }
                    }
                }
            }

            atualizarChao(chaoBlocos, larguraChao, numeroDeChao);

            // Atualiza a posição do player e aplica gravidade
//            movimento.aplicarGravidade(player, chaoBlocos[0]); // Use o primeiro bloco como referência para gravidade
//            movimento.controlarSalto(player);

            // Atualiza a posição dos PlayerIA
            for (PlayerIA playerIA : player2List) {

                movimento.atualizarFisica(playerIA, chaoBlocos[0]);
                movimento.controlarSalto(playerIA);
            }

            pontuacaoLabel.setText("Pontuacao: " + pontuacao);
            janela.repaint();

            // Verifica se todos os players IA morreram
            if (quantidadeVivos <= 0) {
                geracaoAtual++;
                System.out.println("Geração " + geracaoAtual + " concluída.");

                // Seleciona os melhores players com base na pontuação
                //System.out.println("coleta tamanho: " + coleta.size());
                //System.out.println("redesNeurais tamanho: " + redesNeurais.size());

                coleta = selecao(coleta, numPlayers);
                redesNeuraisArmazenadas2 = selecao2(redesNeuraisArmazenadas, numPlayers);


                // Seleciona a melhor rede neural antes de limpar as listas
                if (!coleta.isEmpty() && !redesNeuraisArmazenadas.isEmpty()) {
                    //melhorRede = selecaoMelhorRede(coleta, redesNeuraisArmazenadas);
                    melhorRede = selecaoMelhorRede(redesNeuraisArmazenadas2);
                    System.out.println("Imprimindo melhor rede: " + melhorRede);
                }
                //SalvaInformações(coleta, redesNeuraisArmazenadas2, numPlayers);

                if (geracaoAtual < totalGeracao) {
                    // Reinicializa a população
                    player2List.clear();
                    redesNeurais.clear();
                    redesNeuraisArmazenadas.clear();
                    redesNeuraisArmazenadas2.clear();
                    coleta.clear();
                    System.gc();

                    Cronometro = 0;
                    inimigosCriados = 0;

                    inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, melhorRede);

                    quantidadeVivos = numPlayers;
                    // Limpeza dos inimigos da lista inimigos
                    for (Inimigo inimigo : inimigos) {
                        janela.removerObjeto(inimigo);
                    }
                    inimigos.clear();  // Limpa a lista de inimigos
                }
            }
            try {
                Thread.sleep(16); // Aproximadamente 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Cronometro++;
            cronometoLabel.setText("Cronometro: " + Cronometro);
        }
        System.out.println("Simulação concluída após " + totalGeracao + " gerações.");
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20; // Posicione-os com um espaçamento entre si
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA); // Adiciona o PlayerIA à janela
            //playerIA.adicionarListener();
            RedeNeuralTeste2 redeNeural = new RedeNeuralTeste2(7, 14, 4); // Configure a rede neural conforme necessário
            redesNeurais.add(redeNeural);
        }
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela,
                                             RedeNeuralTeste2 melhorRede) {
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20;
            PlayerIA playerIA = new PlayerIA(posX, 300, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA);

            RedeNeuralTeste2 novaRede = new RedeNeuralTeste2(7, 14, 4);

            // Se houver uma melhor rede neural, inicializamos a nova rede com os pesos dela
            if (melhorRede != null) {
                novaRede.copiarPesos(melhorRede);
            }

            redesNeurais.add(novaRede);
        }
    }


    private static int aumentaVelocidade(int Cronometro){
        int velocidadeInimigos = 0;
        int controleVelocidade = -5;

        // Calcular a fórmula com arredondamento para cima
        double resultado = (((Cronometro / 500.0) * 3) + 5) * -1;

        // Arredondar para cima e converter para inteiro
        velocidadeInimigos = (int) Math.ceil(resultado);

        return velocidadeInimigos;
    }

    private static void criarInimigos2(List<Inimigo> inimigos2, Movimento movimento, Sensores sensores, GameWindow janela, int cronometro, int velocidadeInimigos) {
        Random random = new Random();
        Inimigo inimigo;

        if (cronometro < 500) {
            // Antes de 1000, cria um InimigoTerrestre ou InimigoVoador
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        } else if (cronometro > 500 && cronometro < 1000){
            // Depois de 1000, cria um InimigoTerrestre ou InimigoEspinho
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoEspinho(600, 355, 70, 50, "estegossauro.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        } else if (cronometro > 1000 && cronometro < 1500) {
            // Entre 1500 e 2000, cria um InimigoVoador ou InimigoEspinho
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoEspinho(600, 355, 70, 50, "estegossauro.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        } else {
            // Depois de 3000, cria qualquer um dos três tipos de inimigo
            int escolha = random.nextInt(3);
            if (escolha == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else if (escolha == 1) {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoEspinho(600, 355, 70, 50, "estegossauro.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        }

        // Adiciona o inimigo à lista
        inimigos2.add(inimigo);

        // Adiciona o inimigo à janela (para exibição)
        janela.adicionarObjeto(inimigo);
    }

    private static void criarInimigos3(List<Inimigo> inimigos2, Movimento movimento, Sensores sensores, GameWindow janela, int cronometro, int velocidadeInimigos) {
        Random random = new Random();
        Inimigo inimigo;

        if (cronometro < 500) {
            // Antes de 1000, cria um InimigoTerrestre ou InimigoVoador
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        } else if (cronometro > 500 && cronometro < 1000){
            // Depois de 1000, cria um InimigoTerrestre ou InimigoEspinho
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                int novoValorX = random.nextInt(601) + 50; // Isso vai gerar números entre 50 e 650
                velocidadeInimigos = (velocidadeInimigos/2)*-1;
                inimigo = new InimigoMeteoro(novoValorX, 0, 70, 70, "Meteoro.png", 0, velocidadeInimigos, movimento, sensores, janela);
            }

        } else {
            // Depois de 3000, cria qualquer um dos três tipos de inimigo
            int escolha = random.nextInt(3);
            if (escolha == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else if (escolha == 1) {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                int novoValorX = random.nextInt(601) + 50; // Isso vai gerar números entre 50 e 650
                velocidadeInimigos = (velocidadeInimigos/2)*-1;
                inimigo = new InimigoMeteoro(novoValorX, 0, 70, 70, "Meteoro.png", 0, velocidadeInimigos, movimento, sensores, janela);
            }
        }

        // Adiciona o inimigo à lista
        inimigos2.add(inimigo);

        // Adiciona o inimigo à janela (para exibição)
        janela.adicionarObjeto(inimigo);
    }


    private static void atualizarChao(Chao[] chaoBlocos, int larguraChao, int numeroDeChao) {
        for (Chao chao : chaoBlocos) {
            chao.setX(chao.getX() - 5);
            if (chao.getX() < -larguraChao) {
                chao.setX(larguraChao * (numeroDeChao - 1));
            }
        }
    }

    public static List<PlayerIA> selecao(List<PlayerIA> populacao, int numSelecionados) {
        // Verifica se a população está vazia
        if (populacao == null || populacao.isEmpty()) {
            System.out.println("A população está vazia.");
            return new ArrayList<>();
        }

        // Copia a população para evitar modificar a lista original
        List<PlayerIA> copiaPopulacao = new ArrayList<>(populacao);

        // Ordena a cópia com base na pontuação (do maior para o menor)
        copiaPopulacao.sort((p1, p2) -> Double.compare(p2.getPontuacao(), p1.getPontuacao()));

        // Garante que numSelecionados não ultrapasse o tamanho da lista
        numSelecionados = Math.min(numSelecionados, copiaPopulacao.size());

        // Exibe o ranqueamento no console
        System.out.println("Ranking da População:");
        for (int i = 0; i < copiaPopulacao.size(); i++) {
            System.out.println((i + 1) + "º - " + copiaPopulacao.get(i) + " | Pontuação: " + copiaPopulacao.get(i).getPontuacao());
        }
        System.out.println("Fim Ranking:");

        // Retorna os melhores indivíduos
        return new ArrayList<>(copiaPopulacao.subList(0, numSelecionados));
    }

    public static ArrayList<RedeNeuralTeste2> selecao2(List<RedeNeuralTeste2> redesNeurais, int numSelecionados) {
        // Verifica se a população está vazia
        if (redesNeurais == null || redesNeurais.isEmpty()) {
            System.out.println("A população está vazia.");
            return new ArrayList<>();
        }

        // Copia a população para evitar modificar a lista original
        List<RedeNeuralTeste2> copiaPopulacaoRede = new ArrayList<>(redesNeurais);

        // Ordena a cópia com base na pontuação (do maior para o menor)
        copiaPopulacaoRede.sort((p1, p2) -> Double.compare(p2.getPontuacao(), p1.getPontuacao()));

        // Garante que numSelecionados não ultrapasse o tamanho da lista
        numSelecionados = Math.min(numSelecionados, copiaPopulacaoRede.size());

        // Exibe o ranqueamento no console
        System.out.println("Ranking da População:");
        for (int i = 0; i < copiaPopulacaoRede.size(); i++) {
            System.out.println((i + 1) + "º - " + copiaPopulacaoRede.get(i) + " | Pontuação: " + copiaPopulacaoRede.get(i).getPontuacao());
        }
        System.out.println("Fim Ranking:");

        // Retorna os melhores indivíduos
        return new ArrayList<>(copiaPopulacaoRede.subList(0, numSelecionados));
    }

    public static RedeNeuralTeste2 selecaoMelhorRede(List<RedeNeuralTeste2> redesNeurais) {
        if (redesNeurais.isEmpty()) {
            return null;
        }

        return redesNeurais.get(0);
    }

}