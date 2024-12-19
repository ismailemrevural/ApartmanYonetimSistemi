import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = new ApplicationContext();
                context.setState(ApplicationContext.determineState());
                context.execute();
            }
        });
    }
}