public class Table {
    private int id; // Identifiant unique de la table
    private int tableNumber; // Numéro de la table
    private int capacity; // Capacité de la table (nombre de personnes)
    private String status; // Statut de la table (Available, Reserved, Occupied)
    private String client; // Nom du client assigné à la table

    // Constructeur
    public Table(int tableNumber, int capacity, String status, String client) {
        this.id = tableNumber; // Utilisation du numéro de table comme ID pour simplifier
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Méthodes pour gérer le client
    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }
}