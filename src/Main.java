import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
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

        //--------------------------- VARIÁVEIS DE CONTROLE
        int Cronometro = 0;
        int velocidadeInimigos = 0;

        // Variáveis de controle de geração
        int numPlayers = 20; // Número de PlayerIA
        int quantidadeVivos = numPlayers;
        int geracaoAtual = 0;
        int totalGeracao = 40;

        //Controle Inimigos
        int maxInimigos = 100;
        int inimigosCriados = 0;

        int limiteProximidade = 80; // Defina um limite adequado para a proximidade
        RedeNeuralTeste2 melhorRede = null;
        //--------------------------- VARIÁVEIS DE CONTROLE FIM

//        Player player = new Player(30, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
//        janela.adicionarObjeto(player);
//        player.adicionarListener();

        // Criação de um vetor para armazenar múltiplos PlayerIA
        List<PlayerIA> player2List = new ArrayList<>();
        List<PlayerIA> coleta = new ArrayList<>(); //Coleta pontuações de PlayerIA
        List<RedeNeuralTeste2> redesNeurais = new ArrayList<>();
        List<RedeNeuralTeste2> redesNeuraisArmazenadas = new ArrayList<>();
        List<RedeNeuralTeste2> redesNeuraisArmazenadas2 = new ArrayList<>();
        List<RedeNeuralDesempenho> redesNeuraisMelhorDesempenho = new ArrayList<>();
        List<HistoricoRede> historicoMelhoresRedes = new ArrayList<>();


        List<Inimigo> inimigos = new ArrayList<>(); //Armazena inimigos

        // Inicializa os PlayerIA e redes neurais
        inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, melhorRede);

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
                if (Cronometro >= (inimigosCriados + 1) * 50) {

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
                            playerIA.aplicarFiltro();
                            playerIA.levantar();


                            // Analisar proximidade e usar rede neural
                            if (sensores.analisarProximidade(playerIA, inimigo, limiteProximidade)) {
                                double[] entradas = {playerIA.getX(), playerIA.getY(), inimigo.getX(), inimigo.getY(), inimigo.getAltura(), inimigo.getLargura(), velocidadeInimigos};
                                RedeNeuralTeste2 redeNeural = redesNeurais.get(j);

                                double[] saidas = redeNeural.calcularSaida2(entradas);

                                if (saidas[0] > 0.5) {
                                    playerIA.apertarSaltar(); // Pular
                                } else {
                                    playerIA.apertarAbaixar(); // Abaixar
                                }

                                if (saidas[1] > 0.5) {
                                    if (inimigo.getX() >= playerIA.getX()) {
                                        playerIA.apertarEsquerda();
                                    } else {
                                        playerIA.apertarDireita();
                                    }
                                }

                                playerIA.incrementarPontuacao(1);
                                redeNeural.incrementarPontuacao(1);


                                // Verifica colisão com PlayerIA
                                if (sensores.verificarColisao(playerIA, inimigo) || sensores.tocandoBorda(playerIA)) {
                                    coleta.add(playerIA);
                                    redesNeuraisArmazenadas.add(redesNeurais.get(j));
                                    //RedeNeuralTeste2.salvarDadosEmArquivo(redesNeurais);
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

                // 👇 AVALIAÇÃO
                if (geracaoAtual == 1) {
                    redesNeuraisArmazenadas.sort(Comparator.comparingDouble(RedeNeuralTeste2::getPontuacao).reversed());
                    melhorRede = selecaoMelhorRede(redesNeuraisArmazenadas);
                    historicoMelhoresRedes.add(new HistoricoRede(copiarRede(melhorRede), melhorRede.getPontuacao()));
                } else {
                    for (RedeNeuralTeste2 individuo : redesNeuraisArmazenadas) {
                        double fitness = avaliarFitnessComparativo(individuo, historicoMelhoresRedes);
                        individuo.setFitness(fitness);
                    }
                    redesNeuraisArmazenadas.sort(Comparator.comparingDouble(RedeNeuralTeste2::getFitness).reversed());
                    melhorRede = selecaoMelhorRede(redesNeuraisArmazenadas);
                    historicoMelhoresRedes.add(new HistoricoRede(copiarRede(melhorRede), melhorRede.getFitness()));
                }

                // 👇 LOG E RESET
                System.out.println("Imprimindo melhor rede: " + melhorRede);
                redesNeuraisMelhorDesempenho.add(new RedeNeuralDesempenho(melhorRede, Cronometro));

                if (geracaoAtual < totalGeracao) {
                    player2List.clear();
                    redesNeurais.clear();
                    redesNeuraisArmazenadas.clear();
                    coleta.clear();

                    Cronometro = 0;
                    inimigosCriados = 0;

                    inicializarPopulacao(numPlayers, player2List, redesNeurais, movimento, sensores, som, janela, melhorRede);
                    quantidadeVivos = numPlayers;

                    for (Inimigo inimigo : inimigos) {
                        janela.removerObjeto(inimigo);
                    }
                    inimigos.clear();
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
        System.out.println("Fim Ranking.");
    }

    private static void inicializarPopulacao(int numPlayers, List<PlayerIA> player2List, List<RedeNeuralTeste2> redesNeurais,
                                             Movimento movimento, Sensores sensores, Som som, GameWindow janela,
                                             RedeNeuralTeste2 melhorRede) {
        int numElite = 2;

        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20; // Posicione-os com um espaçamento entre si
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA); // Adiciona o PlayerIA à janela

            RedeNeuralTeste2 novaRede = new RedeNeuralTeste2(7, 14,14, 2);
            if (melhorRede != null && i < numElite) {
                // Elitismo puro: sem mudanças
                novaRede.copiarPesos2(melhorRede);
            }

            redesNeurais.add(novaRede);
        }

        // Se temos uma melhor rede, cruzamos com os descendentes
        if (melhorRede != null) {
            List<RedeNeuralTeste2> descendentes = redesNeurais.subList(numElite, redesNeurais.size());
            melhorRede.aplicarCrossoverComMelhor(melhorRede, descendentes);
            melhorRede.aplicarMutacaoPopulacional(descendentes);
        }
    }


    private static int aumentaVelocidade(int Cronometro){
        int velocidadeInimigos = 0;

        // Calcular a fórmula com arredondamento para cima
        double resultado = (((Cronometro / 500.0) * 3) + 5) * -1;

        // Arredondar para cima e converter para inteiro
        velocidadeInimigos = (int) Math.ceil(resultado);

        return velocidadeInimigos;
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

    public static ArrayList<RedeNeuralTeste2> selecaoRedeNeural(List<RedeNeuralTeste2> redesNeurais, int numSelecionados) {
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

    // Adicione esse metodo na classe Main, fora do metodo main()
    public static double avaliarFitness(RedeNeuralTeste2 rede, int partidas, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        int somaPontuacoes = 0;

        for (int i = 0; i < partidas; i++) {
            PlayerIA player = new PlayerIA(50, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela); // sem janela
            int pontuacao = 0;
            int cronometro = 0;
            int semEventos = 0;

            List<Inimigo> inimigos = new ArrayList<>();
            boolean vivo = true;

            while (vivo && cronometro < 1000) {
                boolean houveEvento = false;

                // Geração periódica de inimigos
                if (cronometro % 100 == 0) {
                    int velocidade = aumentaVelocidade(cronometro);
                    criarInimigos3(inimigos, movimento, sensores, janela, cronometro, velocidade); // sem janela
                }

                // Atualizar e interagir com os inimigos
                for (Inimigo inimigo : new ArrayList<>(inimigos)) {
                    inimigo.atualizar();

                    if (sensores.analisarProximidade(player, inimigo, 80)) {
                        houveEvento = true;

                        double[] entradas = {
                                player.getX(), player.getY(),
                                inimigo.getX(), inimigo.getY(),
                                inimigo.getAltura(), inimigo.getLargura(),
                                5 // velocidade fixa para simplificação
                        };

                        double[] saidas = rede.calcularSaida2(entradas);

                        if (saidas[0] > 0.5) player.apertarSaltar();
                        else player.apertarAbaixar();

                        if (saidas[1] > 0.5) {
                            if (inimigo.getX() >= player.getX()) player.apertarEsquerda();
                            else player.apertarDireita();
                        }

                        if (sensores.verificarColisao(player, inimigo) || sensores.tocandoBorda(player)) {
                            vivo = false;
                            break;
                        }
                    }
                }

                // Atualização de física
                movimento.atualizarFisica(player, new Chao(0, 400, 600, 200));
                movimento.controlarSalto(player);

                // Limita tamanho da lista de inimigos
                if (inimigos.size() > 20) {
                    inimigos.remove(0);
                }

                // Verifica se a rodada está muito longa sem eventos
                if (!houveEvento) semEventos++;
                else semEventos = 0;

                if (semEventos > 200) {
                    break; // Interrompe se passou 200 ciclos sem evento relevante
                }

                pontuacao++;
                cronometro++;
            }

            // Evita uso da janela aqui
            inimigos.clear();
            somaPontuacoes += pontuacao;
        }

        return somaPontuacoes / (double) partidas;
    }

    public static double avaliarFitnessComparativo(RedeNeuralTeste2 novaRede, List<HistoricoRede> historico) {
        if (historico.isEmpty()) return 0;

        double soma = 0;
        for (HistoricoRede h : historico) {
            double distancia = novaRede.distanciaPara(h.getRede()); // você precisa implementar esse método
            double peso = h.getFitness(); // mais fitness = mais peso
            soma += (1.0 / (1.0 + distancia)) * peso;
        }

        return soma / historico.size();
    }

    public static RedeNeuralTeste2 copiarRede(RedeNeuralTeste2 original) {
        RedeNeuralTeste2 copia = new RedeNeuralTeste2(
                original.getNumEntradasNeuronios(),
                original.getNumOcultos1Neuronios(),
                original.getNumOcultos2Neuronios(),
                original.getNumSaidasNeuronios()
        );
        copia.copiarPesos2(original); // supondo que esse metodo copia todos os pesos e biases
        return copia;
    }

}