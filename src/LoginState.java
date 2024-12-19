public class LoginState extends ApplicationState {
    @Override
    public void handle() {
        log("LoginState handle metodu çağrıldı.");
        LoginFrame.display();
    }
}
