package com.asiawaters.fta.classes;

public class Model_NewTask {
    private String IDTradePoint;
    private String Comment;
    private String IDAuthor;
    private String TheTermOfTheTask;

    public String getIDTradePoint() {
        return IDTradePoint;
    }

    public void setIDTradePoint(String IDTradePoint) {
        this.IDTradePoint = IDTradePoint;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getIDAuthor() {
        return IDAuthor;
    }

    public void setIDAuthor(String IDAuthor) {
        this.IDAuthor = IDAuthor;
    }

    public String getTheTermOfTheTask() {
        return TheTermOfTheTask;
    }

    public void setTheTermOfTheTask(String theTermOfTheTask) {
        TheTermOfTheTask = theTermOfTheTask;
    }
}
