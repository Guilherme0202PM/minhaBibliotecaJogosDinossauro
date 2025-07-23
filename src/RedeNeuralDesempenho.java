import java.util.Arrays;

public class RedeNeuralDesempenho {
    private RedeNeuralTeste3 redeNeural;
    private double pontuacao;
    private double fitness;
    private int numEntradas;
    private int[] numOcultos;
    private int numSaidas;
    private int cronometro;

    public RedeNeuralDesempenho(RedeNeuralTeste3 redeNeural, int cronometro) {
        this.redeNeural = redeNeural;
        this.pontuacao = redeNeural.getPontuacao();
        this.fitness = redeNeural.getFitness();
        this.numEntradas = redeNeural.getNumEntradas();
        this.numOcultos = redeNeural.getNumOcultos();
        this.numSaidas = redeNeural.getNumSaidas();
        this.cronometro = cronometro;
    }

    public RedeNeuralTeste3 getRedeNeural() {
        return redeNeural;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public double getFitness() {
        return fitness;
    }

    public int getNumEntradas() {
        return numEntradas;
    }

    public int[] getNumOcultos() {
        return numOcultos;
    }

    public int getNumSaidas() {
        return numSaidas;
    }

    public int getCronometro() {
        return cronometro;
    }

    @Override
    public String toString() {
        return "RedeNeuralTeste3 | Pontuação: " + pontuacao +
                " | Fitness: " + fitness +
                " | Arquitetura: " + numEntradas + "→" + Arrays.toString(numOcultos) + "→" + numSaidas +
                " | Cronômetro: " + cronometro;
    }
}