import java.util.Random;

public class RedeNeural {
    private Camada camadaEntrada;
    private Camada[] camadaEscondida;
    private Camada camadaSaida;
    private int quantidadeEscondidas;
    private int maxGeracoes = 10; // Número máximo de gerações permitidas
    private int geracaoAtual = 0; // Contador de gerações

    public RedeNeural(int quantidadeEscondidas, int qtdNeuroniosEntrada, int qtdNeuroniosEscondida, int qtdNeuroniosSaida) {
        this.quantidadeEscondidas = quantidadeEscondidas;
        this.camadaEntrada = new Camada(qtdNeuroniosEntrada + 1); // +1 para o bias
        this.camadaEscondida = new Camada[quantidadeEscondidas];

        for (int i = 0; i < quantidadeEscondidas; i++) {
            this.camadaEscondida[i] = new Camada(qtdNeuroniosEscondida + 1); // +1 para o bias
        }
        this.camadaSaida = new Camada(qtdNeuroniosSaida);

        inicializarPesos(); // Inicializa os pesos aleatoriamente
    }

    private void inicializarPesos() {
        Random random = new Random();
        // Inicializa os pesos dos neurônios nas camadas escondidas
        for (Camada camada : camadaEscondida) {
            for (Neuronio neuronio : camada.neuronios) {
                for (int j = 0; j < neuronio.quantidadeLigacoes; j++) {
                    neuronio.peso[j] = random.nextDouble() * 2 - 1; // Pesos aleatórios entre -1 e 1
                }
            }
        }

        // Inicializa os pesos da camada de saída
        for (Neuronio neuronio : camadaSaida.neuronios) {
            for (int j = 0; j < neuronio.quantidadeLigacoes; j++) {
                neuronio.peso[j] = random.nextDouble() * 2 - 1;
            }
        }
    }

    public void calcularSaida(double[] entradas) {
        copiarParaEntrada(entradas);
        calcularCamadas();
        calcularSaidaFinal();
    }

    private void copiarParaEntrada(double[] entradas) {
        for (int i = 0; i < camadaEntrada.neuronios.length - 1; i++) {
            camadaEntrada.neuronios[i].saida = entradas[i];
        }
    }

    private void calcularCamadas() {
        for (int i = 0; i < camadaEscondida.length; i++) {
            Camada camadaAtual = camadaEscondida[i];
            Camada camadaAnterior = (i == 0) ? camadaEntrada : camadaEscondida[i - 1];
            for (Neuronio neuronio : camadaAtual.neuronios) {
                double somatorio = 0;
                for (int j = 0; j < camadaAnterior.neuronios.length; j++) {
                    somatorio += camadaAnterior.neuronios[j].saida * neuronio.peso[j];
                }
                neuronio.saida = relu(somatorio);
            }
        }
    }

    private void calcularSaidaFinal() {
        for (int i = 0; i < camadaSaida.neuronios.length; i++) {
            Neuronio neuronio = camadaSaida.neuronios[i];
            double somatorio = 0;
            for (Neuronio neuronioEscondido : camadaEscondida[camadaEscondida.length - 1].neuronios) {
                somatorio += neuronioEscondido.saida * neuronio.peso[i];
            }
            neuronio.saida = relu(somatorio);
        }
    }

    private double relu(double valor) {
        return Math.max(0, valor);
    }

    public double[] obterSaidas() {
        double[] saidas = new double[camadaSaida.neuronios.length];
        for (int i = 0; i < camadaSaida.neuronios.length; i++) {
            saidas[i] = camadaSaida.neuronios[i].saida;
        }
        return saidas;
    }

    public void novaGeracao() {
        if (geracaoAtual < maxGeracoes) {
            geracaoAtual++;
            inicializarPesos(); // Reinicia os pesos para a nova geração
            System.out.println("Nova geração criada: " + geracaoAtual);
        } else {
            System.out.println("Máximo de gerações alcançado.");
        }
    }
}
