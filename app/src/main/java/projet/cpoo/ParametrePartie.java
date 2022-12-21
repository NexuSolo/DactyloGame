package projet.cpoo;

public class ParametrePartie {
    private boolean accent;
    private String langue;

    public ParametrePartie(boolean accent, String langue) {
        this.accent = accent;
        this.langue = langue;
    }

    public boolean isAccent() {
        return accent;
    }

    public String getLangue() {
        return langue;
    } 
    
}
