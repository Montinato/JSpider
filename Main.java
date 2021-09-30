import java.awt.EventQueue;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JSpider main = new JSpider();
            main.setVisible(true);
        });
    }

}
