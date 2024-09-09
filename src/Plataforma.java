import java.awt.*;

public class Plataforma extends CriaObjeto {

    public Plataforma(int x, int y, int largura, int altura, String nomeImagem) {
        super(x, y, largura, altura, nomeImagem);
    }

    // Metodo para verificar se o personagem está sobre a plataforma
    public boolean estaSobre(Player player) {
        Rectangle playerRect = player.getRect();
        Rectangle plataformaRect = this.getRect();

        // Verifica se o jogador está em cima da plataforma (simplificação)
        return playerRect.intersects(plataformaRect) && player.getY() + player.getAltura() <= this.getY() + 5;
    }

}
