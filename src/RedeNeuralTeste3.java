import java.util.*;

public class RedeNeuralTeste3 {
    private int numEntradas;
    private int numSaidas;
    private int[] numOcultos;
    private List<double[][]> pesos;
    private List<double[]> bias;
    private double[] dna;
    private static final Random random = new Random();
    public double taxaMutacao = 0.1;

    // Novos campos para fitness, pontuação, taxa de acerto/erro, desafios e acertos/erros por tipo
    private double fitness;
    private int pontuacao;
    private double taxaDeAcerto;
    private double taxaDeErro;
    private int desafioTerrestre;
    private int desafioVoador;
    private int desafioMeteoro;
    // Controle individual de acerto da última ação
    private boolean acertouUltimaAcao = false;
    // Cronômetro individual de cada dinossauro
    private int cronometroIndividual = 0;

    public RedeNeuralTeste3(int numEntradas, int numSaidas, int... numOcultos) {
        // Validação básica
        if (numOcultos.length == 0) {
            throw new IllegalArgumentException("Deve ter pelo menos uma camada oculta");
        }
        if (numEntradas <= 0 || numSaidas <= 0) {
            throw new IllegalArgumentException("Número de entradas e saídas deve ser maior que zero");
        }
        for (int i = 0; i < numOcultos.length; i++) {
            if (numOcultos[i] <= 0) {
                throw new IllegalArgumentException("Tamanho das camadas ocultas deve ser maior que zero");
            }
        }

        this.numEntradas = numEntradas;
        this.numSaidas = numSaidas;
        this.numOcultos = numOcultos;
        this.fitness = 0.0;
        this.pontuacao = 0;
        this.taxaDeAcerto = 0.0;
        this.taxaDeErro = 0.0;
        this.desafioTerrestre = 0;
        this.desafioVoador = 0;
        this.desafioMeteoro = 0;

        inicializarEstruturas();
        inicializarPesosAleatorios();
        atualizarDNA();
    }

    private void inicializarEstruturas() {
        pesos = new ArrayList<>();
        bias = new ArrayList<>();

        int camadaAnterior = numEntradas;
        for (int camada : numOcultos) {
            pesos.add(new double[camadaAnterior][camada]);
            bias.add(new double[camada]);
            camadaAnterior = camada;
        }

        pesos.add(new double[camadaAnterior][numSaidas]);
        bias.add(new double[numSaidas]);
    }

    private void inicializarPesosAleatorios() {
        for (int l = 0; l < pesos.size(); l++) {
            double[][] matriz = pesos.get(l);
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[0].length; j++) {
                    // Usar range [-1000.0, +1000.0] como o projeto original
                    matriz[i][j] = (random.nextInt(20001) / 10.0) - 1000.0;
                }
            }
            double[] vetor = bias.get(l);
            for (int i = 0; i < vetor.length; i++) {
                // Usar range [-1000.0, +1000.0] como o projeto original
                vetor[i] = (random.nextInt(20001) / 10.0) - 1000.0;
            }
        }
    }

    public double[] calcularSaida(double[] entradas) {
        double[] ativacao = entradas;
        for (int camada = 0; camada < pesos.size(); camada++) {
            double[] novaAtivacao = new double[bias.get(camada).length];
            for (int i = 0; i < novaAtivacao.length; i++) {
                double soma = bias.get(camada)[i];
                for (int j = 0; j < ativacao.length; j++) {
                    soma += ativacao[j] * pesos.get(camada)[j][i];
                }
                novaAtivacao[i] = relu(soma);
            }
            ativacao = novaAtivacao;
        }
        return ativacao;
    }

    private double relu(double x) {
        return Math.max(0, x);
    }

    public double[] getDNAAsVector() {
        return dna;
    }

    public void setDNAFromVector(double[] novoDNA) {
        this.dna = novoDNA.clone();
        int index = 0;

        for (int l = 0; l < pesos.size(); l++) {
            double[][] w = pesos.get(l);
            for (int i = 0; i < w.length; i++) {
                for (int j = 0; j < w[0].length; j++) {
                    w[i][j] = novoDNA[index++];
                }
            }
            double[] b = bias.get(l);
            for (int i = 0; i < b.length; i++) {
                b[i] = novoDNA[index++];
            }
        }
    }

    public void aplicarMutacao() {
        for (int i = 0; i < dna.length; i++) {
            if (random.nextDouble() < taxaMutacao) {
                dna[i] += random.nextGaussian() * 0.1;
            }
        }
        setDNAFromVector(dna);
    }

    public static RedeNeuralTeste3 crossover(RedeNeuralTeste3 p1, RedeNeuralTeste3 p2) {
        double[] dna1 = p1.getDNAAsVector();
        double[] dna2 = p2.getDNAAsVector();

        double[] novoDNA = new double[dna1.length];
        for (int i = 0; i < dna1.length; i++) {
            novoDNA[i] = (dna1[i] + dna2[i]) / 2;
        }

        RedeNeuralTeste3 filho = new RedeNeuralTeste3(p1.numEntradas, p1.numSaidas, p1.numOcultos);
        filho.setDNAFromVector(novoDNA);
        return filho;
    }

    private void atualizarDNA() {
        List<Double> lista = new ArrayList<>();
        for (int l = 0; l < pesos.size(); l++) {
            double[][] w = pesos.get(l);
            for (double[] linha : w) for (double val : linha) lista.add(val);
            for (double val : bias.get(l)) lista.add(val);
        }
        dna = new double[lista.size()];
        for (int i = 0; i < lista.size(); i++) dna[i] = lista.get(i);
    }

    public void copiarPesos(RedeNeuralTeste3 outro) {
        setDNAFromVector(outro.getDNAAsVector());
    }

    // Métodos de fitness, pontuação, taxa de acerto e erro
    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    // Métodos para taxa de acerto e erro
    public void incrementarAcerto() {
        taxaDeAcerto++;
    }
    public void incrementarErro() {
        taxaDeErro++;
    }
    public double getTaxaDeAcerto() {
        return taxaDeAcerto;
    }
    public double getTaxaDeErro() {
        return taxaDeErro;
    }
    public void resetarTaxas() {
        taxaDeAcerto = 0.0;
        taxaDeErro = 0.0;
        fitness = 0.0;
        pontuacao = 0;
    }

    // Métodos para desafios
    public int getDesafioTerrestre() { return desafioTerrestre; }
    public int getDesafioVoador() { return desafioVoador; }
    public int getDesafioMeteoro() { return desafioMeteoro; }
    public void marcarDesafioTerrestre() { desafioTerrestre = 1; }
    public void marcarDesafioVoador() { desafioVoador = 1; }
    public void marcarDesafioMeteoro() { desafioMeteoro = 1; }
    public void resetarDesafios() {
        desafioTerrestre = 0;
        desafioVoador = 0;
        desafioMeteoro = 0;
    }

    // Métodos para obter informações da arquitetura
    public int getNumEntradas() {
        return numEntradas;
    }

    public int getNumSaidas() {
        return numSaidas;
    }

    public int[] getNumOcultos() {
        return numOcultos.clone();
    }

    public int getTamanhoDNA() {
        return dna.length;
    }

    // Métodos adicionais necessários para compatibilidade
    public int getNumEntradasNeuronios() {
        return numEntradas;
    }

    public int getNumOcultos1Neuronios() {
        return numOcultos.length > 0 ? numOcultos[0] : 0;
    }

    public int getNumOcultos2Neuronios() {
        return numOcultos.length > 1 ? numOcultos[1] : 0;
    }

    public int getNumSaidasNeuronios() {
        return numSaidas;
    }

    public void copiarPesos2(RedeNeuralTeste3 outro) {
        copiarPesos(outro);
    }

    public RedeNeuralTeste3 clonar() {
        RedeNeuralTeste3 clone = new RedeNeuralTeste3(numEntradas, numSaidas, numOcultos);
        clone.copiarPesos(this);
        // Não copia fitness nem pontuação - cada clone deve começar do zero
        clone.taxaDeAcerto = this.taxaDeAcerto;
        clone.taxaDeErro = this.taxaDeErro;
        clone.desafioTerrestre = this.desafioTerrestre;
        clone.desafioVoador = this.desafioVoador;
        clone.desafioMeteoro = this.desafioMeteoro;
        return clone;
    }

    public void aplicarMutacaoPopulacional(List<RedeNeuralTeste3> populacao) {
        for (RedeNeuralTeste3 rede : populacao) {
            rede.aplicarMutacao();
        }
    }

    public void aplicarCrossoverComMelhor(RedeNeuralTeste3 melhor, List<RedeNeuralTeste3> populacao) {
        for (int i = 0; i < populacao.size(); i++) {
            RedeNeuralTeste3 filho = crossover(melhor, populacao.get(i));
            populacao.set(i, filho);
        }
    }

    // Métodos para acertouUltimaAcao
    public void setAcertouUltimaAcao(boolean valor) { acertouUltimaAcao = valor; }
    public boolean getAcertouUltimaAcao() { return acertouUltimaAcao; }

    // Métodos para cronômetro individual
    public int getCronometroIndividual() { return cronometroIndividual; }
    public void setCronometroIndividual(int cronometro) { cronometroIndividual = cronometro; }
    public void incrementarCronometro() { cronometroIndividual++; }
    public void resetarCronometro() { cronometroIndividual = 0; }
}