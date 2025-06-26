import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {

        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        //--------------------------- VARIÁVEIS DE CONTROLE
        int Cronometro = 0;
        int velocidadeInimigos = 0;

        // Variaveis de controle de geracao
        int numPlayers = 20; // Número de PlayerIA
        int quantidadeVivos = numPlayers;
        int geracaoAtual = 0;
        int totalGeracao = 5;

        //Controle Inimigos
        int maxInimigos = 100;
        int inimigosCriados = 0;

        // Defina um limite adequado para a proximidade, essa eh a area do "radar"
        int limiteProximidade = 80;
        RedeNeuralTeste3 melhorRede = null;

        // Geração de múltiplos blocos de chão
        int larguraChao = 500; // Largura do chão
        int alturaChao = 200;   // Altura do chão
        int numeroDeChao = 3;  // Número de blocos de chão
        Chao[] chaoBlocos = new Chao[numeroDeChao];

        for (int i = 0; i < numeroDeChao; i++) {
            chaoBlocos[i] = new Chao(i * larguraChao, 400, larguraChao, alturaChao); // Posição inicial dos blocos
            janela.adicionarObjeto(chaoBlocos[i]);
        }


        //Taxas de controle e Acertos
        double taxaDeAcerto = 0;
        double taxaDeErro = 0;
        double fitness = 0;

        double taxaInimigoTerrestre = 0;
        double taxaInimigoVoador = 0;
        double taxaInimigoMeteoro = 0;

        //Vou usar para identificar se o dinossauro executou a ação correta, com base nos 3 inimigos/desafio
        boolean desafioTerrestre = false;
        boolean desafioVoador = false;
        boolean desafioMeteoro = false;
        boolean acertou = false;

        int indentificadorInimigo = 0;


        //--------------------------- VARIÁVEIS DE CONTROLE FIM

        //------------------------------------------------------

        //--------------------------- LISTAS DE CONTROLE

        // Criacao de um vetor para armazenar multiplos PlayerIA ou agentes
        List<PlayerIA> player2List = new ArrayList<>();
        // Lista que coleta os PlayerIA apos uma geracao, geralmente para ordena-los por pontuacao ou comparar desempenho
        List<PlayerIA> coleta = new ArrayList<>(); //Coleta pontuacoes de PlayerIA
        // Lista com todas as redes neurais ativas da geracao atual (cada PlayerIA tem uma rede neural)
        List<RedeNeuralTeste3> redesNeurais = new ArrayList<>();
        // Lista para armazenar redes neurais de geracoes anteriores ou as melhores da geracao anterior
        List<RedeNeuralTeste3> redesNeuraisArmazenadas = new ArrayList<>();
        // Outra lista de backup das redes anteriores (usada para comparacoes ou fallback)
        List<RedeNeuralTeste3> redesNeuraisSelecionadaRoleta = new ArrayList<>();
        // Lista com as redes neurais de melhor desempenho ao longo das geracoes (especie de hall da fama)
        List<RedeNeuralDesempenho> redesNeuraisMelhorDesempenho = new ArrayList<>();
        // Log das melhores redes ja encontradas, para fins de visualizacao ou reexecucao
        List<RedeNeuralTeste3> LogMelhoresRedes = new ArrayList<>();
        // Lista para armazenar os fitness de todos os dinossauros que morreram
        List<Double> fitnessHistorico = new ArrayList<>();



        //Armazena inimigos
        List<Inimigo> inimigos = new ArrayList<>();

        // Inicializa os PlayerIA e redes neurais
        inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, melhorRede);


        //--------------------------- FIM LISTAS DE CONTROLE

        //------------------------------------------------------

        //--------------------------- TEXTO GRAFICO

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

        //        Player player = new Player(30, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
        //        janela.adicionarObjeto(player);
        //        player.adicionarListener();


        //Loop que controla todo o jogo
        while (geracaoAtual < totalGeracao) {
            for (int i = 0; i < maxInimigos; i++) {

                // Criar inimigos a cada 200 unidades do cronômetro, sem depender de 'i'
                if (Cronometro >= (inimigosCriados + 1) * 100) {

                    velocidadeInimigos = aumentaVelocidade(Cronometro);
                    criarInimigos(inimigos, movimento, sensores, janela, Cronometro, velocidadeInimigos);

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
                            playerIA.aplicarFiltro();
                            playerIA.levantar();


                            // Analisar proximidade e usar rede neural
                            if (sensores.analisarProximidade(playerIA, inimigo, limiteProximidade)) {
                                double[] entradas = {playerIA.getX(), playerIA.getY(), inimigo.getX(), inimigo.getY(), inimigo.getAltura(), inimigo.getLargura(), velocidadeInimigos};
                                RedeNeuralTeste3 redeNeural = redesNeurais.get(j);

                                // Ajusta os pesos da rede neural dependendo do inimigo
                                //Se a posição Y do inimigo for igual a 350, então fatorCondicao será -1; caso contrário, será 1
                                //Era entre -1 e 1 mas mudei para 0 e 1
                                double fatorCondicaoY = (inimigo.getY() >= 350) ? 0 : 1;
                                double fatorCondicaoX = (inimigo.getAltura() >= 70) ? 0 : 1;
                                indentificadorInimigo = 0;
                                desafioMeteoro = false;
                                desafioVoador = false;
                                desafioTerrestre = false;

//                                0 0 Voa
//                                0 1 Tere
//                                1 0 Mete
//                                1 1 Voa
//
//                                Terra 350 70 50
//                                Voa 320 70 50
//                                Mete 0 70 70

                                if (fatorCondicaoY >= 1 && fatorCondicaoY == fatorCondicaoX){
                                    indentificadorInimigo = 2; //InimigoVoador
                                } else if(fatorCondicaoY == 0 && fatorCondicaoX == 1){
                                    indentificadorInimigo = 1; //InimigoTerrestre
                                } else{
                                    indentificadorInimigo = 3; //InimigoMeteoro
                                }

                                double[] saidas = redeNeural.calcularSaida(entradas);

//                                for (int p = 0; p < saidas.length; p++) {
//                                    System.out.println("Saídaaaaaaaaaaaaaaaaaaaa " + p + ": " + saidas[p]);
//                                }

                                // Saída 0: Se > 0, pula; senão, não faz nada
                                if (saidas[0] > 0) {
                                    playerIA.apertarSaltar(); // Pular
                                    playerIA.incrementarPontuacao(2); // 2 pontos para pular
                                    redeNeural.incrementarPontuacao(2);
                                    desafioTerrestre = true;
                                }

                                // Saída 1: Se > 0, abaixa; senão, não faz nada
                                if (saidas[1] > 0) {
                                    playerIA.apertarAbaixar(); // Abaixar
                                    playerIA.incrementarPontuacao(2); // 2 pontos para abaixar
                                    redeNeural.incrementarPontuacao(2);
                                    desafioVoador = true;
                                }

                                // Saída 2: Se > 0, vai para direita; senão, não faz nada
                                if (saidas[2] > 0) {
                                    playerIA.apertarDireita();
                                    playerIA.incrementarPontuacao(1); // 1 ponto para direita
                                    redeNeural.incrementarPontuacao(1);
                                    desafioMeteoro = true;
                                }

                                // Saída 3: Se > 0, vai para esquerda; senão, não faz nada
                                if (saidas[3] > 0) {
                                    playerIA.apertarEsquerda();
                                    playerIA.incrementarPontuacao(1); // 1 ponto para esquerda
                                    redeNeural.incrementarPontuacao(1);
                                    desafioMeteoro = true;
                                }

                                /*
                                System.out.println("Debugando identificador Inimigo "+ indentificadorInimigo);
                                System.out.println("Debugando desafio Inimigo Terra "+ desafioTerrestre);
                                System.out.println("Debugando desafio Inimigo voa "+ desafioVoador);
                                System.out.println("Debugando desafio Inimigo mete "+ desafioMeteoro);
                                 */

                                acertou = false;

                                switch (indentificadorInimigo) {
                                    case 1: // Terrestre
                                        if (desafioTerrestre == true) {
                                            taxaInimigoTerrestre++;
                                            acertou = true;
                                        }else {
                                            acertou = false;
                                        }
                                        break;
                                    case 2: // Voador
                                        if (desafioVoador == true) {
                                            taxaInimigoVoador++;
                                            acertou = true;
                                        }else {
                                            acertou = false;
                                        }
                                        break;
                                    case 3: // Meteoro
                                        if (desafioMeteoro == true) {
                                            taxaInimigoMeteoro++;
                                            acertou = true;
                                        }else {
                                            acertou = false;
                                        }
                                        break;
                                }

                                /*
                                System.out.println("Debugando Acertou? "+ acertou);
                                System.out.println("Debugando desafio Inimigo Terra "+ desafioTerrestre);
                                System.out.println("Debugando desafio Inimigo voa "+ desafioVoador);
                                System.out.println("Debugando desafio Inimigo mete "+ desafioMeteoro);

                                 */

                                // Atualiza taxas
                                if (acertou == true) {
                                    taxaDeAcerto++;
                                } else {
                                    taxaDeErro++;
                                }


                                // Atualiza fitness com pesos
                                fitness = taxaDeAcerto * 10 - taxaDeErro * 2;
                                redeNeural.setFitness(fitness);

                                // Verifica colisão com PlayerIA
                                if (sensores.verificarColisao(playerIA, inimigo) || sensores.tocandoBorda(playerIA)) {
                                    coleta.add(playerIA);
                                    redesNeuraisArmazenadas.add(redesNeurais.get(j));
                                    // Armazena o fitness antes de remover o dinossauro
                                    fitnessHistorico.add(redeNeural.getFitness());
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

                // Imprime o histórico de fitness da geração
                imprimeHistoricoFitness(fitnessHistorico, geracaoAtual);
                fitnessHistorico.clear(); // Limpa a lista após imprimir


                // Seleciona os melhores players com base na pontuação
                //System.out.println("coleta tamanho: " + coleta.size());
                //System.out.println("redesNeurais tamanho: " + redesNeurais.size());


                coleta = selecaoPopulacao(coleta, numPlayers);
                //redesNeuraisSelecionadaRoleta = selecaoRedeNeural(redesNeuraisArmazenadas, numPlayers);


                redesNeuraisSelecionadaRoleta = selecaoRoleta(redesNeuraisArmazenadas, numPlayers);

                /*
                // Impressão das redes selecionadas
                System.out.println("Redes selecionadas pela roleta:");
                for (int i = 0; i < redesNeuraisSelecionadaRoleta.size(); i++) {
                    System.out.println((i + 1) + "º - " + redesNeuraisSelecionadaRoleta.get(i));
                }
                System.out.println("Fim da seleção por roleta.\n");

                 */



                // Seleciona a melhor rede neural antes de limpar as listas
                if (!coleta.isEmpty() && !redesNeuraisArmazenadas.isEmpty()) {
                    //melhorRede = selecaoMelhorRede(coleta, redesNeuraisArmazenadas);
                    //melhorRede = selecaoMelhorRede(redesNeuraisSelecionadaRoleta);
                    melhorRede = selecaoMelhorRede(redesNeuraisSelecionadaRoleta);
                    System.out.println("Imprimindo melhor rede: " + melhorRede);

                    // Adicionando a rede neural com o cronômetro
                    redesNeuraisMelhorDesempenho.add(new RedeNeuralDesempenho(melhorRede, Cronometro));

                    //Adicionando o log ou Pesos da minha rede
                    LogMelhoresRedes.add(melhorRede);

                }

                if (geracaoAtual < totalGeracao) {
                    // Reinicializa a população
                    player2List.clear();
                    redesNeurais.clear();
                    redesNeuraisArmazenadas.clear();
                    coleta.clear();

                    Cronometro = 0;
                    inimigosCriados = 0;

                    // Usa o novo método com as redes selecionadas por roleta
                    inicializarPopulacaoRoleta(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, redesNeuraisSelecionadaRoleta);

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
        for (int i = 0; i < redesNeuraisMelhorDesempenho.size(); i++) {
            System.out.println((i + 1) + "º - " + redesNeuraisMelhorDesempenho.get(i));
        }

        // Salva os resultados em arquivo
        salvarResultadosEmArquivo(redesNeuraisMelhorDesempenho, totalGeracao);

        System.out.println("Colata das informações de cada rede " + totalGeracao + " gerações.");
        for (int i = 0; i < LogMelhoresRedes.size(); i++) {
            System.out.println((i + 1) + "ª melhor rede:");
            //LogMelhoresRedes.get(i).imprimirTodos();
            System.out.println("");
        }

        System.out.println("Fim Ranking.");
    }

    // Dentro da sua funcao inicializarPopulacao, aplique essa lógica para respeitar o elitismo absoluto:
    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste3> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela,
                                             RedeNeuralTeste3 melhorRede) {
        int numElite = numPlayers/5;

        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20; // Posicione-os com espaçamento entre si
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA);

            RedeNeuralTeste3 novaRede;
            if (melhorRede != null && i < numElite) {
                novaRede = new RedeNeuralTeste3(melhorRede.getNumEntradasNeuronios(), melhorRede.getNumOcultos1Neuronios(),
                        melhorRede.getNumOcultos2Neuronios(), melhorRede.getNumSaidasNeuronios());
                novaRede.copiarPesos2(melhorRede); // Cópia exata
            } else {
                novaRede = new RedeNeuralTeste3(7, 14, 14, 4); // Criar nova rede com valores aleatórios
            }

            redesNeurais.add(novaRede);
        }

        // Aplicar crossover e mutação só nos descendentes (a partir do índice numElite)
        if (melhorRede != null) {
            List<RedeNeuralTeste3> descendentes = redesNeurais.subList(numElite, redesNeurais.size());
            melhorRede.aplicarCrossoverComMelhor(melhorRede, descendentes);
            melhorRede.aplicarMutacaoPopulacional(descendentes);
        }
    }

    private static void inicializarPopulacaoRoleta(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste3> redesNeurais,
                                                   Movimento movimento, Sensores sensores, Som som, GameWindow janela,
                                                   List<RedeNeuralTeste3> redesSelecionadasRoleta) {
        int numElite = numPlayers/5; // Mantém os 3 melhores intactos

        // Verifica se temos redes selecionadas suficientes
        if (redesSelecionadasRoleta == null || redesSelecionadasRoleta.isEmpty()) {
            System.out.println("AVISO: Lista de redes selecionadas vazia! Inicializando com redes aleatórias.");
            // Inicializa com redes aleatórias se não houver seleção
            for (int i = 0; i < numPlayers; i++) {
                int posX = 50 + i * 20;
                PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
                player2List.add(playerIA);
                janela.adicionarObjeto(playerIA);
                redesNeurais.add(new RedeNeuralTeste3(7, 14, 14, 4));
            }
            return;
        }

        // Cria os dinossauros e suas redes neurais
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20;
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA);

            RedeNeuralTeste3 novaRede;

            if (i < numElite) {
                // ELITISMO: Mantém os melhores indivíduos intactos
                RedeNeuralTeste3 redeBase = redesSelecionadasRoleta.get(i % redesSelecionadasRoleta.size());
                novaRede = redeBase.clonar();
                System.out.println("Indivíduo " + (i + 1) + ": ELITE (cópia exata)");
            } else {
                // CROSSOVER: Cria novos indivíduos através de crossover
                // Seleciona dois pais aleatórios da lista de selecionados
                Random random = new Random();
                int indicePai1 = random.nextInt(redesSelecionadasRoleta.size());
                int indicePai2 = random.nextInt(redesSelecionadasRoleta.size());

                // Garante que os pais sejam diferentes
                while (indicePai2 == indicePai1 && redesSelecionadasRoleta.size() > 1) {
                    indicePai2 = random.nextInt(redesSelecionadasRoleta.size());
                }

                RedeNeuralTeste3 pai1 = redesSelecionadasRoleta.get(indicePai1);
                RedeNeuralTeste3 pai2 = redesSelecionadasRoleta.get(indicePai2);

                // Realiza crossover entre os dois pais
                novaRede = RedeNeuralTeste3.crossover(pai1, pai2);

                // Aplica mutação no indivíduo criado pelo crossover
                List<RedeNeuralTeste3> listaParaMutacao = new ArrayList<>();
                listaParaMutacao.add(novaRede);
                novaRede.aplicarMutacaoPopulacional(listaParaMutacao);

                System.out.println("Indivíduo " + (i + 1) + ": CROSSOVER entre pais " + (indicePai1 + 1) + " e " + (indicePai2 + 1) + " + MUTAÇÃO");
            }

            redesNeurais.add(novaRede);
        }

        System.out.println("População inicializada: " + numElite + " elite + " + (numPlayers - numElite) + " crossover");
    }

    private static int aumentaVelocidade(int Cronometro){
        int velocidadeInimigos = 0;

        // Calcular a fórmula com arredondamento para cima
        double resultado = (((Cronometro / 500.0) * 3) + 5) * -1;

        // Arredondar para cima e converter para inteiro
        velocidadeInimigos = (int) Math.ceil(resultado);

        return velocidadeInimigos;
    }

    private static void criarInimigos(List<Inimigo> inimigos2, Movimento movimento, Sensores sensores, GameWindow janela, int cronometro, int velocidadeInimigos) {
        Random random = new Random();
        Inimigo inimigo;

        if (cronometro < 500) {
            // Antes de 1000, cria um InimigoTerrestre ou InimigoVoador
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            } else {
                inimigo = new InimigoVoador(600, 320, 70, 50, "pterodáctilo_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
            }
        } else {
            if (random.nextInt(2) == 0) {
                inimigo = new InimigoTerrestre(600, 350, 70, 50, "triceraptor_0.png", velocidadeInimigos, 0, movimento, sensores, janela);
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

    public static List<PlayerIA> selecaoPopulacao(List<PlayerIA> populacao, int numSelecionados) {
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

    public static ArrayList<RedeNeuralTeste3> selecaoRedeNeural(List<RedeNeuralTeste3> redesNeurais, int numSelecionados) {
        // Verifica se a população está vazia
        if (redesNeurais == null || redesNeurais.isEmpty()) {
            System.out.println("A população está vazia.");
            return new ArrayList<>();
        }

        // Copia a população para evitar modificar a lista original
        List<RedeNeuralTeste3> copiaPopulacaoRede = new ArrayList<>(redesNeurais);

        // Ordena a cópia com base na pontuação (do maior para o menor)
        copiaPopulacaoRede.sort((p1, p2) -> Double.compare(p2.getPontuacao(), p1.getPontuacao()));

        // Garante que numSelecionados não ultrapasse o tamanho da lista
        numSelecionados = Math.min(numSelecionados, copiaPopulacaoRede.size());

        // Exibe o ranqueamento no console
        System.out.println("Ranking da População:");
        for (int i = 0; i < copiaPopulacaoRede.size(); i++) {
            System.out.println((i + 1) + "º - " + copiaPopulacaoRede.get(i));
        }
        System.out.println("Fim Ranking:");

        // Retorna os melhores indivíduos
        return new ArrayList<>(copiaPopulacaoRede.subList(0, numSelecionados));
    }


    public static RedeNeuralTeste3 selecaoMelhorRede(List<RedeNeuralTeste3> redesNeurais) {
        if (redesNeurais.isEmpty()) {
            return null;
        }

        return redesNeurais.get(0);
    }

    private static void imprimeHistoricoFitness(List<Double> fitnessHistorico, int geracaoAtual) {
        // Imprime o histórico de fitness da geração
        System.out.println("\nHistórico de Fitness da Geração " + geracaoAtual + ":");
        for (int i = 0; i < fitnessHistorico.size(); i++) {
            System.out.println("Dinossauro " + (i + 1) + ": " + fitnessHistorico.get(i));
        }
        System.out.println("Média de Fitness: " + (fitnessHistorico.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)));
        System.out.println("Maior Fitness: " + (fitnessHistorico.stream().mapToDouble(Double::doubleValue).max().orElse(0.0)));
        System.out.println("Menor Fitness: " + (fitnessHistorico.stream().mapToDouble(Double::doubleValue).min().orElse(0.0)));
        System.out.println();
    }

    //Recebe uma populacao (lista de redes neurais)
    //Recebe um número quantidadeSelecionados que define quantos indivíduos retornar
    public static List<RedeNeuralTeste3> selecaoRoleta(List<RedeNeuralTeste3> populacao, int quantidadeSelecionados) {
        //Cria uma nova lista para guardar os indivíduos selecionados da roleta.
        List<RedeNeuralTeste3> selecionados = new ArrayList<>();

        //verifica se a população está vazia
        if (populacao == null || populacao.isEmpty()) {
            System.out.println("AVISO: População vazia na seleção por roleta!");
            return selecionados;
        }

        // Cálculo da soma total dos valores de fitness (apenas positivos)
        // Isso representa o "tamanho total da roleta". Cada indivíduo terá uma "fatia" proporcional ao seu fitness.
        double somaFitness = 0.0;
        System.out.println("\nFitness dos indivíduos na população:");
        for (RedeNeuralTeste3 individuo : populacao) {
            double fitness = individuo.getFitness();
            System.out.println("Fitness: " + fitness);

            // Fitness negativos são ignorados — não contribuem para a chance de serem selecionados
            somaFitness += (fitness > 0) ? fitness : 0;
        }
        System.out.println("Soma total do fitness: " + somaFitness);


        // Processo principal da seleção por roleta
        Random rand = new Random();
        for (int i = 0; i < quantidadeSelecionados; i++) {
            // Gera um valor aleatório entre 0 e somaFitness, representando um "ponto" na roleta

            double ponto = rand.nextDouble() * somaFitness;
            double acumulado = 0.0;

            // Percorre os indivíduos da população somando seus fitness até passar do "ponto"
            // O indivíduo correspondente à posição onde o acumulado ultrapassa o ponto é selecionado
            for (RedeNeuralTeste3 individuo : populacao) {
                double fitness = individuo.getFitness();
                if (fitness > 0) {
                    acumulado += fitness;
                    // Quando o acumulado passa do ponto, selecionamos o indivíduo
                    if (acumulado >= ponto) {
                        selecionados.add(individuo.clonar()); // necessário clonar para evitar efeitos colaterais
                        break;
                    }
                }
            }
        }


        // Exibe o ranqueamento no console
        System.out.println("\nRanking da População (Seleção por Roleta):");
        if (selecionados.isEmpty()) {
            System.out.println("AVISO: Nenhum indivíduo foi selecionado!");
        } else {
            for (int i = 0; i < selecionados.size(); i++) {
                System.out.println((i + 1) + "º - " + selecionados.get(i));
            }
        }
        System.out.println("Fim Ranking Roleta:\n");

        return selecionados;
    }

    // Metodo para salvar resultados em arquivo com renomeação automática
    private static void salvarResultadosEmArquivo(List<RedeNeuralDesempenho> redesNeuraisMelhorDesempenho, int totalGeracao) {
        try {
            // Nome base do arquivo
            String nomeBase = "Resultados";
            String extensao = ".txt";
            String nomeArquivo = nomeBase + extensao;

            // Verifica se o arquivo já existe e renomeia se necessário
            int contador = 1;
            while (Files.exists(Paths.get(nomeArquivo))) {
                nomeArquivo = nomeBase + contador + extensao;
                contador++;
            }

            // Cria o arquivo e escreve os resultados
            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
                writer.println("=== RESULTADOS DA SIMULAÇÃO DE REDES NEURAIS ===");
                writer.println("Data/Hora: " + java.time.LocalDateTime.now());
                writer.println("Simulação concluída após " + totalGeracao + " gerações.");
                writer.println();

                writer.println("=== CONFIGURAÇÕES DO ALGORITMO GENÉTICO ===");
                writer.println("Taxa de Mutação: 10% (0.1)");
                writer.println("Tipo de Seleção: Roleta");
                writer.println("Elitismo: 20% da população (melhores indivíduos preservados)");
                writer.println("Crossover: Média aritmética dos pesos dos pais");
                writer.println("Arquitetura da Rede Neural: 7→[14, 4]→14");
                writer.println();

                writer.println("=== RANKING DAS MELHORES REDES POR GERAÇÃO ===");
                for (int i = 0; i < redesNeuraisMelhorDesempenho.size(); i++) {
                    writer.println((i + 1) + "º - " + redesNeuraisMelhorDesempenho.get(i));
                }

                writer.println();
                writer.println("=== RESUMO ESTATÍSTICO ===");

                // Calcula estatísticas
                double maiorFitness = redesNeuraisMelhorDesempenho.stream()
                        .mapToDouble(RedeNeuralDesempenho::getFitness)
                        .max()
                        .orElse(0.0);

                double menorFitness = redesNeuraisMelhorDesempenho.stream()
                        .mapToDouble(RedeNeuralDesempenho::getFitness)
                        .min()
                        .orElse(0.0);

                double mediaFitness = redesNeuraisMelhorDesempenho.stream()
                        .mapToDouble(RedeNeuralDesempenho::getFitness)
                        .average()
                        .orElse(0.0);

                writer.println("Maior Fitness: " + maiorFitness);
                writer.println("Menor Fitness: " + menorFitness);
                writer.println("Média de Fitness: " + String.format("%.2f", mediaFitness));

                writer.println();
                writer.println("=== ANÁLISE DA EVOLUÇÃO ===");
                writer.println("A mutação foi aplicada em 80% da população a cada geração.");
                writer.println("Cada peso da rede neural tinha 10% de chance de ser mutado.");
                writer.println("A mutação adicionava um valor aleatório seguindo distribuição normal (Gaussiana).");
                writer.println("Os 20% melhores indivíduos (elite) foram preservados sem mutação.");
                writer.println();

                writer.println("=== FIM DOS RESULTADOS ===");
            }

            System.out.println("Resultados salvos em: " + nomeArquivo);

        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

}