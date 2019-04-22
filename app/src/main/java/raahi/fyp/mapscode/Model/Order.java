package raahi.fyp.mapscode.Model;

public class Order {
    private String rName;
    private String rAddress;
    private String ItemName;

    public Order() {
    }

    public Order(String rName, String rAddress, String itemName) {
        this.rName = rName;
        this.rAddress = rAddress;
        ItemName = itemName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public void setrAddress(String rAddress) {
        this.rAddress = rAddress;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getrName() {
        return rName;
    }

    public String getrAddress() {
        return rAddress;
    }

    public String getItemName() {
        return ItemName;
    }
}