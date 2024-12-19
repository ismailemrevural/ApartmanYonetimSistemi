public class ApartmentCreationState extends ApplicationState {
    @Override
    public void handle() {
        log("ApartmentCreationState handle metodu çağrıldı.");
        ApartmentCreationFrame.display();
    }
}

