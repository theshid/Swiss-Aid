package com.shid.swissaid.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "draft")
public class Draft {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "draftId")
    private int draftId;

    @ColumnInfo(name = "nameProject")
    private String nameProjectStep;

    @ColumnInfo(name = "duration")
    private String durationStep;

    @ColumnInfo(name = "numberProject")
    private String numberProject;

    @ColumnInfo(name = "q1")
    private String q1Step;

    @ColumnInfo(name = "q2")
    private String q2Step;

    @ColumnInfo(name = "q3")
    private String q3Step;

    @ColumnInfo(name = "q4")
    private String q4Step;

    @ColumnInfo(name = "q5")
    private String q5Step;

    @ColumnInfo(name = "q6")
    private String q6Step;

    @ColumnInfo(name = "q7")
    private String q7Step;

    @ColumnInfo(name = "q8")
    private String q8Step;

    @ColumnInfo(name = "q9")
    private String q9Step;

    @ColumnInfo(name = "q10")
    private String q10Step;

    @ColumnInfo(name = "q11")
    private String q11Step;

    @ColumnInfo(name = "q12")
    private String q12Step;

    @ColumnInfo(name = "q13")
    private String q13Step;








    public Draft(int draftId, String nameProjectStep, String durationStep, String numberProject, String q1Step, String q2Step, String q3Step, String q4Step, String q5Step, String q6Step, String q7Step, String q8Step, String q9Step, String q10Step, String q11Step, String q12Step, String q13Step) {
        this.draftId = draftId;
        this.nameProjectStep = nameProjectStep;
        this.durationStep = durationStep;
        this.numberProject = numberProject;
        this.q1Step = q1Step;
        this.q2Step = q2Step;
        this.q3Step = q3Step;
        this.q4Step = q4Step;
        this.q5Step = q5Step;
        this.q6Step = q6Step;
        this.q7Step = q7Step;
        this.q8Step = q8Step;
        this.q9Step = q9Step;
        this.q10Step = q10Step;
        this.q11Step = q11Step;
        this.q12Step = q12Step;
        this.q13Step = q13Step;
    }



    public int getDraftId() {
        return draftId;
    }

    public void setDraftId(int draftId) {
        this.draftId = draftId;
    }

    public String getNameProjectStep() {
        return nameProjectStep;
    }

    public void setNameProjectStep(String nameProjectStep) {
        this.nameProjectStep = nameProjectStep;
    }

    public String getDurationStep() {
        return durationStep;
    }

    public void setDurationStep(String durationStep) {
        this.durationStep = durationStep;
    }

    public String getNumberProject() {
        return numberProject;
    }

    public void setNumberProject(String numberProject) {
        this.numberProject = numberProject;
    }

    public String getQ1Step() {
        return q1Step;
    }

    public void setQ1Step(String q1Step) {
        this.q1Step = q1Step;
    }

    public String getQ2Step() {
        return q2Step;
    }

    public void setQ2Step(String q2Step) {
        this.q2Step = q2Step;
    }

    public String getQ3Step() {
        return q3Step;
    }

    public void setQ3Step(String q3Step) {
        this.q3Step = q3Step;
    }

    public String getQ4Step() {
        return q4Step;
    }

    public void setQ4Step(String q4Step) {
        this.q4Step = q4Step;
    }

    public String getQ5Step() {
        return q5Step;
    }

    public void setQ5Step(String q5Step) {
        this.q5Step = q5Step;
    }

    public String getQ6Step() {
        return q6Step;
    }

    public void setQ6Step(String q6Step) {
        this.q6Step = q6Step;
    }

    public String getQ7Step() {
        return q7Step;
    }

    public void setQ7Step(String q7Step) {
        this.q7Step = q7Step;
    }

    public String getQ8Step() {
        return q8Step;
    }

    public void setQ8Step(String q8Step) {
        this.q8Step = q8Step;
    }

    public String getQ9Step() {
        return q9Step;
    }

    public void setQ9Step(String q9Step) {
        this.q9Step = q9Step;
    }

    public String getQ10Step() {
        return q10Step;
    }

    public void setQ10Step(String q10Step) {
        this.q10Step = q10Step;
    }

    public String getQ11Step() {
        return q11Step;
    }

    public void setQ11Step(String q11Step) {
        this.q11Step = q11Step;
    }

    public String getQ12Step() {
        return q12Step;
    }

    public void setQ12Step(String q12Step) {
        this.q12Step = q12Step;
    }

    public String getQ13Step() {
        return q13Step;
    }

    public void setQ13Step(String q13Step) {
        this.q13Step = q13Step;
    }
}
