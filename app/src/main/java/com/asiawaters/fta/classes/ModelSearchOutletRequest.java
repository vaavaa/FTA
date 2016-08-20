package com.asiawaters.fta.classes;

public class ModelSearchOutletRequest {
    private String Word;
    private String Count;
    private String GUIDOrganization;
    private int ListCode;
    //Если код 1 - значит выпадающий список
    //Если код 2 - значит общий список

    public int getListCode() {
        return ListCode;
    }

    public void setListCode(int listCode) {
        ListCode = listCode;
    }

    public String getWord() {
        return Word;
    }

    public void setWord(String word) {
        Word = word;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getGUIDOrganization() {
        return GUIDOrganization;
    }

    public void setGUIDOrganization(String GUIDOrganization) {
        this.GUIDOrganization = GUIDOrganization;
    }

    @Override
    public String toString() {
        return GUIDOrganization;
    }
}
