import java.util.Random;

public class RedeNeuralTeste {
    private double[][] pesosEntradaOculta; // Pesos entre camada de entrada e intermediária
    private double[][] pesosSaidaOculta; // Pesos entre camada intermediária e de saída
    private double[] viesesOculto;
    private double[] viesesSaída;

    private int noEntrada;
    private int noOculto;
    private int noSaida;

    public RedeNeuralTeste(int noEntrada, int noOculto, int noSaida) {
        this.noEntrada = noEntrada;
        this.noOculto = noOculto;
        this.noSaida = noSaida;

        pesosEntradaOculta = new double[noEntrada][noOculto];
        pesosSaidaOculta = new double[noOculto][noSaida];
        viesesOculto = new double[noOculto];
        viesesSaída = new double[noSaida];

        inicializarPesos();
    }

    private void inicializarPesos() {
        Random random = new Random();
        for (int i = 0; i < noEntrada; i++) {
            for (int j = 0; j < noOculto; j++) {
                pesosEntradaOculta[i][j] = random.nextDouble() * 2 - 1; // Valores entre -1 e 1
            }
        }
        for (int i = 0; i < noOculto; i++) {
            for (int j = 0; j < noSaida; j++) {
                pesosSaidaOculta[i][j] = random.nextDouble() * 2 - 1;
            }
            viesesOculto[i] = random.nextDouble() * 2 - 1;
        }
        for (int i = 0; i < noSaida; i++) {
            viesesSaída[i] = random.nextDouble() * 2 - 1;
        }
    }

    public double[] previsao(double[] entradas) {
        double[] oculto = new double[noOculto];
        double[] saidas = new double[noSaida];

        // Calcular valores da camada intermediária
        for (int i = 0; i < noOculto; i++) {
            double soma = viesesOculto[i];
            for (int j = 0; j < noEntrada; j++) {
                soma += entradas[j] * pesosEntradaOculta[j][i];
            }
            oculto[i] = relu(soma); // Usar ReLU como ativação
        }

        // Calcular valores da camada de saída
        for (int i = 0; i < noSaida; i++) {
            double soma = viesesSaída[i];
            for (int j = 0; j < noOculto; j++) {
                soma += oculto[j] * pesosSaidaOculta[j][i];
            }
            saidas[i] = sigmoid(soma); // Usar Sigmoide para saída
        }

        return saidas;
    }

    private double relu(double x) {
        return Math.max(0, x);
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
