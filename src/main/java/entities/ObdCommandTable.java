package entities;

public enum ObdCommandTable {
    RPM("12", "0C", "Engine RPM"),
    SPEED("13", "0D", "Vehicle Speed"),
    THROTTLE_POS("17", "11", "Throttle Position"),
    FUEL_LEVEL("47", "2F", "Fuel Level Input");

    String decimalPid;
    String hexadecimalPid;
    String description;

    ObdCommandTable(String decimalPid, String hexadecimalPid, String description) {
        this.decimalPid = decimalPid;
        this.hexadecimalPid = hexadecimalPid;
        this.description = description;
    }

    public String getDecimalPid() {
        return decimalPid;
    }

    public void setDecimalPid(String decimalPid) {
        this.decimalPid = decimalPid;
    }

    public String getHexadecimalPid() {
        return hexadecimalPid;
    }

    public void setHexadecimalPid(String hexadecimalPid) {
        this.hexadecimalPid = hexadecimalPid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
