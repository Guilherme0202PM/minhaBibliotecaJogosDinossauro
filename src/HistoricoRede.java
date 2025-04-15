public class HistoricoRede {
    private RedeNeuralTeste2 rede;
    private double fitness;

    public HistoricoRede(RedeNeuralTeste2 rede, double fitness) {
        this.rede = rede;
        this.fitness = fitness;
    }

    public RedeNeuralTeste2 getRede() {
        return rede;
    }

    public double getFitness() {
        return fitness;
    }
}
