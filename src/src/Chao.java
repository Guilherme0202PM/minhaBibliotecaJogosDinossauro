import java.awt.Graphics;

public class Chao extends Plataforma {

    public Chao(int x, int y, int largura, int altura) {
        super(x, y, largura, altura, "Chao1.jpg"); // Usa a imagem Chao1.jpg
    }


    // O jogador consegue andar normalmente sobre o chão
    @Override
    public boolean estaSobre(Player player) {
        // Aqui você pode adicionar lógica adicional se necessário
        return super.estaSobre(player);
    }
}
