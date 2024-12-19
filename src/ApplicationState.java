public abstract class ApplicationState {
    // Ortak bir metot, örneğin logging işlemi
    protected void log(String message) {
        System.out.println("[LOG] " + message);
    }

    // Abstract metot: Her durum bunu gerçekleştirmeli
    public abstract void handle();
}
