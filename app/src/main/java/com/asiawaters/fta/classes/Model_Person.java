package com.asiawaters.fta.classes;

public class Model_Person {
    private String person_guid;
    private String person_name;
    private String person_surname;
    private String person_midname;
    private String person_position;
    private String person_organisation;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPerson_organisation() {
        return person_organisation;
    }

    public void setPerson_organisation(String person_organisation) {
        this.person_organisation = person_organisation;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_surname() {
        return person_surname;
    }

    public void setPerson_surname(String person_surname) {
        this.person_surname = person_surname;
    }

    public String getPerson_midname() {
        return person_midname;
    }

    public void setPerson_midname(String person_midname) {
        this.person_midname = person_midname;
    }

    public String getPerson_position() {
        return person_position;
    }

    public void setPerson_position(String person_position) {
        this.person_position = person_position;
    }

    public String getPerson_guid() {
        return person_guid;
    }

    public void setPerson_guid(String person_guid) {
        this.person_guid = person_guid;
    }

    @Override
    public String toString() {
        return "Model_Person{" +
                "person_guid='" + person_guid + '\'' +
                ", person_name='" + person_name + '\'' +
                ", person_surname='" + person_surname + '\'' +
                ", person_midname='" + person_midname + '\'' +
                ", person_position='" + person_position + '\'' +
                ", person_organisation='" + person_organisation + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
