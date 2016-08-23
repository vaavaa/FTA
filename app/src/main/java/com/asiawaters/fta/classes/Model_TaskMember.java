package com.asiawaters.fta.classes;

import java.util.Arrays;
import java.util.Date;

public class Model_TaskMember {

    private Date DateOfExecutionPlan;

    private Date DateOfExecutionFact;
    private Date DateOfCommencementPlan;
    private Date DateOfCommencementFact;

    private String InitiatorBP;
    private String mComment;

    private String Director;
    private String Event;
    private String StateTask;
    private String Status;
    private String Events;
    private Model_TaskListFields[] mTaskListFields;
    private Model_TaskListFields[] mTaskListImages;

    public Model_TaskListFields[] getmTaskListImages() {
        return mTaskListImages;
    }

    public void setmTaskListImages(Model_TaskListFields[] mTaskListImages) {
        this.mTaskListImages = mTaskListImages;
    }


    public Model_TaskListFields[] getmTaskListFields() {
        return mTaskListFields;
    }

    public void setmTaskListFields(Model_TaskListFields[] mTaskListFields) {
        this.mTaskListFields = mTaskListFields;
    }

    public Date getDateOfExecutionPlan() {
        return DateOfExecutionPlan;
    }

    public void setDateOfExecutionPlan(Date dateOfExecutionPlan) {
        DateOfExecutionPlan = dateOfExecutionPlan;
    }

    public Date getDateOfExecutionFact() {
        return DateOfExecutionFact;
    }

    public void setDateOfExecutionFact(Date dateOfExecutionFact) {
        DateOfExecutionFact = dateOfExecutionFact;
    }

    public Date getDateOfCommencementPlan() {
        return DateOfCommencementPlan;
    }

    public void setDateOfCommencementPlan(Date dateOfCommencementPlan) {
        DateOfCommencementPlan = dateOfCommencementPlan;
    }

    public Date getDateOfCommencementFact() {
        return DateOfCommencementFact;
    }

    public void setDateOfCommencementFact(Date dateOfCommencementFact) {
        DateOfCommencementFact = dateOfCommencementFact;
    }

    public String getInitiatorBP() {
        return InitiatorBP;
    }

    public void setInitiatorBP(String initiatorBP) {
        InitiatorBP = initiatorBP;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getStateTask() {
        return StateTask;
    }

    public void setStateTask(String stateTask) {
        StateTask = stateTask;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getEvents() {
        return Events;
    }

    public void setEvents(String events) {
        Events = events;
    }

    @Override
    public String toString() {
        return "Model_TaskMember{" +
                "DateOfExecutionPlan=" + DateOfExecutionPlan +
                ", DateOfExecutionFact=" + DateOfExecutionFact +
                ", DateOfCommencementPlan=" + DateOfCommencementPlan +
                ", DateOfCommencementFact=" + DateOfCommencementFact +
                ", InitiatorBP='" + InitiatorBP + '\'' +
                ", mComment='" + mComment + '\'' +
                ", Director='" + Director + '\'' +
                ", Event='" + Event + '\'' +
                ", StateTask='" + StateTask + '\'' +
                ", Status='" + Status + '\'' +
                ", Events='" + Events + '\'' +
                ", mTaskListFields=" + Arrays.toString(mTaskListFields) +
                '}';
    }
}
