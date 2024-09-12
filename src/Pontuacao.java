public class Pontuacao {
    private int pontos;
    private final int pontuacaoMaxima = 10;

    public Pontuacao() {
        this.pontos = 0;
    }

    public void adicionarPonto() {
        if (pontos < pontuacaoMaxima) {
            pontos++;
            System.out.println("Pontuação atual: " + pontos);
        }
    }

    public int getPontos() {
        return pontos;
    }

    public boolean atingiuPontuacaoMaxima() {
        return pontos >= pontuacaoMaxima;
    }
}
