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
    private int numEntradas, numOcultos, numOcultos2, numSaidas;

    private double[][] pesosEntradaOculta; // Pesos da camada de entrada para a camada oculta
    private double[] biasOculta; // Bias para a camada oculta
    private double[][] pesosOcultaSaida; // Pesos da camada oculta para a camada de saída
    private double[] biasSaida; // Bias para a camada de saída
    private Random random;

    private double[][] pesosEntradaOculta2; // Pesos da camada de entrada para a camada oculta
    private double[] biasOculta2; // Bias para a camada oculta
    private double[][] pesosOcultaSaida2; // Pesos da camada oculta para a camada de saída

    public RedeNeuralTeste2(int numEntradas, int numOcultos,int numOcultos2, int numSaidas) {
        this.numEntradas = numEntradas;
        this.numOcultos = numOcultos;
        this.numOcultos2 = numOcultos2;
        this.numSaidas = numSaidas;
        random = new Random();

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta = new double[numEntradas][numOcultos];
        pesosOcultaSaida = new double[numOcultos][numSaidas];
        biasOculta = new double[numOcultos];

        pesosEntradaOculta2 = new double[numEntradas][numOcultos2];
        pesosOcultaSaida2 = new double[numOcultos2][numSaidas];
        biasOculta2 = new double[numOcultos2];

        biasSaida = new double[numSaidas];

        inicializarPesos2();
    }

    public RedeNeuralTeste2(int numEntradas, int numOcultos, int numSaidas) {
        this.numEntradas = numEntradas;
        this.numOcultos = numOcultos;
        this.numSaidas = numSaidas;
        random = new Random();

        // Inicialização dos pesos com escala ajustada
        pesosEntradaOculta = new double[numEntradas][numOcultos];
        pesosOcultaSaida = new double[numOcultos][numSaidas];
        biasOculta = new double[numOcultos];
        biasSaida = new double[numSaidas];

        inicializarPesos();
    }

    private void inicializarPesos2() {
        // Inicializando os pesos da camada de entrada para a primeira camada oculta
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                pesosEntradaOculta[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            }
        }

        // Inicializando os pesos da primeira camada oculta para a segunda camada oculta
        for (int i = 0; i < numOcultos; i++) {
            for (int j = 0; j < numOcultos2; j++) {
                pesosEntradaOculta2[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            }
        }

        // Inicializando os pesos da camada oculta 2 para a camada de saída
        for (int i = 0; i < numOcultos2; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida2[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            }
        }

        // Inicializando o bias para a primeira camada oculta
        for (int i = 0; i < numOcultos; i++) {
            biasOculta[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
        }

        // Inicializando o bias para a segunda camada oculta
        for (int i = 0; i < numOcultos2; i++) {
            biasOculta2[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
        }

        // Inicializando o bias para a camada de saída
        for (int i = 0; i < numSaidas; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
        }
    }



    private void inicializarPesos() {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                //pesosEntradaOculta[i][j] = random.nextDouble() * 1.0 - 0.5; // [-0.5, 0.5]
                pesosEntradaOculta[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosEntradas: "+pesosEntradaOculta[i][j]);
            }
        }
        for (int i = 0; i < numOcultos; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida[i][j] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
                //System.out.println("pesosOcultaSaida: "+pesosOcultaSaida[i][j]);

            }
        }
        for (int i = 0; i < numOcultos; i++) {
            biasOculta[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasOculta: "+biasOculta[i]);

        }
        for (int i = 0; i < numSaidas; i++) {
            biasSaida[i] = random.nextDouble() * 2.0 - 1.0; // [-1, 1]
            //System.out.println("biasSaida: "+biasSaida[i]);

        }
    }

    public double[] calcularSaida2(double[] entradas) {
        double[] saidaOculta = new double[numOcultos];
        double[] saidaOculta2 = new double[numOcultos2];  // Nova variável para a segunda camada oculta
        double[] saidaFinal = new double[numSaidas];

        // Cálculo da primeira camada oculta
        for (int i = 0; i < numOcultos; i++) {
            double soma = biasOculta[i];
            for (int j = 0; j < numEntradas; j++) {
                soma += entradas[j] * pesosEntradaOculta[j][i];
            }
            saidaOculta[i] = tanh(soma);  // Função de ativação para a primeira camada oculta
        }

        // Cálculo da segunda camada oculta
        for (int i = 0; i < numOcultos2; i++) {
            double soma = biasOculta2[i];
            for (int j = 0; j < numOcultos; j++) {
                soma += saidaOculta[j] * pesosEntradaOculta2[j][i];  // Pesos da primeira camada oculta para a segunda
            }
            saidaOculta2[i] = tanh(soma);  // Função de ativação para a segunda camada oculta
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidas; i++) {
            double soma = biasSaida[i];
            for (int j = 0; j < numOcultos2; j++) {
                soma += saidaOculta2[j] * pesosOcultaSaida2[j][i];  // Pesos da segunda camada oculta para a saída
            }
            saidaFinal[i] = sigmoid(soma);  // Função de ativação para a camada de saída
        }

        return saidaFinal;
    }


    public double[] calcularSaida(double[] entradas) {
        double[] saidaOculta = new double[numOcultos];
        double[] saidaFinal = new double[numSaidas];

        // Cálculo da camada oculta
        for (int i = 0; i < numOcultos; i++) {
            double soma = biasOculta[i];
            //System.out.println("Cálculo da camada oculta soma antes: "+soma);
            for (int j = 0; j < numEntradas; j++) {
                soma += entradas[j] * pesosEntradaOculta[j][i];
                //System.out.println("Cálculo da camada oculta soma depois: "+soma);
            }
            saidaOculta[i] = tanh(soma);
            //System.out.println("Cálculo da camada oculta relu: "+saidaOculta[i]);
        }

        // Cálculo da camada de saída
        for (int i = 0; i < numSaidas; i++) {
            double soma = biasSaida[i];
            //System.out.println("Cálculo da camada de saída soma antes: "+soma);
            for (int j = 0; j < numOcultos; j++) {
                soma += saidaOculta[j] * pesosOcultaSaida[j][i];
                //System.out.println("Cálculo da camada de saída soma depois: "+soma);
            }
            saidaFinal[i] = sigmoid(soma);
            //System.out.println("Cálculo da camada oculta sigmoid: "+saidaFinal[i]);

        }
        return saidaFinal;
    }

    public void copiarPesos(RedeNeuralTeste2 outraRede) {
        for (int i = 0; i < numEntradas; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta[i], 0, this.pesosEntradaOculta[i], 0, numOcultos);
        }
        for (int i = 0; i < numOcultos; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida[i], 0, this.pesosOcultaSaida[i], 0, numSaidas);
        }
        System.arraycopy(outraRede.biasOculta, 0, this.biasOculta, 0, numOcultos);
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidas);
    }

    public void copiarPesos2(RedeNeuralTeste2 outraRede) {
        // Copiar pesos da primeira camada (Entrada -> Oculta)
        for (int i = 0; i < numEntradas; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta[i], 0, this.pesosEntradaOculta[i], 0, numOcultos);
        }

        // Copiar pesos da primeira camada oculta para a segunda camada oculta
        for (int i = 0; i < numOcultos; i++) {
            System.arraycopy(outraRede.pesosEntradaOculta2[i], 0, this.pesosEntradaOculta2[i], 0, numOcultos2);
        }

        // Copiar pesos da segunda camada oculta para a camada de saída
        for (int i = 0; i < numOcultos2; i++) {
            System.arraycopy(outraRede.pesosOcultaSaida2[i], 0, this.pesosOcultaSaida2[i], 0, numSaidas);
        }

        // Copiar biases da primeira camada oculta
        System.arraycopy(outraRede.biasOculta, 0, this.biasOculta, 0, numOcultos);

        // Copiar biases da segunda camada oculta
        System.arraycopy(outraRede.biasOculta2, 0, this.biasOculta2, 0, numOcultos2);

        // Copiar biases da camada de saída
        System.arraycopy(outraRede.biasSaida, 0, this.biasSaida, 0, numSaidas);
    }


    public void ajustarPesosPorCondicao(double[] entradas, double fator) {
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                pesosEntradaOculta[i][j] = fator * entradas[i];
            }
        }
    }

    public void ajustarPesosPorCondicao2(double[] entradas, double fator) {
        // Ajustar pesos da primeira camada (Entrada -> Oculta)
        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                pesosEntradaOculta[i][j] += fator * entradas[i]; // Ajuste simples, dependendo da lógica de aprendizado
            }
        }

        // Ajustar pesos da primeira camada oculta para a segunda camada oculta
        for (int i = 0; i < numOcultos; i++) {
            for (int j = 0; j < numOcultos2; j++) {
                pesosEntradaOculta2[i][j] += fator * entradas[i]; // Ajuste dos pesos para a segunda camada oculta
            }
        }

        // Ajustar pesos da segunda camada oculta para a camada de saída
        for (int i = 0; i < numOcultos2; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida2[i][j] += fator * entradas[i]; // Ajuste dos pesos para a camada de saída
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
        double[] camadaOculta = new double[numOcultos];
        for (int j = 0; j < numOcultos; j++) {
            camadaOculta[j] = biasOculta[j];
            for (int i = 0; i < numEntradas; i++) {
                camadaOculta[j] += entradas[i] * pesosEntradaOculta[i][j];
            }
            camadaOculta[j] = tanh(camadaOculta[j]);  // Mudamos para tanh;
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
            //erroOculto[j] *= (camadaOculta[j] > 0 ? 1 : 0); // Derivada da ReLU
            erroOculto[j] *= tanhDerivada(camadaOculta[j]);  // Usando derivada do tanh
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

    private int pontuacao;

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

// Getters e Setters
//-----------------------------------------------------------------

    // Métodos para numEntradas
    public int getNumEntradas() {
        return numEntradas;
    }

    public void setNumEntradas(int numEntradas) {
        this.numEntradas = numEntradas;
    }

    // Métodos para numOcultos
    public int getNumOcultos() {
        return numOcultos;
    }

    public void setNumOcultos(int numOcultos) {
        this.numOcultos = numOcultos;
    }

    // Métodos para numSaidas
    public int getNumSaidas() {
        return numSaidas;
    }

    public void setNumSaidas(int numSaidas) {
        this.numSaidas = numSaidas;
    }

    // Métodos para pesosEntradaOculta
    public double[][] getPesosEntradaOculta() {
        return pesosEntradaOculta;
    }

    public void setPesosEntradaOculta(double[][] pesosEntradaOculta) {
        this.pesosEntradaOculta = pesosEntradaOculta;
    }

    // Métodos para biasOculta
    public double[] getBiasOculta() {
        return biasOculta;
    }

    public void setBiasOculta(double[] biasOculta) {
        this.biasOculta = biasOculta;
    }

    // Métodos para pesosOcultaSaida
    public double[][] getPesosOcultaSaida() {
        return pesosOcultaSaida;
    }

    public void setPesosOcultaSaida(double[][] pesosOcultaSaida) {
        this.pesosOcultaSaida = pesosOcultaSaida;
    }

    // Métodos para biasSaida
    public double[] getBiasSaida() {
        return biasSaida;
    }

    public void setBiasSaida(double[] biasSaida) {
        this.biasSaida = biasSaida;
    }


    public static void salvarDadosEmArquivo(List<RedeNeuralTeste2> redesNeurais) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt", false))) {
            for (int i = 0; i < redesNeurais.size(); i++) {
                RedeNeuralTeste2 rede = redesNeurais.get(i);
                writer.write("Rede: " + (i + 1) + "\n");

                writer.write("Pesos de entrada para camada oculta:\n");
                for (double[] linha : rede.getPesosEntradaOculta()) {
                    for (double peso : linha) {
                        writer.write(peso + " ");
                    }
                    writer.write("\n");
                }

                writer.write("Bias da camada oculta:\n");
                for (double bias : rede.getBiasOculta()) {
                    writer.write(bias + " ");
                }
                writer.write("\n");

                writer.write("Pesos da camada oculta para saída:\n");
                for (double[] linha : rede.getPesosOcultaSaida()) {
                    for (double peso : linha) {
                        writer.write(peso + " ");
                    }
                    writer.write("\n");
                }

                writer.write("Bias da camada de saída:\n");
                for (double bias : rede.getBiasSaida()) {
                    writer.write(bias + " ");
                }
                writer.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}