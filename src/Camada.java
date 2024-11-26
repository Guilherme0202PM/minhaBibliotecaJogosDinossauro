public class Camada {
    public Neuronio[] neuronios;

    public Camada(int quantidadeNeuronios) {
        neuronios = new Neuronio[quantidadeNeuronios];
        for (int i = 0; i < quantidadeNeuronios; i++) {
            neuronios[i] = new Neuronio(quantidadeNeuronios); // Cada neurônio possui pesos para a próxima camada
        }
    }
}
