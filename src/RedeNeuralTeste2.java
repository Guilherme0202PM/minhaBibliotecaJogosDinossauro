import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RedeNeuralTeste2 {
    private int quantidadeNeuroniosEntrada, quantidadeNeuroniosCamadaOculta1, quantidadeNeuroniosCamadaOculta2,  quantidadeNeuroniosSaida;

    private double taxaMutacaoPopulacional = 0.8; // 50% de chance de mutação na população
    private double taxaMutacaoIndividual = 0.3;   // 30% de diferença entre indivíduos

    private double[][] pesosEntradaOculta1; // Pesos da camada de entrada para a camada oculta
    private double[][] pesosEntradaOculta2;
    private double[] biasOculta1; // Bias para a camada oculta
    private double[] biasOculta2;
    private double[][] pesosOcultaSaida1; // Pesos da camada oculta para a camada de saída
    private double[][] pesosOcultaSaida2;
    private double[] biasSaida; // Bias para a camada de saída
    private double [] entradas;
    private Random random;

    private int pontuacao, erroAcao;
    private int acertos; // Contador de acertos (ações corretas)
    private int totalAcoes; // Contador total de ações
    private int tempoSobrevivencia; // Tempo de sobrevivência do jogador

    public RedeNeuralTeste2(int quantidadeNeuroniosEntrada, int quantidadeNeuroniosCamadaOculta1, int quantidadeNeuroniosSaida) {
        this.quantidadeNeuroniosEntrada = quantidadeNeuroniosEntrada;
        this.quantidadeNeuroniosCamadaOculta1 = quantidadeNeuroniosCamadaOculta1;
        this.quantidadeNeuroniosSaida = quantidadeNeuroniosSaida;
        random = new Random();

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta1 = new double[quantidadeNeuroniosEntrada][quantidadeNeuroniosCamadaOculta1];
        pesosOcultaSaida1 = new double[quantidadeNeuroniosCamadaOculta1][quantidadeNeuroniosSaida];
        biasOculta1 = new double[quantidadeNeuroniosCamadaOculta1];
        biasSaida = new double[quantidadeNeuroniosSaida];

        // Inicialize o array de entradas com o tamanho adequado
        entradas = new double[quantidadeNeuroniosEntrada];

        inicializarPesos();
    }

    public void recebeEntradas(double[] valoresEntradas){
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++){
            entradas[i] = valoresEntradas[i];
            //System.out.println("Entrada coletada"+ entradas[i]);
        }
    }

    private void inicializarPesos() {
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                //pesosEntradaOculta[i][j] = random.nextDouble() * 1.0 - 0.5; // [-0.5, 0.5]
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosEntradas: "+pesosEntradaOculta[i][j]);
            }
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            for (int j = 0; j < quantidadeNeuroniosSaida; j++) {
                pesosOcultaSaida1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosOcultaSaida: "+pesosOcultaSaida[i][j]);

            }
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasOculta: "+biasOculta[i]);

        }
        for (int i = 0; i < quantidadeNeuroniosSaida; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasSaida: "+biasSaida[i]);

        }
    }

    public double[] calcularSaida(double[] entradas) {
        double[] saidaOculta = new double[quantidadeNeuroniosCamadaOculta1];
        double[] saidaFinal = new double[quantidadeNeuroniosSaida];

        // Cálculo da camada oculta
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            double soma = biasOculta1[i];
            //System.out.println("Cálculo da camada oculta soma antes: "+soma);
            for (int j = 0; j < quantidadeNeuroniosEntrada; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
                //System.out.println("Cálculo da camada oculta soma depois: "+soma);
            }
            saidaOculta[i] = tanh(soma);
            //System.out.println("Cálculo da camada oculta relu: "+saidaOculta[i]);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < quantidadeNeuroniosSaida; i++) {
            double soma = biasSaida[i];
            //System.out.println("Cálculo da camada de saída soma antes: "+soma);
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida1[j][i];
                //System.out.println("Cálculo da camada de saída soma depois: "+soma);
            }
            saidaFinal[i] = sigmoid(soma);
            //System.out.println("Cálculo da camada oculta sigmoid: "+saidaFinal[i]);

        }
        return saidaFinal;
    }

    public void copiarPesos(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, quantidadeNeuroniosCamadaOculta1);
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida1[i], 0, this.pesosOcultaSaida1[i], 0, quantidadeNeuroniosSaida);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, quantidadeNeuroniosCamadaOculta1);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, quantidadeNeuroniosSaida);
    }

    public void ajustarPesosPorCondicao(double[] entradas, double fator) {
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
    }

    //----------------------------------------------------------------------------------------------------
    //------------------------------------Rede Neural com duas Camadas
    public RedeNeuralTeste2(int quantidadeNeuroniosEntrada, int quantidadeNeuroniosCamadaOculta1, int quantidadeNeuroniosCamadaOculta2, int quantidadeNeuroniosSaida) {
        this.quantidadeNeuroniosEntrada = quantidadeNeuroniosEntrada;
        this.quantidadeNeuroniosCamadaOculta1 = quantidadeNeuroniosCamadaOculta1;
        this.quantidadeNeuroniosCamadaOculta2 = quantidadeNeuroniosCamadaOculta2;
        this.quantidadeNeuroniosSaida = quantidadeNeuroniosSaida;
        random = new Random();

        pesosEntradaOculta1 = new double[quantidadeNeuroniosEntrada][quantidadeNeuroniosCamadaOculta1];
        biasOculta1 = new double[quantidadeNeuroniosCamadaOculta1];
        pesosEntradaOculta2 = new double[quantidadeNeuroniosCamadaOculta1][quantidadeNeuroniosCamadaOculta2];
        biasOculta2 = new double[quantidadeNeuroniosCamadaOculta2];
        pesosOcultaSaida2 = new double[quantidadeNeuroniosCamadaOculta2][quantidadeNeuroniosSaida];
        biasSaida = new double[quantidadeNeuroniosSaida];

        entradas = new double[quantidadeNeuroniosEntrada];


        inicializarPesos2();
    }

    private void inicializarPesos2() {
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta2; j++) {
                pesosEntradaOculta2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
            for (int j = 0; j < quantidadeNeuroniosSaida; j++) {
                pesosOcultaSaida2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
            biasOculta2[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < quantidadeNeuroniosSaida; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0;
        }
    }

    public double[] calcularSaida2(double[] entradas) {
        double[] saidaOculta1 = new double[quantidadeNeuroniosCamadaOculta1];
        double[] saidaOculta2 = new double[quantidadeNeuroniosCamadaOculta2];
        double[] saidaFinal = new double[quantidadeNeuroniosSaida];

        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < quantidadeNeuroniosEntrada; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
            System.out.println("saidaOculta1: "+saidaOculta1[i]);

        }

        for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
            System.out.println("saidaOculta2: "+saidaOculta2[i]);

        }

        for (int i = 0; i < quantidadeNeuroniosSaida; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta2; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }
            saidaFinal[i] = relu(soma);
            System.out.println("Saida Final: "+saidaFinal[i]);
        }

        return saidaFinal;
    }

    public void copiarPesos2(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, quantidadeNeuroniosCamadaOculta1);
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta2[i], 0, this.pesosEntradaOculta2[i], 0, quantidadeNeuroniosCamadaOculta2);
        }
        for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida2[i], 0, this.pesosOcultaSaida2[i], 0, quantidadeNeuroniosSaida);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, quantidadeNeuroniosCamadaOculta1);
        System.arraycopy(outraRede.biasOculta2, 0, this.biasOculta2, 0, quantidadeNeuroniosCamadaOculta2);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, quantidadeNeuroniosSaida);
    }


    public void ajustarPesosPorCondicao2(double[] entradas, double fator) {
        int acaoCorreta = identificarInimigo(entradas);
        double[] saidasEsperadas = new double[4]; // 4 saídas: [pular, abaixar, esquerda, direita]

        // Inicializa todas as saídas como 0
        for (int i = 0; i < 4; i++) {
            saidasEsperadas[i] = 0;
        }

        // Define a saída correta como 1
        saidasEsperadas[acaoCorreta] = 1;

        // Ajusta os pesos usando backpropagation
        treinar(entradas, saidasEsperadas, 0.1); // 0.1 é a taxa de aprendizado
    }

    public int identificarInimigo(double[] entradas) {
        double xPlayer = entradas[0];
        double yPlayer = entradas[1];
        double x = entradas[2];
        double y = entradas[3];
        double altura = entradas[4];
        double largura = entradas[5];
        double velocidade = entradas[6];
        int numeroInimigos = 4;

        int[] inimigo = new int[3];

        // Determina os valores do array com base nas condições
        inimigo[0] = (x >= xPlayer) ? 0 : 1;
        inimigo[1] = (y >= 350) ? 0 : 1;
        inimigo[2] = (altura >= 70) ? 0 : 1;

        int acaoEsperada = determinarAcaoEsperada(inimigo);

        return acaoEsperada;
    }

    // Metodo para determinar a ação esperada
    public int determinarAcaoEsperada(int[] inimigo) {
        if (inimigo[1] == 0 && inimigo[2] == 1) {
            return 1;
        } else if (inimigo[1] == 1 && inimigo[2] == 1) {
            return 2;
        } else if (inimigo[0] == 0 && inimigo[2] == 0) {
            return 3;
        } else if (inimigo[0] == 1 && inimigo[2] == 0) {
            return 4;
        }
        return 0; // Caso padrão (não especificado)
    }


    // Função de arredondamento para 4 casas decimais
    private double arredondar(double valor) {
        return new BigDecimal(valor).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    // Função de ativação ReLU
    private double relu(double x) {
        return arredondar(Math.max(0, x));
    }

    // Função de ativação Sigmóide
    private double sigmoid(double x) {
        return arredondar(1 / (1 + Math.exp(-x)));
    }

    // Função de ativação Tangente Hiperbólica
    private double tanh(double x) {
        return arredondar(Math.tanh(x));
    }

    // Derivada da função de ativação Tangente Hiperbólica
    private double tanhDerivada(double x) {
        double th = Math.tanh(x);
        return arredondar(1 - (th * th)); // 1 - tanh^2(x)
    }

    public void treinar(double[] entradas, double[] saidasEsperadas, double taxaAprendizagem) {
        // Forward pass
        double[] camadaOculta = new double[quantidadeNeuroniosCamadaOculta1];
        for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
            camadaOculta[j] = biasOculta1[j];
            for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta1[i][j];
            }
            camadaOculta[j] = tanh(camadaOculta[j]);  // Mudamos para tanh;
        }

        double[] saidas = new double[quantidadeNeuroniosSaida];
        for (int j = 0; j < quantidadeNeuroniosSaida; j++) {
            saidas[j] = biasSaida[j];
            for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
                saidas[j] += camadaOculta[i] * pesosOcultaSaida1[i][j];
            }
            saidas[j] = sigmoid(saidas[j]);
        }

        // Backward pass (calcular erro e atualizar pesos)
        double[] erroSaida = new double[quantidadeNeuroniosSaida];
        for (int j = 0; j < quantidadeNeuroniosSaida; j++) {
            erroSaida[j] = saidasEsperadas[j] - saidas[j];
        }

        double[] erroOculto = new double[quantidadeNeuroniosCamadaOculta1];
        for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
            for (int k = 0; k < quantidadeNeuroniosSaida; k++) {
                erroOculto[j] += erroSaida[k] * pesosOcultaSaida1[j][k];
            }
            //erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
            erroOculto[j] *= tanhDerivada(camadaOculta[j]);  // Usando derivada do tanh
        }

        for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
            for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                pesosEntradaOculta1[i][j] += taxaAprendizagem * erroOculto[j] * entradas[i];
            }
        }

        for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
            biasOculta1[j] += taxaAprendizagem * erroOculto[j];
        }

        for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
            for (int k = 0; k < quantidadeNeuroniosSaida; k++) {
                pesosOcultaSaida1[j][k] += taxaAprendizagem * erroSaida[k] * camadaOculta[j];
            }
        }

        for (int k = 0; k < quantidadeNeuroniosSaida; k++) {
            biasSaida[k] += taxaAprendizagem * erroSaida[k];
        }
    }

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void ErroAcao(int acao){
        erroAcao = acao;
    }

    public int getErroAcao() {
        return erroAcao;
    }

    public void registrarAcerto() {
        acertos++;
        totalAcoes++;
    }

    public void registrarAcao() {
        totalAcoes++;
    }

    public void incrementarTempoSobrevivencia() {
        tempoSobrevivencia++;
    }

    public double getTaxaAcerto() {
        if (totalAcoes == 0) return 0.0;
        return (double) acertos / totalAcoes;
    }

    public int getTempoSobrevivencia() {
        return tempoSobrevivencia;
    }

    public double calcularPreFitness() {
        // PreFitness = (Pontuação * 0.5) + (Taxa de Acerto * 0.3)
        return (pontuacao * 0.5) + (getTaxaAcerto() * 0.3);
    }

    public double calcularFitness(int tempoMaximo) {
        double preFitness = calcularPreFitness();

        // Penalidade por eliminação baseada no tempo de sobrevivência
        double penalidadeEliminacao = 0.5 * (1.0 - (double) tempoSobrevivencia / tempoMaximo);

        // Fitness = PreFitness * (1 - Penalidade por Eliminação)
        return preFitness * (1.0 - penalidadeEliminacao);
    }

    public void aplicarMutacaoPopulacional(List<RedeNeuralTeste2> populacao) {
        Random random = new Random();

        // Aplica mutação populacional em 50% dos indivíduos
        for (RedeNeuralTeste2 rede : populacao) {
            if (random.nextDouble() < taxaMutacaoPopulacional) {
                // Aplica mutação individual de 30% em cada peso
                for (int i = 0; i < quantidadeNeuroniosEntrada; i++) {
                    for (int j = 0; j < quantidadeNeuroniosCamadaOculta1; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta1[i][j] += variacao;
                    }
                }

                // Mutação nos bias da primeira camada
                for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta1[i] += variacao;
                }

                // Mutação nos pesos da segunda camada
                for (int i = 0; i < quantidadeNeuroniosCamadaOculta1; i++) {
                    for (int j = 0; j < quantidadeNeuroniosCamadaOculta2; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta2[i][j] += variacao;
                    }
                }

                // Mutação nos bias da segunda camada
                for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta2[i] += variacao;
                }

                // Mutação nos pesos da camada de saída
                for (int i = 0; i < quantidadeNeuroniosCamadaOculta2; i++) {
                    for (int j = 0; j < quantidadeNeuroniosSaida; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosOcultaSaida2[i][j] += variacao;
                    }
                }
                // Mutação nos bias da camada de saída
                for (int i = 0; i < quantidadeNeuroniosSaida; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasSaida[i] += variacao;
                }
            }
        }
    }

}