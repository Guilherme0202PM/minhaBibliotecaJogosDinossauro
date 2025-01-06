import java.awt.*;

public class Inimigo extends CriaObjeto {
    protected int velocidadeX;
    protected int velocidadeY;

    protected Movimento movimento;
    protected Sensores sensores;
    protected GameWindow janela;


    public Inimigo(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem);
        this.velocidadeX = velocidadeX;
        this.velocidadeY = velocidadeY;
        this.movimento = movimento;
        this.sensores = sensores;
        this.janela = janela;
    }


    public void atualizar() {
        // Move o inimigo na direção atual
        movimento.movimentoX(this, velocidadeX);
        movimento.movimentoY(this, velocidadeY);

        // Verifica se o inimigo está fora da tela e remove se necessário
        if (getRect().x < -1000) {
            janela.removerObjeto(this);
        }
    }
}
