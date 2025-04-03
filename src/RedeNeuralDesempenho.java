public class RedeNeuralDesempenho {
    private RedeNeuralTeste2 redeNeural;
    private double pontuacao;
    private int cronometro;

    public RedeNeuralDesempenho(RedeNeuralTeste2 redeNeural, int cronometro) {
        this.redeNeural = redeNeural;
        this.pontuacao = redeNeural.getPontuacao();
        this.cronometro = cronometro;
    }

    public RedeNeuralTeste2 getRedeNeural() {
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
