import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

        for (int i = 0; i < numEntradas; i++) {
            for (int j = 0; j < numOcultos; j++) {
                pesosEntradaOculta[i][j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
            }
        }

        for (int j = 0; j < numOcultos; j++) {
            biasOculta[j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
        }

        for (int i = 0; i < numOcultos; i++) {
            for (int j = 0; j < numSaidas; j++) {
                pesosOcultaSaida[i][j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
            }
        }

        for (int j = 0; j < numSaidas; j++) {
            biasSaida[j] = arredondar(random.nextDouble() * 2 - 1); // [-1, 1]
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

    // Método para calcular a saída da rede
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
}
