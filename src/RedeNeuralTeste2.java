import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RedeNeuralTeste2 {
    private int numEntradasNeuronios, numOcultos1Neuronios, numOcultos2Neuronios,  numSaidasNeuronios;
    private double taxaMutacaoPopulacional = 0.1; // 50% de chance de mutação na população
    private double taxaMutacaoIndividual = 0.3;   // 30% de diferença entre indivíduos

    private double[][] pesosEntradaOculta1; // Pesos da camada de entrada para a camada oculta
    private double[][] pesosEntradaOculta2;
    private double[] biasOculta1; // Bias para a camada oculta
    private double[] biasOculta2;
    private double[][] pesosOcultaSaida1; // Pesos da camada oculta para a camada de saída
    private double[][] pesosOcultaSaida2;
    private double[] biasSaida; // Bias para a camada de saída
    private Random random;


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

        inicializarPesos();
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
            //System.out.println("Cálculo da camada oculta soma antes: "+soma);
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
                //System.out.println("Cálculo da camada oculta soma depois: "+soma);
            }
            saidaOculta[i] = tanh(soma);
            //System.out.println("Cálculo da camada oculta relu: "+saidaOculta[i]);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            //System.out.println("Cálculo da camada de saída soma antes: "+soma);
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida1[j][i];
                //System.out.println("Cálculo da camada de saída soma depois: "+soma);
            }
            saidaFinal[i] = sigmoid(soma);
            //System.out.println("Cálculo da camada oculta sigmoid: "+saidaFinal[i]);

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
    //----------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------
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
        double[] saidaOculta1 = new double[numOcultos1Neuronios];
        double[] saidaOculta2 = new double[numOcultos2Neuronios];
        double[] saidaFinal = new double[numSaidasNeuronios];

        for (int i = 0; i < numOcultos1Neuronios; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradasNeuronios; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
        }

        for (int i = 0; i < numOcultos2Neuronios; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
        }

        for (int i = 0; i < numSaidasNeuronios; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos2Neuronios; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }
            saidaFinal[i] = sigmoid(soma);
            System.out.println("Saida: " +saidaFinal[i]);
        }

        return saidaFinal;
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

    public void ajustarPesosPorCondicao2(double[] entradas, double fator) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
    }

    public void ajustarPesosPorCondicoesSeparadas(double[] entradas, double fatorY, double fatorX) {
        for (int i = 0; i < numEntradasNeuronios; i++) {
            for (int j = 0; j < numOcultos1Neuronios; j++) {
                double fator = 0;

                // Exemplo: entradas 0 = X do player, 1 = Y do player, 2 = X do inimigo, 3 = Y do inimigo
                if (i == 1 || i == 3 || i == 4) { // Dados mais relevantes para altura
                    fator = fatorY;
                } else if (i == 0 || i == 2 || i == 5) { // Dados mais relevantes para posição X ou largura
                    fator = fatorX;
                } else {
                    fator = 1; // Para velocidade ou neutros
                }

                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
    }


    // Função de arredondamento para 4 casas decimais
    private double arredondar(double valor) {
        return new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
        double[] camadaOculta = new double[numOcultos1Neuronios];
        for (int j = 0; j < numOcultos1Neuronios; j++) {
            camadaOculta[j] = biasOculta1[j];
            for (int i = 0; i < numEntradasNeuronios; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta1[i][j];
            }
            camadaOculta[j] = tanh(camadaOculta[j]);  // Mudamos para tanh;
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
            //erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
            erroOculto[j] *= tanhDerivada(camadaOculta[j]);  // Usando derivada do tanh
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

    private int pontuacao;

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void aplicarMutacaoPopulacional(List<RedeNeuralTeste2> populacao) {
        Random random = new Random();

        // Aplica mutação populacional em 50% dos indivíduos
        for (RedeNeuralTeste2 rede : populacao) {
            if (random.nextDouble() < taxaMutacaoPopulacional) {
                // Aplica mutação individual de 30% em cada peso
                for (int i = 0; i < numEntradasNeuronios; i++) {
                    for (int j = 0; j < numOcultos1Neuronios; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta1[i][j] += variacao;
                    }
                }

                // Mutação nos bias da primeira camada
                for (int i = 0; i < numOcultos1Neuronios; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta1[i] += variacao;
                }

                // Mutação nos pesos da segunda camada
                for (int i = 0; i < numOcultos1Neuronios; i++) {
                    for (int j = 0; j < numOcultos2Neuronios; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosEntradaOculta2[i][j] += variacao;
                    }
                }

                // Mutação nos bias da segunda camada
                for (int i = 0; i < numOcultos2Neuronios; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasOculta2[i] += variacao;
                }

                // Mutação nos pesos da camada de saída
                for (int i = 0; i < numOcultos2Neuronios; i++) {
                    for (int j = 0; j < numSaidasNeuronios; j++) {
                        double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                        rede.pesosOcultaSaida2[i][j] += variacao;
                    }
                }

                // Mutação nos bias da camada de saída
                for (int i = 0; i < numSaidasNeuronios; i++) {
                    double variacao = random.nextGaussian() * taxaMutacaoIndividual;
                    rede.biasSaida[i] += variacao;
                }
            }
        }
    }

}