import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) {

        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        //--------------------------- VARIÁVEIS DE CONTROLE
        int pontuacao = 0;
        int Cronometro = 0;
        int velocidadeInimigos = 0;


        // Variáveis de controle de geração
        int numPlayers = 20; // Número de PlayerIA
        int quantidadeVivos = numPlayers;
        int geracaoAtual = 0;
        int totalGeracao = 20;

        //Controle Inimigos
        int maxInimigos = 100;
        int inimigosCriados = 0;

        int limiteProximidade = 80; // Defina um limite adequado para a proximidade
        RedeNeuralTeste2 melhorRede = null;

        // Lista para armazenar os resultados de cada geração
        List<String> resultadosGeracoes = new ArrayList<>();

        //--------------------------- VARIÁVEIS DE CONTROLE FIM

//        Player player = new Player(30, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
//        janela.adicionarObjeto(player);
//        player.adicionarListener();

        // Criação de um vetor para armazenar múltiplos PlayerIA
        List<PlayerIA> player2List = new ArrayList<>();
        List<PlayerIA> coleta = new ArrayList<>(); //Coleta pontuações de PlayerIA
        List<RedeNeuralTeste2> redesNeurais = new ArrayList<>();
        Map<Integer, RedeNeuralTeste2> redesNeuraisArmazenadas = new HashMap<>();
        List<RedeNeuralTeste2> redesNeuraisArmazenadas2 = new ArrayList<>();


        List<Inimigo> inimigos = new ArrayList<>(); //Armazena inimigos

        // Inicializa os PlayerIA e redes neurais
        inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela);


        // Geração de múltiplos blocos de chão
        int larguraChao = 500; // Largura do chão
        int alturaChao = 200;   // Altura do chão
        int numeroDeChao = 3;  // Número de blocos de chão
        Chao[] chaoBlocos = new Chao[numeroDeChao];

        for (int i = 0; i < numeroDeChao; i++) {
            chaoBlocos[i] = new Chao(i * larguraChao, 400, larguraChao, alturaChao); // Posição inicial dos blocos
            janela.adicionarObjeto(chaoBlocos[i]);
        }

        JLabel geracaoLabel = new JLabel("Geração: 1");
        geracaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        geracaoLabel.setForeground(Color.BLACK);
        geracaoLabel.setBounds(30, 30, 200, 30);
        janela.addComponentToGamePanel(geracaoLabel);

        JLabel dinossaurosVivosLabel = new JLabel("Dinossauros Vivos: " + numPlayers);
        dinossaurosVivosLabel.setFont(new Font("Arial", Font.BOLD, 24));
        dinossaurosVivosLabel.setForeground(Color.GREEN);
        dinossaurosVivosLabel.setBounds(30, 60, 300, 30);
        janela.addComponentToGamePanel(dinossaurosVivosLabel);

        JLabel cronometoLabel = new JLabel("Cronometro: 0");
        cronometoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cronometoLabel.setForeground(Color.BLACK);
        cronometoLabel.setBounds(30, 90, 200, 30);
        janela.addComponentToGamePanel(cronometoLabel);


        while (geracaoAtual < totalGeracao) {
            for (int i = 0; i < maxInimigos; i++) {

                // Criar inimigos a cada 200 unidades do cronômetro, sem depender de 'i'
                if (Cronometro >= (inimigosCriados + 1) * 100) {

                    velocidadeInimigos = aumentaVelocidade(Cronometro);
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

                        // Interação com cada PlayerIA
                        for (int j = 0; j < player2List.size(); j++) {
                            PlayerIA playerIA = player2List.get(j);
                            playerIA.apertaF();
                            playerIA.levantar();


                            // Analisar proximidade e usar rede neural
                            if (sensores.analisarProximidade(playerIA, inimigo, limiteProximidade)) {
                                double[] entradas = {playerIA.getX(), playerIA.getY(), inimigo.getX(), inimigo.getY(), inimigo.getAltura(), inimigo.getLargura(), velocidadeInimigos};
                                RedeNeuralTeste2 redeNeural = redesNeurais.get(j);
                                redeNeural.recebeEntradas(entradas);

                                int tabelaVerdadeX, tabelaVerdadeY, tabelaVerdadeZ;

                                if (inimigo.getX() >= playerIA.getX()){
                                    tabelaVerdadeX = 0;
                                } else {
                                    tabelaVerdadeX = 1;
                                }

                                if (inimigo.getY() >= 350){
                                    tabelaVerdadeY = 0; //Pula
                                } else {
                                    tabelaVerdadeY = 1; //Abaixa
                                }

                                if (inimigo.getAltura() >= 70){
                                    tabelaVerdadeZ = 0; //Meteoro
                                } else {
                                    tabelaVerdadeZ = 1;
                                }

                                // Atualização do fatorCondicao com base nas possibilidades
                                double fatorCondicao = 0; // Valor padrão caso nenhuma condição seja atendida
                                int acao = 0;
                                int acaoRealizada, acaoEsperada;

                                if (tabelaVerdadeY == 0 && tabelaVerdadeZ == 1) {
                                    fatorCondicao = 0.25;  // Inimigo Terrestre // Pular
                                }else if (tabelaVerdadeX == 0 && tabelaVerdadeZ == 0) {
                                    fatorCondicao = 0.5;  // Esquerda
                                } else if (tabelaVerdadeX == 1 && tabelaVerdadeZ == 0) {
                                    fatorCondicao = 0.75;  // Meteoro  Direita
                                } else if (tabelaVerdadeY == 1 && tabelaVerdadeZ == 1) {
                                    fatorCondicao = 1;  // Voador
                                }

                                redeNeural.ajustarPesosPorCondicao2(entradas, fatorCondicao);

                                // Calcula as saídas da rede neural
                                double[] saidas = redeNeural.calcularSaida2(entradas);
                                double[] saidasOrdenada = redeNeural.calcularSaida2(entradas);


//                                for (int y = 0; i < saidas.length; i++) {
//                                    System.out.println("Saida[" + i + "] = " + saidas[i]);
//                                }
                                // Ordena o array 'saidas' em ordem decrescente
                                Arrays.sort(saidasOrdenada);

                                // Verifica o maior valor de 'saidas' após a ordenação
                                if (saidas[0] == saidasOrdenada[0]) {
                                    playerIA.apertarEspaco(); // Pular
                                    acao = 1;
                                } else if (saidas[1] == saidasOrdenada[1]) {
                                    playerIA.apertarS(); // Abaixar
                                    acao = 2;
                                } else if (saidas[2] == saidasOrdenada[2]) {
                                    playerIA.apertarEsquerda(); // Esquerda
                                    acao = 3;
                                }else if (saidas[3] == saidasOrdenada[3]) {
                                    playerIA.apertarDireita(); // Esquerda
                                    acao = 4;
                                }

                                // Incrementa a pontuação
                                playerIA.incrementarPontuacao(1);
                                redeNeural.incrementarPontuacao(1);
                                acaoRealizada = acao;

                                // Incrementa o tempo de sobrevivência
                                redeNeural.incrementarTempoSobrevivencia();

                                // Determina a ação esperada com base nas características do inimigo
                                int[] caracteristicasInimigo = new int[3];
                                caracteristicasInimigo[0] = (inimigo.getX() >= playerIA.getX()) ? 0 : 1;
                                caracteristicasInimigo[1] = (inimigo.getY() >= 350) ? 0 : 1;
                                caracteristicasInimigo[2] = (inimigo.getAltura() >= 70) ? 0 : 1;
                                acaoEsperada = redeNeural.determinarAcaoEsperada(caracteristicasInimigo);

                                // Registra a ação e verifica se foi um acerto
                                redeNeural.registrarAcao();
                                if (acao == acaoEsperada) {
                                    redeNeural.registrarAcerto();
                                }

                                // Verifica colisão com PlayerIA
                                if (sensores.verificarColisao(playerIA, inimigo)) {
                                    redeNeural.ErroAcao(acaoRealizada);
                                    coleta.add(playerIA);
                                    redesNeuraisArmazenadas.put(j, redesNeurais.get(j));
                                    //RedeNeuralTeste2.salvarDadosEmArquivo(redesNeurais);
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
            geracaoLabel.setText("Geração: " + (geracaoAtual + 1));
            dinossaurosVivosLabel.setText("Dinossauros Vivos: " + quantidadeVivos);
            janela.repaint();

            // Verifica se todos os players IA morreram
            if (quantidadeVivos <= 0) {
                geracaoAtual++;
                System.out.println("Geração " + geracaoAtual + " concluída.");

                // Seleciona os melhores players com base na pontuação
                //System.out.println("coleta tamanho: " + coleta.size());
                //System.out.println("redesNeurais tamanho: " + redesNeurais.size());

                coleta = selecao(coleta, player2List, redesNeuraisArmazenadas, numPlayers);
                redesNeuraisArmazenadas2 = selecao2(redesNeuraisArmazenadas, numPlayers);


                // Seleciona a melhor rede neural antes de limpar as listas
                if (!coleta.isEmpty() && !redesNeuraisArmazenadas.isEmpty()) {
                    //melhorRede = selecaoMelhorRede(coleta, redesNeuraisArmazenadas);
                    melhorRede = selecaoMelhorRede(redesNeuraisArmazenadas2);
                    System.out.println("Imprimindo melhor rede: " + melhorRede);

                    // Imprime informações sobre o melhor indivíduo da geração atual
                    System.out.println("\n===== MELHOR INDIVÍDUO DA GERAÇÃO " + geracaoAtual + " =====");
                    String resultadoGeracao = imprimirMelhorIndividuo(coleta, redesNeuraisArmazenadas2);
                    resultadosGeracoes.add("Geração " + geracaoAtual + ":\n" + resultadoGeracao);
                }

                if (geracaoAtual < totalGeracao) {
                    // Reinicializa a população
                    player2List.clear();
                    redesNeurais.clear();
                    redesNeuraisArmazenadas.clear();
                    coleta.clear();

                    Cronometro = 0;
                    inimigosCriados = 0;

                    // Imprime informações sobre o melhor indivíduo da geração atual
                    System.out.println("\nGeração " + geracaoAtual + " concluída.");
                    System.out.println("Iniciando próxima geração...\n");

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

        // Imprime informações sobre o melhor indivíduo de todas as gerações
        if (!redesNeuraisArmazenadas2.isEmpty()) {
            System.out.println("\n===== MELHOR INDIVÍDUO DE TODAS AS GERAÇÕES =====");
            imprimirMelhorIndividuo(coleta, redesNeuraisArmazenadas2);
        } else {
            System.out.println("\nNão foi possível determinar o melhor indivíduo de todas as gerações.");
        }

        // Imprime todos os resultados de todas as gerações
        System.out.println("\n\n===== RESULTADOS DE TODAS AS GERAÇÕES =====");
        for (String resultado : resultadosGeracoes) {
            System.out.println(resultado);
            System.out.println();
        }
        System.out.println("===========================================");
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20; // Posicione-os com um espaçamento entre si
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA); // Adiciona o PlayerIA à janela
            //playerIA.adicionarListener();
            RedeNeuralTeste2 redeNeural = new RedeNeuralTeste2(7, 14, 20, 4); // Configure a rede neural conforme necessário
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

            RedeNeuralTeste2 novaRede = new RedeNeuralTeste2(7, 14, 20, 4);

            // Se houver uma melhor rede neural, inicializamos a nova rede com os pesos dela
            if (melhorRede != null) {
                novaRede.copiarPesos2(melhorRede);
                melhorRede.aplicarMutacaoPopulacional(redesNeurais);

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

        // Adiciona o inimigo à lista.
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

    private static void criarInimigos4(List<Inimigo> inimigos2, Movimento movimento, Sensores sensores, GameWindow janela, int cronometro, int velocidadeInimigos) {
        Random random = new Random();
        Inimigo inimigo;

        if (cronometro < 2500) {
            // Antes de 1000, cria um InimigoTerrestre ou InimigoVoador
            int novoValorX = random.nextInt(601) + 50; // Isso vai gerar números entre 50 e 650
            velocidadeInimigos = (velocidadeInimigos/2)*-1;
            inimigo = new InimigoMeteoro(novoValorX, 0, 70, 70, "Meteoro.png", 0, velocidadeInimigos, movimento, sensores, janela);
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

        // Adiciona o inimigo à lista.
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

    public static List<PlayerIA> selecao(List<PlayerIA> populacao, List<PlayerIA> player2List, Map<Integer, RedeNeuralTeste2> redesNeuraisArmazenadas, int numSelecionados) {
        // Calcula o fitness para cada indivíduo
        Map<PlayerIA, Double> fitnessMap = new HashMap<>();
        int tempoMaximo = 0;

        // Encontra o tempo máximo de sobrevivência
        for (PlayerIA player : populacao) {
            int index = player2List.indexOf(player);
            RedeNeuralTeste2 rede = redesNeuraisArmazenadas.get(index);
            if (rede != null) {
                tempoMaximo = Math.max(tempoMaximo, rede.getTempoSobrevivencia());
            }
        }

        // Se não houver tempo de sobrevivência, usa um valor padrão
        if (tempoMaximo == 0) tempoMaximo = 1000;

        // Calcula o fitness para cada indivíduo
        for (PlayerIA player : populacao) {
            int index = player2List.indexOf(player);
            RedeNeuralTeste2 rede = redesNeuraisArmazenadas.get(index);
            if (rede != null) {
                double fitness = rede.calcularFitness(tempoMaximo);
                fitnessMap.put(player, fitness);
            }
        }

        // Ordena a população pelo fitness (decrescente)
        List<PlayerIA> populacaoOrdenada = new ArrayList<>(populacao);
        populacaoOrdenada.sort((p1, p2) -> Double.compare(fitnessMap.getOrDefault(p2, 0.0), fitnessMap.getOrDefault(p1, 0.0)));

        // Retorna os melhores indivíduos
        return populacaoOrdenada.subList(0, Math.min(numSelecionados, populacaoOrdenada.size()));
    }

    public static List<RedeNeuralTeste2> selecao2(Map<Integer, RedeNeuralTeste2> redesNeurais, int numSelecionados) {
        // Calcula o fitness para cada rede
        Map<RedeNeuralTeste2, Double> fitnessMap = new HashMap<>();
        int tempoMaximo = 0;

        // Encontra o tempo máximo de sobrevivência
        for (RedeNeuralTeste2 rede : redesNeurais.values()) {
            tempoMaximo = Math.max(tempoMaximo, rede.getTempoSobrevivencia());
        }

        // Se não houver tempo de sobrevivência, usa um valor padrão
        if (tempoMaximo == 0) tempoMaximo = 1000;

        // Calcula o fitness para cada rede
        for (RedeNeuralTeste2 rede : redesNeurais.values()) {
            double fitness = rede.calcularFitness(tempoMaximo);
            fitnessMap.put(rede, fitness);
        }

        // Ordena a população pelo fitness (decrescente)
        List<RedeNeuralTeste2> populacaoOrdenada = new ArrayList<>(redesNeurais.values());
        populacaoOrdenada.sort((r1, r2) -> Double.compare(fitnessMap.get(r2), fitnessMap.get(r1)));

        // Retorna as melhores redes
        return populacaoOrdenada.subList(0, Math.min(numSelecionados, populacaoOrdenada.size()));
    }


    public static RedeNeuralTeste2 selecaoMelhorRede(List<RedeNeuralTeste2> redesNeurais) {
        if (redesNeurais.isEmpty()) {
            return null;
        }

        return redesNeurais.get(0);
    }

    public static String imprimirMelhorIndividuo(List<PlayerIA> populacao, List<RedeNeuralTeste2> redesNeurais) {
        if (redesNeurais == null || redesNeurais.isEmpty()) {
            return "Não há redes neurais para analisar.";
        }

        // Encontra o tempo máximo de sobrevivência
        int tempoMaximo = 0;
        for (RedeNeuralTeste2 rede : redesNeurais) {
            tempoMaximo = Math.max(tempoMaximo, rede.getTempoSobrevivencia());
        }

        // Encontra a melhor rede neural com base no fitness
        RedeNeuralTeste2 melhorRede = null;
        double melhorFitness = Double.NEGATIVE_INFINITY;

        for (RedeNeuralTeste2 rede : redesNeurais) {
            double fitness = rede.calcularFitness(tempoMaximo);
            if (fitness > melhorFitness) {
                melhorFitness = fitness;
                melhorRede = rede;
            }
        }

        if (melhorRede != null) {
            StringBuilder resultado = new StringBuilder();
            resultado.append("===== MELHOR INDIVÍDUO =====\n");
            resultado.append("Tempo de Sobrevivência: ").append(melhorRede.getTempoSobrevivencia()).append("\n");
            resultado.append("Pontuação: ").append(melhorRede.getPontuacao()).append("\n");
            resultado.append("Taxa de Acerto: ").append(String.format("%.2f", melhorRede.getTaxaAcerto() * 100)).append("%\n");
            resultado.append("Fitness: ").append(String.format("%.2f", melhorFitness)).append("\n");

            // Adiciona informações sobre o preFitness
            double preFitness = melhorRede.calcularPreFitness();
            resultado.append("PreFitness: ").append(String.format("%.2f", preFitness)).append("\n");

            // Adiciona informações sobre erros
            resultado.append("Erros de Ação: ").append(melhorRede.getErroAcao()).append("\n");

            resultado.append("===========================");

            // Imprime o resultado no console
            System.out.println(resultado.toString());

            return resultado.toString();
        } else {
            String mensagem = "Não foi possível encontrar o melhor indivíduo.";
            System.out.println(mensagem);
            return mensagem;
        }
    }
}