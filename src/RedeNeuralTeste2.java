import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RedeNeuralTeste2 {
    private double[][] pesosEntradaOculta; // Pesos da camada de entrada para a camada oculta
    private double[] biasOculta; // Bias para a camada oculta
    private double[][] pesosOcultaSaida; // Pesos da camada oculta para a camada de saída
    private double[] biasSaida; // Bias para a camada de saída

    private int numEntradas;
    private int numOcultos;
    private int numSaidas;

    public RedeNeuralTeste2(int numEntradas, int numOcultos, int numSaidas) {
        this.numEntradas = numEntradas;
        this.numOcultos = numOcultos;
        this.numSaidas = numSaidas;

        // Inicializa pesos e bias aleatoriamente
        Random random = new Random();
        pesosEntradaOculta = new double[numEntradas][numOcultos];
        biasOculta = new double[numOcultos];
        pesosOcultaSaida = new double[numOcultos][numSaidas];
        biasSaida = new double[numSaidas];

        System.out.println("CRIAMOS A REDE NEURAL");

        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                //pesosEntradaOculta[i][j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
                pesosEntradaOculta[i][j] = arredondar(random.nextDouble() * 0.2 - 0.1); // [-0.1, 0.1]
                System.out.println("RedeNeural: pesosEntradaOculta[i][j] " + pesosEntradaOculta[i][j]);


            }
        }

        for (int j = 0; j < numOcultos; j++) {
            biasOculta[j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
            System.out.println("RedeNeural: biasOculta[j] " + biasOculta[j]);

        }

        for (int i = 0; i < numOcultos; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida[i][j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
                System.out.println("RedeNeural: pesosOcultaSaida[i][j] " + pesosOcultaSaida[i][j]);

            }
        }

        for (int j = 0; j < numSaidas; j++) {
            biasSaida[j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
            System.out.println("RedeNeural: biasSaida[j] " + biasSaida[j]);

        }
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

    // Metodo para calcular a saída da rede
    public double[] calcularSaida(double[] entradas) {
        double[] camadaOculta = new double[numOcultos];

        // Cálculo da camada oculta
        for (int j = 0; j < numOcultos; j++) {
            camadaOculta[j] = biasOculta[j];
            for (int i = 0; i < numEntradas; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta[i][j];
            }
            camadaOculta[j] = arredondar(relu(camadaOculta[j]));
            System.out.println("CalculaSaida: camada oculta " + camadaOculta[j]);
        }

        // Cálculo da camada de saída
        double[] saida = new double[numSaidas];
        for (int j = 0; j < numSaidas; j++) {
            saida[j] = biasSaida[j];
            System.out.println("CalculaSaida: biasSaida " + saida[j]);
            for (int i = 0; i < numOcultos; i++) {
                saida[j] += camadaOculta[i] * pesosOcultaSaida[i][j];
            }
            saida[j] = arredondar(sigmoid(saida[j]));
            System.out.println("CalculaSaida: sigmoid(saida[j]) " + saida[j]);
        }

        return saida;
    }
//double[] saidas = redeNeural.calcularSaida(entradas);
//System.out.println("Saídas recalculadas: " + java.util.Arrays.toString(saidas));



    public void treinar(double[] entradas, double[] saidasEsperadas, double taxaAprendizagem) {
        // Forward pass
        double[] camadaOculta = new double[numOcultos];
        for (int j = 0; j < numOcultos; j++) {
            camadaOculta[j] = biasOculta[j];
            for (int i = 0; i < numEntradas; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta[i][j];
            }
            camadaOculta[j] = relu(camadaOculta[j]);
        }

        double[] saidas = new double[numSaidas];
        for (int j = 0; j < numSaidas; j++) {
            saidas[j] = biasSaida[j];
            for (int i = 0; i < numOcultos; i++) {
                saidas[j] += camadaOculta[i] * pesosOcultaSaida[i][j];
            }
            saidas[j] = sigmoid(saidas[j]);
        }

        // Backward pass (calcular erro e atualizar pesos)
        double[] erroSaida = new double[numSaidas];
        for (int j = 0; j < numSaidas; j++) {
            erroSaida[j] = saidasEsperadas[j] - saidas[j];
        }

        double[] erroOculto = new double[numOcultos];
        for (int j = 0; j < numOcultos; j++) {
            for (int k = 0; k < numSaidas; k++) {
                erroOculto[j] += erroSaida[k] * pesosOcultaSaida[j][k];
            }
            erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
        }

        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                pesosEntradaOculta[i][j] += taxaAprendizagem * erroOculto[j] * entradas[i];
            }
        }

        for (int j = 0; j < numOcultos; j++) {
            biasOculta[j] += taxaAprendizagem * erroOculto[j];
        }

        for (int j = 0; j < numOcultos; j++) {
            for (int k = 0; k < numSaidas; k++) {
                pesosOcultaSaida[j][k] += taxaAprendizagem * erroSaida[k] * camadaOculta[j];
            }
        }

        for (int k = 0; k < numSaidas; k++) {
            biasSaida[k] += taxaAprendizagem * erroSaida[k];
        }
    }

    public void ajustarPesosPorCondicao(double[] entradas, double fator) {
        // Ajusta os pesos da camada de entrada para a camada oculta
        for (int i = 0; i < pesosEntradaOculta.length; i++) {
            for (int j = 0; j < pesosEntradaOculta[i].length; j++) {
                pesosEntradaOculta[i][j] += entradas[i] * fator;
            }
        }
        // Ajusta os bias da camada oculta
        for (int i = 0; i < biasOculta.length; i++) {
            biasOculta[i] += fator;
        }
    }

    public void destruirRedeNeural() {
        // Anula todos os arrays da rede neural
        pesosEntradaOculta = null;
        biasOculta = null;
        pesosOcultaSaida = null;
        biasSaida = null;

        System.out.println("A rede neural foi destruída.");
    }

    public double[][] pesosEntradaOculta() {
        return pesosEntradaOculta;
    }

    public double[] biasOculta() {
        return biasOculta;
    }

    public double[][] pesosOcultaSaida() {
        return pesosOcultaSaida;
    }

    public double[] biasSaida() {
        return biasSaida;
    }

    public static void salvarDadosEmArquivo(List<RedeNeuralTeste2> redesNeurais) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt", false))) {
            for (int i = 0; i < redesNeurais.size(); i++) {
                RedeNeuralTeste2 rede = redesNeurais.get(i);
                writer.write("Rede: " + (i + 1) + "\n");

                writer.write("Pesos de entrada para camada oculta:\n");
                for (double[] linha : rede.pesosEntradaOculta()) {
                    for (double peso : linha) {
                        writer.write(peso + " ");
                    }
                    writer.write("\n");
                }

                writer.write("Bias da camada oculta:\n");
                for (double bias : rede.biasOculta()) {
                    writer.write(bias + " ");
                }
                writer.write("\n");

                writer.write("Pesos da camada oculta para saída:\n");
                for (double[] linha : rede.pesosOcultaSaida()) {
                    for (double peso : linha) {
                        writer.write(peso + " ");
                    }
                    writer.write("\n");
                }

                writer.write("Bias da camada de saída:\n");
                for (double bias : rede.biasSaida()) {
                    writer.write(bias + " ");
                }
                writer.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}