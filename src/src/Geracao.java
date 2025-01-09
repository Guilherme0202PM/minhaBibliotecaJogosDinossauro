import java.util.Random;

public class Geracao {
    private int geracaoAtual;
    private RedeNeural[] redes;
    private final int MAX_GERACOES = 10;

    public Geracao(int qtdRedes, int quantidadeEscondidas, int qtdNeuroniosEntrada, int qtdNeuroniosEscondida, int qtdNeuroniosSaida) {
        redes = new RedeNeural[qtdRedes];
        for (int i = 0; i < qtdRedes; i++) {
            redes[i] = new RedeNeural(quantidadeEscondidas, qtdNeuroniosEntrada, qtdNeuroniosEscondida, qtdNeuroniosSaida);
        }
        geracaoAtual = 1;
    }

    public RedeNeural novaRede() {
        if (geracaoAtual >= MAX_GERACOES) {
            return redes[0]; // Retorna a primeira rede da última geração
        }
        geracaoAtual++;
        return redes[new Random().nextInt(redes.length)];
    }

    public int getGeracaoAtual() {
        return geracaoAtual;
    }
}
