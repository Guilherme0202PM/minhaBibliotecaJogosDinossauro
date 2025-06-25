public class RedeNeuralDesempenho {
    private RedeNeuralTeste3 redeNeural;
    private double pontuacao;
    private int cronometro;

    public RedeNeuralDesempenho(RedeNeuralTeste3 redeNeural, int cronometro) {
        this.redeNeural = redeNeural;
        this.pontuacao = redeNeural.getPontuacao();
        this.cronometro = cronometro;
    }

    public RedeNeuralTeste3 getRedeNeural() {
        return redeNeural;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public int getCronometro() {
        return cronometro;
    }

    @Override
    public String toString() {
        return redeNeural + " | Pontuação: " + pontuacao + " | Cronômetro: " + cronometro;
    }
}