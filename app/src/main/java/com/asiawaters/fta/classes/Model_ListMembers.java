package com.asiawaters.fta.classes;

import java.util.Date;

public class Model_ListMembers {
    private String NumberOfTask;
    private Date AppointmentDateOfTask;
    private String TaskName;
    private Date TargetDatesForTheTask;
    private String GuidTask;
    private boolean Active;
    private String Template;
    private boolean ActiveBP;

    public boolean isActiveBP() {
        return ActiveBP;
    }

    public void setActiveBP(boolean activeBP) {
        ActiveBP = activeBP;
    }

    public String getTemplate() {
        return Template;
    }

    public void setTemplate(String template) {
        Template = template;
    }

    public String getNumberOfTask() {
        return NumberOfTask;
    }

    public void setNumberOfTask(String numberOfTask) {
        NumberOfTask = numberOfTask;
    }

    public Date getAppointmentDateOfTask() {
        return AppointmentDateOfTask;
    }

    public void setAppointmentDateOfTask(Date appointmentDateOfTask) {
        AppointmentDateOfTask = appointmentDateOfTask;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public Date getTargetDatesForTheTask() {
        return TargetDatesForTheTask;
    }

    public void setTargetDatesForTheTask(Date targetDatesForTheTask) {
        TargetDatesForTheTask = targetDatesForTheTask;
    }

    public String getGuidTask() {
        return GuidTask;
    }

    public void setGuidTask(String guidTask) {
        GuidTask = guidTask;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    @Override
    public String toString() {
        return "ListMembers{" +
                "NumberOfTask='" + NumberOfTask + '\'' +
                ", AppointmentDateOfTask=" + AppointmentDateOfTask +
                ", TaskName='" + TaskName + '\'' +
                ", TargetDatesForTheTask=" + TargetDatesForTheTask +
                ", GuidTask='" + GuidTask + '\'' +
                ", Active=" + Active +
                '}';
    }
}
