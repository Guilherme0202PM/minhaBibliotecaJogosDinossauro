import java.awt.*;

public class InimigoTerrestre extends Inimigo {
    private int velocidadeX;
    private int velocidadeY;

    private Movimento movimento;

    private Sensores sensores;
    private GameWindow janela;

    public InimigoTerrestre(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX,velocidadeY, movimento, sensores, janela);

    }

    public void atualizar() {
        // Move o inimigo na direção atual
        movimento.movimentoX(this, velocidadeX);
        movimento.movimentoY(this, velocidadeY);

        // Verifica se o inimigo está tocando as bordas e ajusta a direção se necessário
        if (sensores.tocandoBorda(this)) {
            velocidadeX = -velocidadeX;
            velocidadeY = -velocidadeY;
        }
    }
}