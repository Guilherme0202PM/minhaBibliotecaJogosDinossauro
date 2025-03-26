import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RedeNeuralTeste2 {
    private int numEntradas, numOcultos1, numOcultos2,  numSaidas;

    private double[][] pesosEntradaOculta1; // Pesos da camada de entrada para a camada oculta
    private double[][] pesosEntradaOculta2;
    private double[] biasOculta1; // Bias para a camada oculta
    private double[] biasOculta2;
    private double[][] pesosOcultaSaida1; // Pesos da camada oculta para a camada de saída
    private double[][] pesosOcultaSaida2;
    private double[] biasSaida; // Bias para a camada de saída
    private Random random;


    public RedeNeuralTeste2(int numEntradas, int numOcultos1, int numSaidas) {
        this.numEntradas = numEntradas;
        this.numOcultos1 = numOcultos1;
        this.numSaidas = numSaidas;
        random = new Random();

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta1 = new double[numEntradas][numOcultos1];
        pesosOcultaSaida1 = new double[numOcultos1][numSaidas];
        biasOculta1 = new double[numOcultos1];
        biasSaida = new double[numSaidas];

        inicializarPesos();
    }

    private void inicializarPesos() {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos1; j++) {
                //pesosEntradaOculta[i][j] = random.nextDouble() * 1.0 - 0.5; // [-0.5, 0.5]
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosEntradas: "+pesosEntradaOculta[i][j]);
            }
        }
        for (int i = 0; i < numOcultos1; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida1[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosOcultaSaida: "+pesosOcultaSaida[i][j]);

            }
        }
        for (int i = 0; i < numOcultos1; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasOculta: "+biasOculta[i]);

        }
        for (int i = 0; i < numSaidas; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasSaida: "+biasSaida[i]);

        }
    }

    public double[] calcularSaida(double[] entradas) {
        double[] saidaOculta = new double[numOcultos1];
        double[] saidaFinal = new double[numSaidas];

        // Cálculo da camada oculta
        for (int i = 0; i < numOcultos1; i++) {
            double soma = biasOculta1[i];
            //System.out.println("Cálculo da camada oculta soma antes: "+soma);
            for (int j = 0; j < numEntradas; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
                //System.out.println("Cálculo da camada oculta soma depois: "+soma);
            }
            saidaOculta[i] = tanh(soma);
            //System.out.println("Cálculo da camada oculta relu: "+saidaOculta[i]);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidas; i++) {
            double soma = biasSaida[i];
            //System.out.println("Cálculo da camada de saída soma antes: "+soma);
            for (int j = 0; j < numOcultos1; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida1[j][i];
                //System.out.println("Cálculo da camada de saída soma depois: "+soma);
            }
            saidaFinal[i] = sigmoid(soma);
            //System.out.println("Cálculo da camada oculta sigmoid: "+saidaFinal[i]);

        }
        return saidaFinal;
    }

    public void copiarPesos(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < numEntradas; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, numOcultos1);
        }
        for (int i = 0; i < numOcultos1; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida1[i], 0, this.pesosOcultaSaida1[i], 0, numSaidas);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, numOcultos1);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidas);
    }

    public void ajustarPesosPorCondicao(double[] entradas, double fator) {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos1; j++) {
                pesosEntradaOculta1[i][j] = fator * entradas[i];
            }
        }
    }

    //----------------------------------------------------------------------------------------------------
    //------------------------------------Rede Neural com duas Camadas
    public RedeNeuralTeste2(int numEntradas, int numOcultos1, int numOcultos2, int numSaidas) {
        this.numEntradas = numEntradas;
        this.numOcultos1 = numOcultos1;
        this.numOcultos2 = numOcultos2;
        this.numSaidas = numSaidas;
        random = new Random();

        pesosEntradaOculta1 = new double[numEntradas][numOcultos1];
        biasOculta1 = new double[numOcultos1];
        pesosEntradaOculta2 = new double[numOcultos1][numOcultos2];
        biasOculta2 = new double[numOcultos2];
        pesosOcultaSaida2 = new double[numOcultos2][numSaidas];
        biasSaida = new double[numSaidas];

        inicializarPesos2();
    }

    private void inicializarPesos2() {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos1; j++) {
                pesosEntradaOculta1[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos1; i++) {
            for (int j = 0; j < numOcultos2; j++) {
                pesosEntradaOculta2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos2; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida2[i][j] = random.nextDouble() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < numOcultos1; i++) {
            biasOculta1[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numOcultos2; i++) {
            biasOculta2[i] = random.nextDouble() * 2.0 - 1.0;
        }
        for (int i = 0; i < numSaidas; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0;
        }
    }

    public double[] calcularSaida2(double[] entradas) {
        double[] saidaOculta1 = new double[numOcultos1];
        double[] saidaOculta2 = new double[numOcultos2];
        double[] saidaFinal = new double[numSaidas];

        for (int i = 0; i < numOcultos1; i++) {
            double soma = biasOculta1[i];
            for (int j = 0; j < numEntradas; j++) {
                soma += entradas[j] * pesosEntradaOculta1[j][i];
            }
            saidaOculta1[i] = relu(soma);
        }

        for (int i = 0; i < numOcultos2; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos1; j++) {
                soma += saidaOculta1[j] * pesosEntradaOculta2[j][i];
            }
            saidaOculta2[i] = relu(soma);
        }

        for (int i = 0; i < numSaidas; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos2; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];
            }
            saidaFinal[i] = sigmoid(soma);
        }

        return saidaFinal;
    }

    public void copiarPesos2(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < numEntradas; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta1[i], 0, this.pesosEntradaOculta1[i], 0, numOcultos1);
        }
        for (int i = 0; i < numOcultos1; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta2[i], 0, this.pesosEntradaOculta2[i], 0, numOcultos2);
        }
        for (int i = 0; i < numOcultos2; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida2[i], 0, this.pesosOcultaSaida2[i], 0, numSaidas);
        }
        System.arraycopy(outraRede.biasOculta1, 0, this.biasOculta1, 0, numOcultos1);
        System.arraycopy(outraRede.biasOculta2, 0, this.biasOculta2, 0, numOcultos2);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidas);
    }

    public void ajustarPesosPorCondicao2(double[] entradas, double fator) {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos1; j++) {
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
            previsoes[0] = -1;
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
        double[] camadaOculta = new double[numOcultos1];
        for (int j = 0; j < numOcultos1; j++) {
            camadaOculta[j] = biasOculta1[j];
            for (int i = 0; i < numEntradas; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta1[i][j];
            }
            camadaOculta[j] = tanh(camadaOculta[j]);  // Mudamos para tanh;
        }

        double[] saidas = new double[numSaidas];
        for (int j = 0; j < numSaidas; j++) {
            saidas[j] = biasSaida[j];
            for (int i = 0; i < numOcultos1; i++) {
                saidas[j] += camadaOculta[i] * pesosOcultaSaida1[i][j];
            }
            saidas[j] = sigmoid(saidas[j]);
        }

        // Backward pass (calcular erro e atualizar pesos)
        double[] erroSaida = new double[numSaidas];
        for (int j = 0; j < numSaidas; j++) {
            erroSaida[j] = saidasEsperadas[j] - saidas[j];
        }

        double[] erroOculto = new double[numOcultos1];
        for (int j = 0; j < numOcultos1; j++) {
            for (int k = 0; k < numSaidas; k++) {
                erroOculto[j] += erroSaida[k] * pesosOcultaSaida1[j][k];
            }
            //erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
            erroOculto[j] *= tanhDerivada(camadaOculta[j]);  // Usando derivada do tanh
        }

        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos1; j++) {
                pesosEntradaOculta1[i][j] += taxaAprendizagem * erroOculto[j] * entradas[i];
            }
        }

        for (int j = 0; j < numOcultos1; j++) {
            biasOculta1[j] += taxaAprendizagem * erroOculto[j];
        }

        for (int j = 0; j < numOcultos1; j++) {
            for (int k = 0; k < numSaidas; k++) {
                pesosOcultaSaida1[j][k] += taxaAprendizagem * erroSaida[k] * camadaOculta[j];
            }
        }

        for (int k = 0; k < numSaidas; k++) {
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

}