package com.asiawaters.fta;

import android.app.Application;
import android.graphics.Bitmap;

import com.asiawaters.fta.classes.DBController;
import com.asiawaters.fta.classes.ModelRegions;
import com.asiawaters.fta.classes.Model_ListMembers;
import com.asiawaters.fta.classes.Model_NetState;
import com.asiawaters.fta.classes.Model_NewTask;
import com.asiawaters.fta.classes.Model_Person;
import com.asiawaters.fta.classes.Model_TaskMember;
import com.asiawaters.fta.classes.NetListener;

import java.util.Date;

public class FTA extends Application {
    private Model_Person person;
    private Model_ListMembers listMember;
    private NetListener mnetListener = new NetListener();
    private Model_NetState model_netState = new Model_NetState();
    private Model_ListMembers[] list_values;
    private String path_url = "http://ws.asiawaters.com/ast2/ws/Mobile";
    private DBController db;
    private int timeOut = 80000;
    private Date DateFrom;
    private Date DateTo;
    private Boolean UpdateList = false;
    private String user;
    private String password;
    private String TaskGuid;
    private ModelRegions[] ModelRegionsArray;
    private Model_TaskMember TaskMember;
    private Model_NewTask MNT;
    private Bitmap ImageToShow;

    public Bitmap getImageToShow() {
        return ImageToShow;
    }

    public void setImageToShow(Bitmap imageToShow) {
        ImageToShow = imageToShow;
    }

    public Model_NewTask getMNT() {
        return MNT;
    }

    public void setMNT(Model_NewTask MNT) {
        this.MNT = MNT;
    }

    public Model_TaskMember getTaskMember() {
        return TaskMember;
    }

    public void setTaskMember(Model_TaskMember taskMember) {
        TaskMember = taskMember;
    }

    public ModelRegions[] getModelRegionsArray() {
        return ModelRegionsArray;
    }

    public void setModelRegionsArray(ModelRegions[] modelRegionsArray) {
        ModelRegionsArray = modelRegionsArray;
    }

    public String getTaskGuid() {
        return TaskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        TaskGuid = taskGuid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUpdateList() {
        return UpdateList;
    }

    public void setUpdateList(Boolean updateList) {
        UpdateList = updateList;
    }

    public Date getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        DateFrom = dateFrom;
    }

    public Date getDateTo() {
        return DateTo;
    }

    public void setDateTo(Date dateTo) {
        DateTo = dateTo;
    }

    public DBController getDb() {
        return db;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setDb(DBController db) {
        this.db = db;
    }

    public Model_ListMembers getListMember() {
        return listMember;
    }

    public void setListMembers(Model_ListMembers listMember) {
        this.listMember = listMember;
    }

    public Model_Person getPerson() {
        return person;
    }

    public void setPerson(Model_Person person) {
        this.person = person;
    }

    public NetListener getMnetListener() {
        return mnetListener;
    }

    public void setMnetListener(NetListener mnetListener) {
        this.mnetListener = mnetListener;
    }

    public Model_NetState getModel_netState() {
        return model_netState;
    }

    public void setModel_netState(Model_NetState model_netState) {
        this.model_netState = model_netState;
    }

    public String getPath_url() {
        return path_url;
    }

    public void setPath_url(String path_url) {
        this.path_url = path_url;
    }

    public Model_ListMembers[] getList_values() {
        return list_values;
    }

    public void setList_values(Model_ListMembers[] list_values) {
        this.list_values = list_values;
    }
}
