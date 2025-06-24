import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Movimento {
    private GameWindow janela;
    private double velocidadeVertical = 0; // Velocidade vertical do player
    private double gravidade = 0.5; // Força da gravidade
    private double salto = -15; // Força do salto
    private boolean noAr = true; // Indica se o player está no ar
    private boolean saltando = false; // Verifica se o salto está ativo

    //private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    //private volatile boolean running = false;

    public void movimentoX(CriaObjeto objeto, int velocidade) {
        objeto.x += velocidade;
    }

    public void movimentoY(CriaObjeto objeto, int velocidade) {
        objeto.y += velocidade;
    }

    public void goX(CriaObjeto objeto, int newX) {
        objeto.setX(newX);
    }

    public void goY(CriaObjeto objeto, int newY) {
        objeto.setY(newY);
    }

    public void goPosicao(CriaObjeto objeto, int newX, int newY) {
        objeto.setX(newX);
        objeto.setY(newY);
    }

    public void goPosicaoAleatoria(CriaObjeto objeto, GameWindow janela) {
        int larguraTela = janela.getWidth();
        int alturaTela = janela.getHeight();

        int xAleatorio = (int) (Math.random() * (larguraTela));
        int yAleatorio = (int) (Math.random() * (alturaTela));

        objeto.x = xAleatorio;
        objeto.y = yAleatorio;
    }

    // Metodo para atualizar a posição do jogador com base na gravidade
    public void aplicarGravidade(Player player, Plataforma chao) {
        // Se o player estiver no ar, aplica a gravidade
        if (player.isNoAr()) {
            player.setVelocidadeVertical(player.getVelocidadeVertical() + gravidade);
            player.setY(player.getY() + (int) player.getVelocidadeVertical());
        }

        // Verifica se o player colidiu com a plataforma (chão)
        if (verificarColisao(player, chao)) {
            player.setY(chao.getY() - player.getAltura()); // Coloca o player no topo da plataforma
            player.setNoAr(false);
            player.setVelocidadeVertical(0); // Para a queda
        } else {
            player.setNoAr(true);
        }
    }

    // Metodo para iniciar o salto
    public void iniciarSalto(Player player) {
        if (!player.isNoAr()) { // Só permite saltar se estiver no chão
            player.setVelocidadeVertical(salto); // Aplica a força do salto
            player.setNoAr(true);
            player.setSaltando(true); // Indica que o salto começou
        }
    }

    // Metodo para controlar a força do salto (salto controlado)
    public void controlarSalto(Player player) {
        if (player.isSaltando()) {
            // Se a tecla de salto ainda estiver pressionada, diminui a velocidade da queda
            if (player.getVelocidadeVertical() < 0) {
                player.setVelocidadeVertical(player.getVelocidadeVertical() + 0.5); // Ajuste para controlar a altura do salto
            } else {
                player.setSaltando(false); // Termina o controle do salto quando a velocidade começar a aumentar
            }
        }
    }

    // Verifica a colisão entre o player e a plataforma (chão)
    public boolean verificarColisao(CriaObjeto player, Plataforma chao) {
        return player.getY() + player.getAltura() >= chao.getY();
    }

    public void girarDireita(CriaObjeto objeto, double graus) {
        objeto.girarDireita(graus);
    }

    public void girarEsquerda(CriaObjeto objeto, double graus) {
        objeto.girarEsquerda(graus);
    }

    public void apontarDirecao(CriaObjeto objeto, double angulo) {
        objeto.apontarDirecao(angulo);
    }

    // Atualiza a física de movimento do jogador
    public void atualizarFisica(PlayerIA player, Plataforma chao) {
        if (player.isNoAr()) {
            // Aplica a gravidade no movimento vertical do jogador
            player.setVelocidadeVertical(player.getVelocidadeVertical() + gravidade);
            player.setY(player.getY() + (int) player.getVelocidadeVertical());
        }

        // Verifica a colisão com o chão e ajusta a posição do jogador
        if (verificarColisao(player, chao)) {
            player.setY(chao.getY() - player.getAltura()); // Ajusta para o topo da plataforma
            player.setNoAr(false); // Player não está mais no ar
            player.setVelocidadeVertical(0); // Reseta a velocidade vertical
        } else {
            player.setNoAr(true); // Se não colidiu, está no ar
        }
    }

    // Inicia o salto do jogador, se não estiver no ar
    public void iniciarSalto(PlayerIA player) {
        if (!player.isNoAr()) { // Só pode pular se estiver no chão
            player.setVelocidadeVertical(salto); // Aplica a força de salto
            player.setNoAr(true); // O player agora está no ar
            player.setSaltando(true); // Marca o player como saltando
        }
    }

    // Controla a física do salto, permitindo saltos mais altos se a tecla continuar pressionada
    public void controlarSalto(PlayerIA player) {
        if (player.isSaltando()) {
            // Se o salto ainda estiver no ar, ajusta a gravidade para tornar o salto mais controlado
            if (player.getVelocidadeVertical() < 0) {
                player.setVelocidadeVertical(player.getVelocidadeVertical() + 0.5); // Diminui a velocidade da queda
            } else {
                player.setSaltando(false); // Finaliza o salto quando a velocidade vertical é positiva
            }
        }
    }

    // Verifica a colisão entre o player e o chão
    public boolean verificarColisao(PlayerIA player, Plataforma chao) {
        return player.getY() + player.getAltura() >= chao.getY(); // Verifica se o player atingiu a plataforma
    }}