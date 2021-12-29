package entity;

public class ToDoList {

    private int id;
    private int idUser;
    private String toDoList;

    public ToDoList() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getToDoList() {
        return toDoList;
    }

    public void setToDoList(String toDoList) {
        this.toDoList = toDoList;
    }

}
