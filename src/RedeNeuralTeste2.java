import java.io.*;
import java.util.Random;
import java.util.List;

public class RedeNeuralTeste2 {
    private int numEntradasNeuronios, numOcultos1Neuronios, numOcultos2Neuronios, numSaidasNeuronios;
    private double taxaMutacaoPopulacional = 0.5; // 50% de chance de mutação na população
    private double taxaMutacaoIndividual = 0.1; // 30% de diferença entre indivíduos
    private static final Random globalRandom = new Random();

    private double[][] pesosEntradaOculta1; // Pesos da camada de entrada para a camada oculta
    private double[][] pesosEntradaOculta2;
    private double[] biasOculta1; // Bias para a camada oculta
    private double[] biasOculta2;
    private double[][] pesosOcultaSaida1; // Pesos da camada oculta para a camada de saída
    private double[][] pesosOcultaSaida2;
    private double[] biasSaida; // Bias para a camada de saída
    private Random random;

    private int pontuacao;
    private double fitness; // Novo campo para armazenar o fitness

    public RedeNeuralTeste2(int numEntradasNeuronios, int numOcultos1Neuronios, int numSaidasNeuronios) {
        this.numEntradasNeuronios = numEntradasNeuronios;
        this.numOcultos1Neuronios = numOcultos1Neuronios;
        this.numSaidasNeuronios = numSaidasNeuronios;

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta1 = new double[numEntradasNeuronios][numOcultos1Neuronios];
        pesosOcultaSaida1 = new double[numOcultos1Neuronios][numSaidasNeuronios];
        biasOculta1 = new double[numOcultos1Neuronios];
        biasSaida = new double[numSaidasNeuronios];

        inicializarPesos();
    }

    private void inicializarPesos() {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                // pesosEntradaOculta[i][j] = globalRandom.nextDouble() * 1.0 - 0.5; // [-0.5,
                // 0.5]
                pesosEntradaOculta1[i][j] = globalRandom.nextDouble() * 2.0 - 1.0; // [-1, 1]
                // System.out.println("pesosEntradas: "+pesosEntradaOculta[i][j]);
            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida1[i][j] = globalRandom.nextDouble() * 2.0 - 1.0; // [-1, 1]
                // System.out.println("pesosOcultaSaida: "+pesosOcultaSaida[i][j]);

            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            biasOculta1[i] = globalRandom.nextDouble() * 2.0 - 1.0; // [-1, 1]
            // System.out.println("biasOculta: "+biasOculta[i]);

        }
        for (int i = 0; i < numSaidasNeuronios; i++) {
            biasSaida[i] = globalRandom.nextDouble() * 2.0 - 1.0; // [-1, 1]
            // System.out.println("biasSaida: "+biasSaida[i]);

        }
    }

    public double[] calcularSaida(double[] entradas) {
        double[] saidaOculta = new double[numOcultos1Neuronios];
        double[] saidaFinal = new double[numSaidasNeuronios];

        // Cálculo da camada oculta
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            // System.out.println("Cálculo da camada oculta soma antes: "+soma);
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
                // System.out.println("Cálculo da camada oculta soma depois: "+soma);
            }
            saidaOculta[i] = tanh(soma);
            // System.out.println("Cálculo da camada oculta relu: "+saidaOculta[i]);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            // System.out.println("Cálculo da camada de saída soma antes: "+soma);
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida1[j][i];
                // System.out.println("Cálculo da camada de saída soma depois: "+soma);
            }
            saidaFinal[i] = sigmoid(soma);
            // System.out.println("Cálculo da camada oculta sigmoid: "+saidaFinal[i]);

        }
        return saidaFinal;
    }

    public void copiarPesos(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, numOcultos1Neuronios);
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida1[i], 0, this.pesosOcultaSaida1[i], 0, numSaidasNeuronios);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, numOcultos1Neuronios);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidasNeuronios);
    }

    public void ajustarPesosPorCondicao(double[] entradas, double fator) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ------------------------------------Rede Neural com duas Camadas
    public RedeNeuralTeste2(int numEntradasNeuronios, int numOcultos1Neuronios, int numOcultos2Neuronios,
                            int numSaidasNeuronios) {
        this.numEntradasNeuronios = numEntradasNeuronios;
        this.numOcultos1Neuronios = numOcultos1Neuronios;
        this.numOcultos2Neuronios = numOcultos2Neuronios;
        this.numSaidasNeuronios = numSaidasNeuronios;

        pesosEntradaOculta1 = new double[numEntradasNeuronios][numOcultos1Neuronios];
        biasOculta1 = new double[numOcultos1Neuronios];
        pesosEntradaOculta2 = new double[numOcultos1Neuronios][numOcultos2Neuronios];
        biasOculta2 = new double[numOcultos2Neuronios];
        pesosOcultaSaida1 = new double[numOcultos1Neuronios][numSaidasNeuronios];
        pesosOcultaSaida2 = new double[numOcultos2Neuronios][numSaidasNeuronios];
        biasSaida = new double[numSaidasNeuronios];

        inicializarPesos2();
        //carregarPesosFixos(); // Substitui inicializarPesos2()

    }

    private void inicializarPesos2() {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] = globalRandom.nextDouble() * 2.0 - 1.0;
            }
        }

        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida1[i][j] = globalRandom.nextDouble() * 2.0 - 1.0;
            }
        }

        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                pesosEntradaOculta2[i][j] = globalRandom.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida2[i][j] = globalRandom.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            biasOculta1[i] = globalRandom.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            biasOculta2[i] = globalRandom.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numSaidasNeuronios; i++) {
            biasSaida[i] = globalRandom.nextDouble() * 2.0 - 1.0;
        }
    }

    public double[] calcularSaida2(double[] entradas) {
        double[] entradasNormalizadas = normalizarEntradas(entradas);

        double[] saidaOculta1 = new double[numOcultos1Neuronios];
        double[] saidaOculta2 = new double[numOcultos2Neuronios];
        double[] saidaFinal = new double[numSaidasNeuronios];

        // Camada oculta 1
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradasNormalizadas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
        }

        // Camada oculta 2
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
        }

        // Camada de saída (usa saída da camada oculta 2 + atalho da oculta 1)
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];

            // Contribuição da camada oculta 2
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }

            // Contribuição direta da camada oculta 1 (skip connection)
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta1[j] * pesosOcultaSaida1[j][i];
            }

            saidaFinal[i] = sigmoid(soma);
        }

        return saidaFinal;
    }


    private double[] normalizarEntradas(double[] entradas) {
        double[] norm = new double[entradas.length];

        norm[0] = entradas[0] / 600.0; // x Player
        norm[1] = entradas[1] / 400.0; // y Player
        norm[2] = entradas[2] / 600.0; // x Inimigo
        norm[3] = entradas[3] / 400.0; // y Inimigo
        norm[4] = entradas[4] / 200.0; // altura Inimigo
        norm[5] = entradas[5] / 200.0; // largura Inimigo
        norm[6] = entradas[6] / 10.0; // velocidade

        return norm;
    }

    public void copiarPesos2(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, numOcultos1Neuronios);
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta2[i], 0, this.pesosEntradaOculta2[i], 0, numOcultos2Neuronios);
        }
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida2[i], 0, this.pesosOcultaSaida2[i], 0, numSaidasNeuronios);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, numOcultos1Neuronios);
        System.arraycopy(outraRede.biasOculta2, 0, this.biasOculta2, 0, numOcultos2Neuronios);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidasNeuronios);
    }

    // Função de ativação ReLU
    private double relu(double x) {
        return (Math.max(0, x));
    }

    // Função de ativação Sigmóide
    private double sigmoid(double x) {
        return (1 / (1 + Math.exp(-x)));
    }

    // Função de ativação Tangente Hiperbólica
    private double tanh(double x) {
        return (Math.tanh(x));
    }

    // Derivada da função de ativação Tangente Hiperbólica
    private double tanhDerivada(double x) {
        double th = Math.tanh(x);
        return (1 - (th * th)); // 1 - tanh^2(x)
    }

    public void treinar(double[] entradas, double[] saidasEsperadas, double taxaAprendizagem) {
        // Forward pass
        double[] camadaOculta = new double[numOcultos1Neuronios];
        for (int j = 0; j < numOcultos1Neuronios; j++) {
            camadaOculta[j] = biasOculta1[j];
            for (int i = 0; i < numEntradasNeuronios; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta1[i][j];
            }
            camadaOculta[j] = tanh(camadaOculta[j]); // Mudamos para tanh;
        }

        double[] saidas = new double[numSaidasNeuronios];
        for (int j = 0; j < numSaidasNeuronios; j++) {
            saidas[j] = biasSaida[j];
            for (int i = 0; i < numOcultos1Neuronios; i++) {
                saidas[j] += camadaOculta[i] * pesosOcultaSaida1[i][j];
            }
            saidas[j] = sigmoid(saidas[j]);
        }

        // Backward pass (calcular erro e atualizar pesos)
        double[] erroSaida = new double[numSaidasNeuronios];
        for (int j = 0; j < numSaidasNeuronios; j++) {
            erroSaida[j] = saidasEsperadas[j] - saidas[j];
        }

        double[] erroOculto = new double[numOcultos1Neuronios];
        for (int j = 0; j < numOcultos1Neuronios; j++) {
            for (int k = 0; k < numSaidasNeuronios; k++) {
                erroOculto[j] += erroSaida[k] * pesosOcultaSaida1[j][k];
            }
            // erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
            erroOculto[j] *= tanhDerivada(camadaOculta[j]); // Usando derivada do tanh
        }

        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] += taxaAprendizagem * erroOculto[j] * entradas[i];
            }
        }

        for (int j = 0; j < numOcultos1Neuronios; j++) {
            biasOculta1[j] += taxaAprendizagem * erroOculto[j];
        }

        for (int j = 0; j < numOcultos1Neuronios; j++) {
            for (int k = 0; k < numSaidasNeuronios; k++) {
                pesosOcultaSaida1[j][k] += taxaAprendizagem * erroSaida[k] * camadaOculta[j];
            }
        }

        for (int k = 0; k < numSaidasNeuronios; k++) {
            biasSaida[k] += taxaAprendizagem * erroSaida[k];
        }
    }

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        return "Rede Neural | Pontuação: " + pontuacao + " | Fitness: " + fitness;
    }

    public void aplicarMutacaoPopulacional(List<RedeNeuralTeste2> populacao) {

        // Aplica mutação populacional em 50% dos indivíduos
        for (RedeNeuralTeste2 rede : populacao) {
            if (globalRandom.nextDouble() < taxaMutacaoPopulacional) {
                // Aplica mutação individual de 30% em cada peso
                for (int i = 0; i < numEntradasNeuronios; i++) {
                    for (int j = 0; j < numOcultos1Neuronios; j++) {
                        double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta1[i][j] += variacao;
                    }
                }

                // Mutação nos bias da primeira camada
                for (int i = 0; i < numOcultos1Neuronios; i++) {
                    double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta1[i] += variacao;
                }

                // Mutação nos pesos da segunda camada
                for (int i = 0; i < numOcultos1Neuronios; i++) {
                    for (int j = 0; j < numOcultos2Neuronios; j++) {
                        double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta2[i][j] += variacao;
                    }
                }

                // Mutação nos bias da segunda camada
                for (int i = 0; i < numOcultos2Neuronios; i++) {
                    double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta2[i] += variacao;
                }

                // Mutação nos pesos da camada de saída
                for (int i = 0; i < numOcultos2Neuronios; i++) {
                    for (int j = 0; j < numSaidasNeuronios; j++) {
                        double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosOcultaSaida2[i][j] += variacao;
                    }
                }

                // Mutação nos bias da camada de saída
                for (int i = 0; i < numSaidasNeuronios; i++) {
                    double variacao = globalRandom.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasSaida[i] += variacao;
                }
            }
        }
    }

    public void aplicarCrossoverComMelhor(RedeNeuralTeste2 melhor, List<RedeNeuralTeste2> populacao) {
        for (int i = 0; i < populacao.size(); i++) {
            RedeNeuralTeste2 filho = populacao.get(i);

            // Crossover: Pesos da entrada para a camada oculta 1
            for (int j = 0; j < numEntradasNeuronios; j++) {
                for (int k = 0; k < numOcultos1Neuronios; k++) {
                    filho.pesosEntradaOculta1[j][k] = (melhor.pesosEntradaOculta1[j][k]
                            + filho.pesosEntradaOculta1[j][k]) / 2.0;
                }
            }

            // Crossover: Bias da camada oculta 1
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                filho.biasOculta1[j] = (melhor.biasOculta1[j] + filho.biasOculta1[j]) / 2.0;
            }

            // Se a rede tem duas camadas ocultas, aplica crossover também na segunda camada
            if (melhor.pesosEntradaOculta2 != null && filho.pesosEntradaOculta2 != null) {
                // Crossover: Pesos da camada oculta 1 para oculta 2
                for (int j = 0; j < numOcultos1Neuronios; j++) {
                    for (int k = 0; k < numOcultos2Neuronios; k++) {
                        filho.pesosEntradaOculta2[j][k] = (melhor.pesosEntradaOculta2[j][k]
                                + filho.pesosEntradaOculta2[j][k]) / 2.0;
                    }
                }

                // Crossover: Bias da camada oculta 2
                for (int j = 0; j < numOcultos2Neuronios; j++) {
                    filho.biasOculta2[j] = (melhor.biasOculta2[j] + filho.biasOculta2[j]) / 2.0;
                }

                // Crossover: Pesos da camada oculta 2 para saída
                for (int j = 0; j < numOcultos2Neuronios; j++) {
                    for (int k = 0; k < numSaidasNeuronios; k++) {
                        filho.pesosOcultaSaida2[j][k] = (melhor.pesosOcultaSaida2[j][k] + filho.pesosOcultaSaida2[j][k])
                                / 2.0;
                    }
                }
            } else {
                // Caso tenha só uma camada oculta: Pesos da camada oculta 1 para saída
                for (int j = 0; j < numOcultos1Neuronios; j++) {
                    for (int k = 0; k < numSaidasNeuronios; k++) {
                        filho.pesosOcultaSaida1[j][k] = (melhor.pesosOcultaSaida1[j][k] + filho.pesosOcultaSaida1[j][k])
                                / 2.0;
                    }
                }
            }

            // Crossover: Bias da camada de saída (em ambos os casos)
            for (int j = 0; j < numSaidasNeuronios; j++) {
                filho.biasSaida[j] = (melhor.biasSaida[j] + filho.biasSaida[j]) / 2.0;
            }
        }
    }

    public void salvarParaArquivo(String caminho) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(this);
        }
    }

    public static RedeNeuralTeste2 carregarDeArquivo(String caminho) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            return (RedeNeuralTeste2) ois.readObject();
        }
    }

    // Getters
    public int getNumEntradasNeuronios() {
        return numEntradasNeuronios;
    }

    public int getNumOcultos1Neuronios() {
        return numOcultos1Neuronios;
    }

    public int getNumOcultos2Neuronios() {
        return numOcultos2Neuronios;
    }

    public int getNumSaidasNeuronios() {
        return numSaidasNeuronios;
    }

    public double[][] getPesosEntradaOculta1() {
        return pesosEntradaOculta1;
    }

    public double[][] getPesosEntradaOculta2() {
        return pesosEntradaOculta2;
    }

    public double[] getBiasOculta1() {
        return biasOculta1;
    }

    public double[] getBiasOculta2() {
        return biasOculta2;
    }

    public double[][] getPesosOcultaSaida1() {
        return pesosOcultaSaida1;
    }

    public double[][] getPesosOcultaSaida2() {
        return pesosOcultaSaida2;
    }

    public double[] getBiasSaida() {
        return biasSaida;
    }

    public void imprimirTodos() {
        System.out.println("numEntradasNeuronios: " + numEntradasNeuronios);
        System.out.println("numOcultos1Neuronios: " + numOcultos1Neuronios);
        System.out.println("numOcultos2Neuronios: " + numOcultos2Neuronios);
        System.out.println("numSaidasNeuronios: " + numSaidasNeuronios);

        System.out.println("pesosEntradaOculta1:");
        imprimirMatriz(pesosEntradaOculta1);

        System.out.println("pesosEntradaOculta2:");
        imprimirMatriz(pesosEntradaOculta2);

        System.out.println("biasOculta1:");
        imprimirVetor(biasOculta1);

        System.out.println("biasOculta2:");
        imprimirVetor(biasOculta2);

        System.out.println("pesosOcultaSaida1:");
        imprimirMatriz(pesosOcultaSaida1);

        System.out.println("pesosOcultaSaida2:");
        imprimirMatriz(pesosOcultaSaida2);

        System.out.println("biasSaida:");
        imprimirVetor(biasSaida);
    }

    // Métodos auxiliares para imprimir vetores e matrizes
    private void imprimirVetor(double[] vetor) {
        if (vetor != null) {
            for (double v : vetor) {
                System.out.print(v + " ");
            }
            System.out.println();
        } else {
            System.out.println("null");
        }
    }

    private void imprimirMatriz(double[][] matriz) {
        if (matriz != null) {
            for (double[] linha : matriz) {
                for (double v : linha) {
                    System.out.print(v + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("null");
        }
    }

    private void carregarPesosFixos() {
        pesosEntradaOculta1 = new double[][] {
                {-0.09112705381093217, 0.2492120675558795, -0.32922900504206404, -0.3294442345383924, 0.09333465573612199, -0.4731257215306076, 0.20070025023694962, 0.39891576644300597, 0.6386795374571634, 0.38979957733370024, 0.5569861806004902, 0.27789803521260087, -0.33530010365512125, 0.10132263079095491},
                {0.156818817525064, -0.3755937076684291, -0.4097736402084804, 0.27869451983059595, -0.21308925590005245, -0.5190354670015401, -0.5817629498365651, -0.3277337443689672, 0.48101917678333295, 0.09775010799994294, 0.8408164092105861, -0.24471321807345917, -0.10186540169179414, 0.08792225038024473},
                {0.3000691784634556, 0.16214034668780664, 0.3572002267881391, 0.20156529483823188, -0.15231949775883036, 0.5425257339855482, 0.34522669771435655, -0.12440510679013109, 0.19708328644124531, -0.17054303075503313, -0.21931110069525478, -0.3808933826795464, 0.2916763854257214, -0.5443759384792715},
                {0.21735636348809506, -0.15460755172279597, -0.24689568803495385, -0.01784804352260294, -0.019455558518863714, 0.005632259811053912, 0.5868213797643428, -0.297640542232984, -0.48584201574420016, -0.3633079242223962, 0.33617558920494667, 0.6709190518570192, -0.26255094948625063, -0.6554325749643248},
                {0.4987860676953297, -0.04279417949789785, 0.4347598557185658, 0.19229249016262212, -0.2255145322098508, -0.6966425539653662, 0.5900862583203312, 0.3594211689637151, -0.2675549908902127, 0.14806414615700106, 0.7343229320687019, 0.2995441566975098, -0.45509260743127156, 0.30785174735408183},
                {-0.4312484428830038, -0.278057728928254, -0.09190863417689143, 0.5830280647724548, -0.33553783343482346, 0.3358640253272477, -0.495132375126297, 0.1864299139964947, 0.4571324515840708, 0.008376901574756415, -0.42403318575193294, -0.5309721493943756, -0.044474158126133534, 0.2067223701575953},
                {0.5532908243366483, 0.2950690496301709, -0.3490863601442369, -0.16780901819643382, 0.455742070434552, -0.4182077232034579, -0.07705826351716984, 0.7054919128464896, -0.3590290524924519, -0.4100461430812091, -0.01565412685390949, 0.49010953245092503, 0.24455868474493989, 0.2989422762246309}
        };

        pesosEntradaOculta2 = new double[][] {
                {-0.3954983070597937, 0.4309633968809345, -0.005109288937925262, 0.19298477121960245, 0.09038150757041358, 1.0595297659489218, 0.016698456650777932, -0.026951367089196487, -0.3108815917538529, 0.0796299446099944, 0.1282959417239434, 0.23585890941620077, -0.2323469097922723, 0.2969567773469785},
                {-0.3947584537256773, -0.49793606344447694, -0.531091150790365, -0.08827551348902651, -0.28307459028744425, 0.30907765787725966, -0.3935903690260796, -0.20537359145976192, -0.6556510909207485, -0.7251487512555561, 0.012806233971363323, -0.11598671610095405, -0.16366601953134732, -0.24059976157119117},
                {0.10256368493881085, 0.05965629128670838, 0.2421976631048978, 0.2922351758343442, 0.41643327678411357, 0.07149965873090894, -0.018749358233111343, -0.9025739949806454, 0.46783529200619944, -0.19460459936617452, 0.01769927632458615, -0.6800566140844901, -0.4556665641754664, -0.09965641820362506},
                {-0.46565340570475194, 0.3111162625439449, -0.0108469076517822, -0.4598722975307714, -0.05596355016387283, -0.09537414501338792, -0.07133025144436819, -0.5132196041580763, 0.1884562773487239, 0.3627163254035261, -0.35802466172322733, 0.315241997666702, 0.28023482648429415, 0.104434164664933},
                {-1.161047195858266, 0.41034031570841534, 0.6903530590664939, 0.2028746401943176, -0.117440252128924, 0.04249093570102208, -0.4107773534154257, 0.07370226786830616, 0.5556972065353927, 0.2820623733805389, -0.0297695274322526, 0.6786847625794015, -0.2838571287711201, 0.4958966872618353},
                {-0.39516779240435773, 0.3991655709180597, 0.5822199884797452, -0.09417656961094353, 0.5651784213978571, 0.47631839297215733, -0.4056017739530445, 0.051711168835678195, 0.14494799527267238, -0.497527885636602, 0.12972917584935342, -0.08718920458280494, -0.9578867318405826, 0.4416920161381285},
                {-0.33779470599643396, 0.13642630700528022, -0.005734648382685531, 0.7171669176813513, -0.2542522573254344, 0.5012158670714881, -0.5614921260695567, -0.5084795186928126, -0.048260843358227085, 0.41700657549734405, -0.09552664202082038, 0.2167848570881089, 0.6821186189432897, -0.463134875830309},
                {0.49266631093256197, -0.6216940266770293, -0.007877664020684669, -0.23444996913258578, 0.4401879244141682, -0.42915209303995483, 0.5336107753788512, -0.4100849055385733, 0.0054733265502724116, -0.4048750704905593, 0.13072163539791692, -0.16937486811995572, 0.31539408942344227, -0.4585918519886254},
                {0.06004508460874682, 0.1568082682830324, -0.4521195830267731, -0.22468056129755581, 0.34950157171434126, 0.1554397713985336, 0.6734218593652793, -0.48251021887653855, 0.038393905107756476, 0.4179750195662642, -0.19548957824523644, 0.3444700795654797, -0.29626659733107275, 0.3725616226760396},
                {0.28039540590269824, 0.4873092244064031, -0.41871238227943436, 0.523012884658782, -0.432036504539074, -0.30372673201272304, -0.07705725645964831, -0.12669101990581427, -0.060646572478096905, -0.08230513288234281, -0.4601673699864449, -0.21314219569616977, -0.6381008231837602, -0.449471016712811},
                {0.27694391527387996, -0.8355821843972184, 0.7732080400115702, 0.25091226511270454, 0.300735484939447, 0.2725205695652253, -0.9175601286469409, -0.8325081360566181, -0.1818512123799692, 0.1252020095061382, 0.3992082272472048, -0.29979160134106836, -0.15894901120884797, -0.23433915579153158},
                {0.06094481844711841, 0.12846220899345195, 0.34231874178039734, 0.30594532096165505, 0.5203481348385579, -0.026695239783918752, 0.21416007306284326, 0.6587841337428512, 0.07504526750011761, 0.3444653350828314, 0.004583244532607639, 0.4842262267882806, 0.06949926279744137, 5.9227644128379E-4},
                {-0.10698025456263927, 0.3296727114618572, -0.36546074894446745, 0.15736294091605196, -0.06516521587547641, -0.6361875860973005, 0.14839543752909173, 0.28072295571092476, 0.16722061989132841, -0.274830507863001, 0.028959123551210608, 0.8405804176175308, 0.4913062254307109, 0.3602084416136669},
                {0.32762010620773957, 0.5043116296803611, -0.3376781662006072, -0.7441903208302852, -0.6297487413494143, -0.3301948289055564, -0.35310003023328596, 0.019928008447439263, -0.35559335351940635, -0.26622175092550227, -0.08763537341957772, 0.15798920315535514, -0.48271211573373085, -0.30657207207923987}
        };

        biasOculta1 = new double[] {
                0.252173774984518, 0.2579568301531664, 0.22274987343525637, -0.11629641362309256, 0.47683803753955545, 0.4689115137271923, 0.5283116525327317, -0.28399255190701955, -0.06120827975488201, 0.25886681235626213, -0.9235182580411794, -0.3702538739069263, 0.3687894925428378, 1.2732416529212558
        };

        biasOculta2 = new double[] {
                0.07875519133053571 ,-0.007147160543324579 ,-0.03259184873468507 ,0.606360897757942 ,-0.45479822283438665 ,-0.12429222982144952 ,0.17429265914143172 ,-0.5123230042119398 ,0.2399345934340023 ,-0.5553525986064054 ,-0.44126042688777445 ,0.5124654876914876 ,0.08057059834860227 ,-0.2874793804798205
        };

        pesosOcultaSaida1 = new double[][] {
                {-0.5237722410703678, -0.5126454547599704},
                {-0.5341127267262802, 0.8101706440322485},
                {0.6569760302556031, 0.8872388045500714},
                {-0.9470275588404737, -0.8666491731057884},
                {-0.6523242545918493, 0.0071121297509502135},
                {-0.5713482419165592, -0.5759533681955673},
                {0.0009136780452851934, -0.7158985232031816},
                {-0.7260574461321221, -0.5135761388311391},
                {0.41559591381723693, 0.38924974271722523},
                {-0.5823151278949237, 0.052757995826649084},
                {0.751343629203552, -0.03274789579690762},
                {0.6083541232419891, -0.961257695412292},
                {-0.6988542424970863, 0.13086286876952524},
                {0.48434638983853984, -0.27072012753775865}
        };

        pesosOcultaSaida2 = new double[][] {
                {-0.08239699742658346, -0.27076322820712095},
                {0.32587664931310056, -0.05373543767561856},
                {-0.26322117945456974, 0.3164801938490469},
                {0.31848953180261463, 0.25270998920890203},
                {-0.11639082212804613, 0.42480129620587426},
                {-0.04288720582445073, -0.4764101073116923},
                {-0.10651839435274948, 0.675576542898767},
                {-0.37665406000783075, 0.5125788344523086},
                {0.26173521475253875, -0.06314565258779986},
                {0.009994020528011655, -0.26941131725663126},
                {0.42189735106464515, 0.3626589206016309},
                {-0.3298388823563616, 0.06013133248949057},
                {0.24633876038283517, -0.3348755157907569},
                {0.5645190727272862, -0.039981553740698235}
        };


        biasSaida = new double[] {
                0.4354275440472779, -0.041560663850673196
        };
    }

}