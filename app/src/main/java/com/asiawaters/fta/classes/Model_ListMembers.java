package com.asiawaters.fta.classes;

import java.util.Date;

public class Model_ListMembers {
    private String OutletName;
    private String OutletAddress;
    private String OutletAgent;
    private Date AppointmentDateOfTask;
    private Date InitialDateOfTask;
    private Date Deadline;
    private String IDTask;
    private String TextProblem;
    private boolean done;
    private String GUIDTT;

    public String getGUIDTT() {
        return GUIDTT;
    }

    public void setGUIDTT(String GUIDTT) {
        this.GUIDTT = GUIDTT;
    }

    public String getOutletName() {
        return OutletName;
    }

    public void setOutletName(String outletName) {
        OutletName = outletName;
    }

    public String getOutletAddress() {
        return OutletAddress;
    }

    public void setOutletAddress(String outletAddress) {
        OutletAddress = outletAddress;
    }

    public String getOutletAgent() {
        return OutletAgent;
    }

    public void setOutletAgent(String outletAgent) {
        OutletAgent = outletAgent;
    }

    public Date getAppointmentDateOfTask() {
        return AppointmentDateOfTask;
    }

    public void setAppointmentDateOfTask(Date appointmentDateOfTask) {
        AppointmentDateOfTask = appointmentDateOfTask;
    }

    public Date getInitialDateOfTask() {
        return InitialDateOfTask;
    }

    public void setInitialDateOfTask(Date initialDateOfTask) {
        InitialDateOfTask = initialDateOfTask;
    }

    public Date getDeadline() {
        return Deadline;
    }

    public void setDeadline(Date deadline) {
        Deadline = deadline;
    }

    public String getIDTask() {
        return IDTask;
    }

    public void setIDTask(String IDTask) {
        this.IDTask = IDTask;
    }

    public String getTextProblem() {
        return TextProblem;
    }

    public void setTextProblem(String textProblem) {
        TextProblem = textProblem;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Model_ListMembers{" +
                "OutletName='" + OutletName + '\'' +
                ", OutletAddress='" + OutletAddress + '\'' +
                ", OutletAgent='" + OutletAgent + '\'' +
                ", AppointmentDateOfTask=" + AppointmentDateOfTask +
                ", InitialDateOfTask=" + InitialDateOfTask +
                ", Deadline=" + Deadline +
                ", IDTask='" + IDTask + '\'' +
                ", TextProblem='" + TextProblem + '\'' +
                ", done=" + done +
                '}';
    }
}