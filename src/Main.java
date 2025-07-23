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
        int totalGeracao = 30;

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
        double fitness = 0;

        double taxaInimigoTerrestre = 0;
        double taxaInimigoVoador = 0;
        double taxaInimigoMeteoro = 0;

        //Vou usar para identificar se o dinossauro executou a ação correta, com base nos 3 inimigos/desafio
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

        // Lista para armazenar todos os indivíduos de todas as gerações
        List<List<RedeNeuralTeste3>> todasGeracoes = new ArrayList<>();
        List<Integer> cronometrosGeracoes = new ArrayList<>();

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
                                // Entradas simplificadas como no projeto original
                                double[] entradas = {
                                        inimigo.getX() - playerIA.getX(), // Distância horizontal
                                        inimigo.getY() - playerIA.getY(), // Diferença de altura
                                        velocidadeInimigos // Velocidade
                                };
                                RedeNeuralTeste3 redeNeural = redesNeurais.get(j);

                                double[] saidas = redeNeural.calcularSaida(entradas);

                                // Lógica sem viés - rede neural decide sozinha
                                // saidas[0] > 0: pular
                                if (saidas[0] > 0) {
                                    playerIA.apertarSaltar();
                                }

                                if (saidas[1] > 0) {
                                    playerIA.apertarAbaixar();
                                }

                                // Recompensa baseada apenas na sobrevivência (como no original)
                                // A rede neural recebe recompensa apenas por continuar viva
                                redeNeural.incrementarPontuacao(1);
                                redeNeural.setFitness(redeNeural.getPontuacao());

                                // Verifica colisão com PlayerIA
                                if (sensores.verificarColisao(playerIA, inimigo) || sensores.tocandoBorda(playerIA)) {
                                    // Armazena o cronômetro individual do dinossauro quando ele morre
                                    redeNeural.setCronometroIndividual(Cronometro);

                                    coleta.add(playerIA);
                                    redesNeuraisArmazenadas.add(redeNeural);
                                    // Armazena o fitness antes de remover o dinossauro
                                    fitnessHistorico.add(redeNeural.getFitness());
                                    // Salva ou imprime taxa de acerto e erro ao eliminar
                                    System.out.println("Dinossauro eliminado: Fitness=" + redeNeural.getFitness() + ", Acertos=" + redeNeural.getTaxaDeAcerto() + ", Erros=" + redeNeural.getTaxaDeErro()
                                            + ", DesafioTerrestre=" + redeNeural.getDesafioTerrestre()
                                            + ", DesafioVoador=" + redeNeural.getDesafioVoador()
                                            + ", DesafioMeteoro=" + redeNeural.getDesafioMeteoro());
                                    // Resetar desafios ao eliminar
                                    redeNeural.resetarDesafios();
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

                // Armazena todos os indivíduos da geração atual
                List<RedeNeuralTeste3> geracaoAtualIndividuos = new ArrayList<>(redesNeuraisArmazenadas);
                todasGeracoes.add(geracaoAtualIndividuos);
                cronometrosGeracoes.add(Cronometro);

                fitnessHistorico.clear(); // Limpa a lista após imprimir


                // Seleciona os melhores players com base na pontuação
                //System.out.println("coleta tamanho: " + coleta.size());
                //System.out.println("redesNeurais tamanho: " + redesNeurais.size());


                coleta = selecaoPopulacao(coleta, numPlayers);
                //redesNeuraisSelecionadaRoleta = selecaoRedeNeural(redesNeuraisArmazenadas, numPlayers);


                redesNeuraisSelecionadaRoleta = selecaoOriginal(redesNeuraisArmazenadas, numPlayers);

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
                    // Ordena as redes por fitness e pega a melhor
                    List<RedeNeuralTeste3> redesOrdenadas = new ArrayList<>(redesNeuraisArmazenadas);
                    redesOrdenadas.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
                    melhorRede = redesOrdenadas.get(0);
                    System.out.println("Imprimindo melhor rede: " + melhorRede);

                    // Adicionando a rede neural com o cronômetro
                    redesNeuraisMelhorDesempenho.add(new RedeNeuralDesempenho(melhorRede, melhorRede.getCronometroIndividual()));

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

                    // Resetar taxas de acerto/erro, desafios e acertos/erros por tipo das redes selecionadas para nova geração
                    for (RedeNeuralTeste3 rede : redesNeuraisSelecionadaRoleta) {
                        rede.resetarTaxas();
                        rede.resetarDesafios();
                    }

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

        // Salva todos os indivíduos de todas as gerações
        salvarTodosIndividuosEmArquivo(todasGeracoes, cronometrosGeracoes, totalGeracao);

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
                novaRede = new RedeNeuralTeste3(3, 2, 6, 6); // Rede original: 3 entradas, 1 saída, 6 neurônios ocultos
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
        // Verifica se temos redes selecionadas suficientes
        if (redesSelecionadasRoleta == null || redesSelecionadasRoleta.isEmpty()) {
            System.out.println("AVISO: Lista de redes selecionadas vazia! Inicializando com redes aleatórias.");
            // Inicializa com redes aleatórias se não houver seleção
            for (int i = 0; i < numPlayers; i++) {
                int posX = 50 + i * 20;
                PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
                player2List.add(playerIA);
                janela.adicionarObjeto(playerIA);
                redesNeurais.add(new RedeNeuralTeste3(3, 2, 6, 6));
            }
            return;
        }

        // Cria os dinossauros e suas redes neurais
        for (int i = 0; i < numPlayers; i++) {
            int posX = 50 + i * 20;
            PlayerIA playerIA = new PlayerIA(posX, 320, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
            player2List.add(playerIA);
            janela.adicionarObjeto(playerIA);

            // Clona o melhor indivíduo (primeiro da lista já ordenada por fitness)
            RedeNeuralTeste3 novaRede = redesSelecionadasRoleta.get(0).clonar();

            // Aplica mutação em todos exceto o primeiro (que mantém o melhor intacto)
            if (i > 0) {
                novaRede.aplicarMutacao();
            }

            redesNeurais.add(novaRede);
        }

        System.out.println("População inicializada com método original: clonagem do melhor + mutação");
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

        if (cronometro < 5000) {
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
    public static List<RedeNeuralTeste3> selecaoOriginal(List<RedeNeuralTeste3> populacao, int quantidadeSelecionados) {
        List<RedeNeuralTeste3> selecionados = new ArrayList<>();

        if (populacao == null || populacao.isEmpty()) {
            return selecionados;
        }

        // 1. Ordenar por fitness (melhor primeiro)
        List<RedeNeuralTeste3> ordenados = new ArrayList<>(populacao);
        ordenados.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        // 2. Clonar o melhor para todos
        RedeNeuralTeste3 melhor = ordenados.get(0);
        for (int i = 0; i < quantidadeSelecionados; i++) {
            RedeNeuralTeste3 clone = melhor.clonar();
            if (i > 0) clone.aplicarMutacao(); // Mutar todos exceto o primeiro
            selecionados.add(clone);
        }

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
                writer.println("Arquitetura da Rede Neural: 7→[14, 14]→4");
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

                writer.println("=== FIM DOS RESULTADOS ===");
            }

            System.out.println("Resultados salvos em: " + nomeArquivo);

        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // Salva todos os indivíduos de todas as gerações
    private static void salvarTodosIndividuosEmArquivo(List<List<RedeNeuralTeste3>> todasGeracoes, List<Integer> cronometrosGeracoes, int totalGeracao) {
        try {
            // Nome base do arquivo
            String nomeBase = "Individuos";
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
                writer.println("=== TODOS OS INDIVÍDUOS DE TODAS AS GERAÇÕES ===");
                writer.println("Data/Hora: " + java.time.LocalDateTime.now());
                writer.println("Simulação concluída após " + totalGeracao + " gerações.");
                writer.println();

                // Estatísticas dos melhores indivíduos por geração
                for (int i = 0; i < todasGeracoes.size(); i++) {
                    List<RedeNeuralTeste3> geracaoAtualIndividuos = todasGeracoes.get(i);
                    writer.println("######################################################");
                    writer.println("Geração " + (i + 1) + ":");
                    writer.println();

                    // Ordena os indivíduos por fitness (melhor primeiro)
                    List<RedeNeuralTeste3> individuosOrdenados = new ArrayList<>(geracaoAtualIndividuos);
                    individuosOrdenados.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

                    // Mostra todos os indivíduos da geração
                    for (int j = 0; j < individuosOrdenados.size(); j++) {
                        RedeNeuralTeste3 dino = individuosOrdenados.get(j);
                        writer.println("Dino" + (j + 1) + ": " + dino + " | Pontuação: " + dino.getPontuacao() + " | Fitness: " + dino.getFitness() + " | Cronômetro: " + dino.getCronometroIndividual());
                    }
                    writer.println("Dino Fim");
                    writer.println();

                    // Calcula estatísticas da geração
                    double piorFitnessGeracao = individuosOrdenados.stream()
                            .mapToDouble(RedeNeuralTeste3::getFitness)
                            .min()
                            .orElse(0.0);

                    double mediaFitnessGeracao = individuosOrdenados.stream()
                            .mapToDouble(RedeNeuralTeste3::getFitness)
                            .average()
                            .orElse(0.0);

                    double melhorFitnessGeracao = individuosOrdenados.stream()
                            .mapToDouble(RedeNeuralTeste3::getFitness)
                            .max()
                            .orElse(0.0);

                    double mediaCronometroGeracao = individuosOrdenados.stream()
                            .mapToDouble(RedeNeuralTeste3::getCronometroIndividual)
                            .average()
                            .orElse(0.0);

                    // Conta quantos dinos estão acima da média do cronômetro
                    long dinosAcimaMedia = individuosOrdenados.stream()
                            .filter(dino -> dino.getCronometroIndividual() > mediaCronometroGeracao)
                            .count();

                    writer.println("Pior fitness da geração: " + piorFitnessGeracao);
                    writer.println("Média dos fitness da geração: " + String.format("%.2f", mediaFitnessGeracao));
                    writer.println("Melhor fitness da geração: " + melhorFitnessGeracao);
                    writer.println("Média Cronômetro geração: " + String.format("%.2f", mediaCronometroGeracao));
                    writer.println("Quantos dinos estão acima da média do cronômetro: " + dinosAcimaMedia);
                    writer.println();
                }

                writer.println("######################################################");
                writer.println("RESUMO GERAL:");
                writer.println();

                // Calcula estatísticas gerais
                double maiorFitness = todasGeracoes.stream()
                        .flatMap(List::stream)
                        .mapToDouble(RedeNeuralTeste3::getFitness)
                        .max()
                        .orElse(0.0);

                double menorFitness = todasGeracoes.stream()
                        .flatMap(List::stream)
                        .mapToDouble(RedeNeuralTeste3::getFitness)
                        .min()
                        .orElse(0.0);

                double mediaFitness = todasGeracoes.stream()
                        .flatMap(List::stream)
                        .mapToDouble(RedeNeuralTeste3::getFitness)
                        .average()
                        .orElse(0.0);

                double mediaCronometro = todasGeracoes.stream()
                        .flatMap(List::stream)
                        .mapToDouble(RedeNeuralTeste3::getCronometroIndividual)
                        .average()
                        .orElse(0.0);

                writer.println("Pior fitness de todas as gerações: " + menorFitness);
                writer.println("Média dos fitness de todas as gerações: " + String.format("%.2f", mediaFitness));
                writer.println("Melhor fitness de todas as gerações: " + maiorFitness);
                writer.println("Média Cronômetro gerações: " + String.format("%.2f", mediaCronometro));
                writer.println();

                writer.println("=== FIM DOS RESULTADOS ===");
            }

            System.out.println("Resultados salvos em: " + nomeArquivo);

        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

}