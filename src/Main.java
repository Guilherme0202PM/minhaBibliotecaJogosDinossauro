import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        int pontuacaoAlvo = 100;
        int pontuacao = 0;

        Player player = new Player(30, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
        janela.adicionarObjeto(player);
        player.adicionarListener();

        // Criação de um vetor para armazenar múltiplos PlayerIA
        int numPlayers = 20; // Número de PlayerIA
        List<PlayerIA> player2List = new ArrayList<>();
        List<PlayerIA> coleta = new ArrayList<>();
        List<RedeNeuralTeste2> redesNeurais = new ArrayList<>();

        // Inicializa os PlayerIA e redes neurais
        inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela);

        // Variáveis de controle de geração
        int quantidadeVivos = numPlayers;
        int geracaoAtual = 0;
        int totalGeracao = 10;

        int maxInimigos = pontuacaoAlvo;
        Inimigo[] inimigos = criarInimigos(maxInimigos, movimento, sensores, janela);

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

        long startTime = System.currentTimeMillis();

        int limiteProximidade = 80; // Defina um limite adequado para a proximidade



        while (geracaoAtual < totalGeracao) {
            for (int i = 0; i < maxInimigos; i++) {
                Inimigo inimigo = inimigos[i];
                if (inimigo != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= i * 2000) {
                        inimigo.atualizar();
                    }

                    if (sensores.verificarColisao(player, inimigo)) {
                        janela.removerObjeto(inimigo);
                        inimigos[i] = null;
                        System.gc();

                        System.out.println("Colisão detectada! Inimigo removido.");
                    } else if (inimigo.getRect().x < player.getRect().x) {
                        pontuacao++;
                        System.out.println("Pontuação: " + pontuacao);
                        pontuacaoLabel.setText("Pontuacao: " + pontuacao);

                        if (inimigo.getRect().x < -inimigo.getRect().width) {
                            janela.removerObjeto(inimigo);
                            inimigos[i] = null;
                            System.gc();

                        }
                    }


                    // Interação com cada PlayerIA
                    for (int j = 0; j < player2List.size(); j++) {
                        PlayerIA playerIA = player2List.get(j);
                        playerIA.apertaF();


                        // Analisar proximidade e usar rede neural
                        if (sensores.analisarProximidade(playerIA, inimigo, limiteProximidade)) {
                            double[] entradas = {playerIA.getX(), playerIA.getY(), inimigo.getX(), inimigo.getY()};
                            RedeNeuralTeste2 redeNeural = redesNeurais.get(j);

                            // Ajusta os pesos da rede neural dependendo do inimigo
                            int fatorCondicao = (inimigo.getY() == 350) ? 1 : -1;
                            redeNeural.ajustarPesosPorCondicao(entradas, fatorCondicao);

                            // Calcula as saídas da rede neural
                            double[] saidas = redeNeural.calcularSaida(entradas);
                            if (saidas[0] > 0) {
                                playerIA.apertarEspaco(); // Pular
                                playerIA.incrementarPontuacao(1);
                            } else {
                                playerIA.apertarS(); // Abaixar
                                playerIA.incrementarPontuacao(1);
                            }
                            // Verifica colisão com PlayerIA
                            if (sensores.verificarColisao(playerIA, inimigo)) {
                                coleta.add(playerIA);
                                RedeNeuralTeste2.salvarDadosEmArquivo(redesNeurais);
                                janela.removerObjeto(playerIA);
                                player2List.remove(j);
                                redesNeurais.remove(j);
                                quantidadeVivos--;
                                //System.out.println("Quantidade de vivos"+ quantidadeVivos);
                                j--; // Ajusta o índice após remoção
                                System.gc();

                            }
                        }
                    }
                }
            }

            atualizarChao(chaoBlocos, larguraChao, numeroDeChao);

            // Atualiza a posição do player e aplica gravidade
            movimento.aplicarGravidade(player, chaoBlocos[0]); // Use o primeiro bloco como referência para gravidade
            movimento.controlarSalto(player);

            // Atualiza a posição dos PlayerIA
            for (PlayerIA playerIA : player2List) {
                movimento.aplicarGravidade(playerIA, chaoBlocos[0]);
                movimento.controlarSalto(playerIA);
            }

            pontuacaoLabel.setText("Pontuacao: " + pontuacao);
            janela.repaint();

            // Verifica se todos os players IA morreram
            if (quantidadeVivos <= 0) {
                geracaoAtual++;
                System.out.println("Geração " + geracaoAtual + " concluída.");

                coleta = selecao(coleta, numPlayers);

                if (geracaoAtual < totalGeracao) {
                    // Reinicializa a população
                    player2List.clear();
                    redesNeurais.clear();
                    RedeNeuralTeste2 melhorRede = null;

                    inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, melhorRede);
                    //inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela);
                    quantidadeVivos = numPlayers;
                    inimigos = limpezaTotal(inimigos, janela);
                    player.teleporte(500,350);

                }
                inimigos = criarInimigos(maxInimigos,movimento,sensores,janela);
            }
            try {
                Thread.sleep(16); // Aproximadamente 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Simulação concluída após " + totalGeracao + " gerações.");
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20; // Posicione-os com um espaçamento entre si
            PlayerIA playerIA = new PlayerIA(posX, 50, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA); // Adiciona o PlayerIA à janela
            //playerIA.adicionarListener();
            RedeNeuralTeste2 redeNeural = new RedeNeuralTeste2(4, 6, 2); // Configure a rede neural conforme necessário
            redesNeurais.add(redeNeural);
        }
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela,
                                             RedeNeuralTeste2 melhorRede) {
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20;
            PlayerIA playerIA = new PlayerIA(posX, 50, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA);

            RedeNeuralTeste2 novaRede = new RedeNeuralTeste2(4, 6, 2);

            // Se houver uma melhor rede neural, inicializamos a nova rede com os pesos dela
            if (melhorRede != null) {
                novaRede.copiarPesos(melhorRede);
            }

            redesNeurais.add(novaRede);
        }
    }

    private static Inimigo[] criarInimigos(int maxInimigos, Movimento movimento, Sensores sensores, GameWindow janela) {
        Random random = new Random();
        Inimigo[] inimigos = new Inimigo[maxInimigos];

        for (int i = 0; i < maxInimigos; i++) {

            if (random.nextInt(2) == 0) {
                inimigos[i] = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", -5, 0, movimento, sensores, janela);
            } else {
                inimigos[i] = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", -5, 0, movimento, sensores, janela);
            }
            janela.adicionarObjeto(inimigos[i]);
        }
        return inimigos;
    }

    private static Inimigo[] limpezaTotal(Inimigo[] inimigos, GameWindow janela) {
        if (inimigos != null) {
            for (int i = 0; i < inimigos.length; i++) {
                if (inimigos[i] != null) {
                    janela.removerObjeto(inimigos[i]); // Remove da janela
                    inimigos[i] = null; // Define como nulo
                }
            }
        }
        return inimigos; // Retorna o vetor atualizado
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

    public static RedeNeuralTeste2 selecaoMelhorRede(List<PlayerIA> populacao, List<RedeNeuralTeste2> redesNeurais) {
        if (populacao.isEmpty()) {
            return null;
        }

        // Encontrar o PlayerIA com a maior pontuação
        PlayerIA melhorPlayer = populacao.get(0);
        for (PlayerIA player : populacao) {
            if (player.getPontuacao() > melhorPlayer.getPontuacao()) {
                melhorPlayer = player;
            }
        }

        // Encontrar a rede neural correspondente ao melhor PlayerIA
        int indiceMelhor = populacao.indexOf(melhorPlayer);
        return redesNeurais.get(indiceMelhor);
    }

}