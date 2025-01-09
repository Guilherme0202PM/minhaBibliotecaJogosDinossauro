public class Neuronio {
    public double[] peso; // Pesos para as conexões
    public double saida; // Saída do neurônio
    public int quantidadeLigacoes; // Número de ligações

    public Neuronio(int quantidadeLigacoes) {
        this.quantidadeLigacoes = quantidadeLigacoes;
        this.peso = new double[quantidadeLigacoes];
    }
}
