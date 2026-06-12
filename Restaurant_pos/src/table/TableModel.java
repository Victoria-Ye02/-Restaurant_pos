package table;

public class TableModel {
    private int id;
    private int tableNum;
    private int seats;
    private String status; // available / ordering / bill

    public TableModel(int id, int tableNum, int seats, String status) {
        this.id       = id;
        this.tableNum = tableNum;
        this.seats    = seats;
        this.status   = status;
    }

    public int getId()         
    { return id; }
    public int getTableNum()   
    { return tableNum; }
    public int getSeats()       
    { return seats; }
    public String getStatus()  
    { return status; }
    public void setStatus(String s) 
    { this.status = s; }
}