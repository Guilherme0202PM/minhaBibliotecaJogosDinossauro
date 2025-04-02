import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RedeNeuralTeste2 {
    private int numEntradasNeuronios, numOcultos1Neuronios, numOcultos2Neuronios,  numSaidasNeuronios;

    private double[][] pesosEntradaOculta1; // Pesos da camada de entrada para a camada oculta
    private double[][] pesosEntradaOculta2;
    private double[] biasOculta1; // Bias para a camada oculta
    private double[] biasOculta2;
    private double[][] pesosOcultaSaida1; // Pesos da camada oculta para a camada de saída
    private double[][] pesosOcultaSaida2;
    private double[] biasSaida; // Bias para a camada de saída
    private double [] entradas;
    private Random random;

    private int pontuacao;
    private int sobrevivencia; // Tempo de sobrevivência
    private int acertos; // Número de decisões corretas
    private int erros; // Número de decisões erradas
    private double fitness; // Valor de adaptação combinado

    public RedeNeuralTeste2(int numEntradasNeuronios, int numOcultos1Neuronios, int numSaidasNeuronios) {
        this.numEntradasNeuronios = numEntradasNeuronios;
        this.numOcultos1Neuronios = numOcultos1Neuronios;
        this.numSaidasNeuronios = numSaidasNeuronios;
        random = new Random();

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta1 = new double[numEntradasNeuronios][numOcultos1Neuronios];
        pesosOcultaSaida1 = new double[numOcultos1Neuronios][numSaidasNeuronios];
        biasOculta1 = new double[numOcultos1Neuronios];
        biasSaida = new double[numSaidasNeuronios];

        // Inicialize o array de entradas com o tamanho adequado
        entradas = new double[numEntradasNeuronios];

        inicializarPesos();
    }

    public void recebeEntradas(double[] valoresEntradas){
        for (int i = 0; i <numEntradasNeuronios; i++){
            entradas[i] = valoresEntradas[i];
            //System.out.println("Entrada coletada"+ entradas[i]);
        }
    }

    private void inicializarPesos() {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                //pesosEntradaOculta[i][j] = random.nextDouble() * 1.0 - 0.5; // [-0.5, 0.5]
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosEntradas: "+pesosEntradaOculta[i][j]);
            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosOcultaSaida: "+pesosOcultaSaida[i][j]);

            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasOculta: "+biasOculta[i]);

        }
        for (int i = 0; i < numSaidasNeuronios; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasSaida: "+biasSaida[i]);

        }
    }

    public double[] calcularSaida(double[] entradas) {
        double[] saidaOculta = new double[numOcultos1Neuronios];
        double[] saidaFinal = new double[numSaidasNeuronios];

        // Cálculo da camada oculta
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta[i] = relu(soma);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida1[j][i];
            }
            saidaFinal[i] = sigmoid(soma);
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

    //----------------------------------------------------------------------------------------------------
    //------------------------------------Rede Neural com duas Camadas
    public RedeNeuralTeste2(int numEntradasNeuronios, int numOcultos1Neuronios, int numOcultos2Neuronios, int numSaidasNeuronios) {
        this.numEntradasNeuronios = numEntradasNeuronios;
        this.numOcultos1Neuronios = numOcultos1Neuronios;
        this.numOcultos2Neuronios = numOcultos2Neuronios;
        this.numSaidasNeuronios = numSaidasNeuronios;
        random = new Random();

        pesosEntradaOculta1 = new double[numEntradasNeuronios][numOcultos1Neuronios];
        biasOculta1 = new double[numOcultos1Neuronios];
        pesosEntradaOculta2 = new double[numOcultos1Neuronios][numOcultos2Neuronios];
        biasOculta2 = new double[numOcultos2Neuronios];
        pesosOcultaSaida2 = new double[numOcultos2Neuronios][numSaidasNeuronios];
        biasSaida = new double[numSaidasNeuronios];

        entradas = new double[numEntradasNeuronios];


        inicializarPesos2();
    }

    private void inicializarPesos2() {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                pesosEntradaOculta2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            biasOculta2[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numSaidasNeuronios; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0;
        }
    }

    public double[] calcularSaida2(double[] entradas) {
        // Primeira camada oculta
        double[] saidaOculta1 = new double[numOcultos1Neuronios];
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
        }

        // Segunda camada oculta
        double[] saidaOculta2 = new double[numOcultos2Neuronios];
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
        }

        // Camada de saída
        double[] saidaFinal = new double[numSaidasNeuronios];
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }
            saidaFinal[i] = sigmoid(soma);
        }

        return saidaFinal;
    }

    public void copiarPesos2(RedeNeuralTeste2 outraRede) {
        // Copia pesos da primeira camada oculta
        for (int i = 0; i < numEntradasNeuronios; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, 
                            this.pesosEntradaOculta1[i], 0, numOcultos1Neuronios);
        }
        
        // Copia bias da primeira camada oculta
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, numOcultos1Neuronios);
        
        // Copia pesos da segunda camada oculta
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta2[i], 0, 
                            this.pesosEntradaOculta2[i], 0, numOcultos2Neuronios);
        }
        
        // Copia bias da segunda camada oculta
        System.arraycopy(outraRede.biasOculta2, 0, this.biasOculta2, 0, numOcultos2Neuronios);
        
        // Copia pesos da camada de saída
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida2[i], 0, 
                            this.pesosOcultaSaida2[i], 0, numSaidasNeuronios);
        }
        
        // Copia bias da camada de saída
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidasNeuronios);
    }

    public void ajustarPesosPorCondicao2(double[] entradas, double fator) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
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

        // Inicializa um array para armazenar as previsões
        int[] previsoes = new int[5];

        // Verificação baseada na posição Y
        if (y == 350) {
            previsoes[0] = random.nextInt(numeroInimigos) + 1; // Chute aleatório entre 1 e 4
        } else if (y == 320) {
            previsoes[0] = random.nextInt(numeroInimigos) + 1;
        } else if (y == 355) {
            previsoes[0] = random.nextInt(numeroInimigos) + 1;
        } else {
            previsoes[0] = 0;
        }

        // Verificação baseada na posição X para Meteoro
        if (x == 600 && altura == 70 && largura == 70) {
            previsoes[1] = random.nextInt(numeroInimigos) + 1;
        } else {
            previsoes[1] = -1;
        }

        // Verificação baseada nas dimensões
        if (altura == 70 && largura == 50) {
            previsoes[2] = random.nextInt(numeroInimigos) + 1;
        } else if (altura == 70 && largura == 70) {
            previsoes[2] = random.nextInt(numeroInimigos) + 1;
        } else {
            previsoes[2] = -1;
        }

        // Determina o tipo de inimigo com base nas previsões
        return determinarTipoInimigo(previsoes);
    }

    // Metodo para determinar o tipo de inimigo com base nas previsões
    private int determinarTipoInimigo(int[] previsoes) {
        // Lógica para determinar o tipo de inimigo com base nas previsões
        Map<Integer, Integer> contagem = new HashMap<>();
        for (int previsao : previsoes) {
            if (previsao != -1) { // Ignora previsões não identificadas
                contagem.put(previsao, contagem.getOrDefault(previsao, 0) + 1);
            }
        }
        return contagem.isEmpty() ? -1 : Collections.max(contagem.entrySet(), Map.Entry.comparingByValue()).getKey();
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
        // Primeira camada oculta
        double[] saidaOculta1 = new double[numOcultos1Neuronios];
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
        }

        // Segunda camada oculta
        double[] saidaOculta2 = new double[numOcultos2Neuronios];
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
        }

        // Camada de saída
        double[] saidaFinal = new double[numSaidasNeuronios];
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }
            saidaFinal[i] = sigmoid(soma);
        }

        // Backward pass
        // Erro na camada de saída
        double[] erroSaida = new double[numSaidasNeuronios];
        for (int i = 0; i < numSaidasNeuronios; i++) {
            erroSaida[i] = saidasEsperadas[i] - saidaFinal[i];
        }

        // Erro na segunda camada oculta
        double[] erroOculta2 = new double[numOcultos2Neuronios];
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                erroOculta2[i] += erroSaida[j] * pesosOcultaSaida2[i][j];
            }
            erroOculta2[i] *= (saidaOculta2[i] > 0 ? 1 : 0); // Derivada da ReLU
        }

        // Erro na primeira camada oculta
        double[] erroOculta1 = new double[numOcultos1Neuronios];
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                erroOculta1[i] += erroOculta2[j] * pesosEntradaOculta2[i][j];
            }
            erroOculta1[i] *= (saidaOculta1[i] > 0 ? 1 : 0); // Derivada da ReLU
        }

        // Atualização dos pesos
        // Atualização pesos entrada -> oculta1
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] += taxaAprendizagem * erroOculta1[j] * entradas[i];
            }
        }

        // Atualização bias oculta1
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            biasOculta1[i] += taxaAprendizagem * erroOculta1[i];
        }

        // Atualização pesos oculta1 -> oculta2
        for (int i = 0; i < numOcultos1Neuronios; i++) {
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                pesosEntradaOculta2[i][j] += taxaAprendizagem * erroOculta2[j] * saidaOculta1[i];
            }
        }

        // Atualização bias oculta2
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            biasOculta2[i] += taxaAprendizagem * erroOculta2[i];
        }

        // Atualização pesos oculta2 -> saída
        for (int i = 0; i < numOcultos2Neuronios; i++) {
            for (int j = 0; j < numSaidasNeuronios; j++) {
                pesosOcultaSaida2[i][j] += taxaAprendizagem * erroSaida[j] * saidaOculta2[i];
            }
        }

        // Atualização bias saída
        for (int i = 0; i < numSaidasNeuronios; i++) {
            biasSaida[i] += taxaAprendizagem * erroSaida[i];
        }
    }

    // Métodos para atualizar as métricas
    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public void incrementarSobrevivencia() {
        sobrevivencia++;
    }

    public void registrarAcerto() {
        acertos++;
    }

    public void registrarErro() {
        erros++;
    }

    // Método para calcular o fitness
    public void calcularFitness() {
        // Peso para cada métrica
        double pesoPontuacao = 0.4;
        double pesoSobrevivencia = 0.3;
        double pesoAcertos = 0.3;
        
        // Normalização das métricas
        double pontuacaoNormalizada = Math.min(pontuacao / 1000.0, 1.0); // Limita a pontuação a 1.0
        double sobrevivenciaNormalizada = Math.min(sobrevivencia / 1000.0, 1.0); // Limita a sobrevivência a 1.0
        
        // Calcula taxa de acertos evitando divisão por zero
        double taxaAcertos = 0.0;
        if (acertos + erros > 0) {
            taxaAcertos = acertos / (double)(acertos + erros);
        }
        
        // Cálculo do fitness
        fitness = (pesoPontuacao * pontuacaoNormalizada) +
                 (pesoSobrevivencia * sobrevivenciaNormalizada) +
                 (pesoAcertos * taxaAcertos);
    }

    // Getters para as métricas
    public double getFitness() {
        return fitness;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getSobrevivencia() {
        return sobrevivencia;
    }

    public double getTaxaAcertos() {
        if (acertos + erros == 0) {
            return 0.0;
        }
        return acertos / (double)(acertos + erros);
    }

    public void mutar(double taxaMutacao) {
        // Mutação nos pesos da primeira camada oculta
        for (int i = 0; i < pesosEntradaOculta1.length; i++) {
            for (int j = 0; j < pesosEntradaOculta1[i].length; j++) {
                if (Math.random() < taxaMutacao) {
                    pesosEntradaOculta1[i][j] += (Math.random() - 0.5) * 0.2; // Variação de ±0.1
                }
            }
        }
        
        // Mutação nos pesos da segunda camada oculta
        for (int i = 0; i < pesosEntradaOculta2.length; i++) {
            for (int j = 0; j < pesosEntradaOculta2[i].length; j++) {
                if (Math.random() < taxaMutacao) {
                    pesosEntradaOculta2[i][j] += (Math.random() - 0.5) * 0.2;
                }
            }
        }
        
        // Mutação nos pesos da camada de saída
        for (int i = 0; i < pesosOcultaSaida2.length; i++) {
            for (int j = 0; j < pesosOcultaSaida2[i].length; j++) {
                if (Math.random() < taxaMutacao) {
                    pesosOcultaSaida2[i][j] += (Math.random() - 0.5) * 0.2;
                }
            }
        }
        
        // Mutação nos bias
        for (int i = 0; i < biasOculta1.length; i++) {
            if (Math.random() < taxaMutacao) {
                biasOculta1[i] += (Math.random() - 0.5) * 0.2;
            }
        }
        
        for (int i = 0; i < biasOculta2.length; i++) {
            if (Math.random() < taxaMutacao) {
                biasOculta2[i] += (Math.random() - 0.5) * 0.2;
            }
        }
        
        for (int i = 0; i < biasSaida.length; i++) {
            if (Math.random() < taxaMutacao) {
                biasSaida[i] += (Math.random() - 0.5) * 0.2;
            }
        }
    }
}
