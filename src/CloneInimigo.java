import javax.swing.*;

public class CloneInimigo extends Inimigo {
    private static final int POSICAO_FINAL_X = -1000; // Posição X final onde o clone será excluído

    public CloneInimigo(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX, velocidadeY, movimento, sensores, janela);
    }

    @Override
    public void atualizar() {
        // Move o inimigo na direção atual
        movimento.movimentoX(this, velocidadeX);
        movimento.movimentoY(this, velocidadeY);

        // Verifica se o inimigo alcançou a posição final
        if (this.getX() <= POSICAO_FINAL_X) {
            // Remove o clone atual
            janela.removeObjeto(this);

            // Cria um novo clone na posição inicial
            CloneInimigo novoClone = new CloneInimigo(1000, this.getY(), this.getLargura(), this.getAltura(), "Monstro.png", -5, 0, movimento, sensores, janela);
            janela.adicionarObjeto(novoClone);
        }
    }
}
