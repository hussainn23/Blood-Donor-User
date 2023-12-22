package com.khushi.blooddonors.Models;

public class ModelUser {



    String donorName;
    String donorCNIC;
    String donorDOB;
    String donorEmail;
    String donorAddress;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
       this.status = status;
    }

    public ModelUser(String donorName, String donorCNIC, String donorDOB, String donorEmail, String donorAddress, String donorBloddGroup, String status, String img, String donorID, String donorPassword) {
        this.donorName = donorName;
        this.donorCNIC = donorCNIC;
        this.donorDOB = donorDOB;
        this.donorEmail = donorEmail;
        this.donorAddress = donorAddress;
        this.donorBloddGroup = donorBloddGroup;
        this.status = status;
        this.img = img;
        this.donorID = donorID;
        this.donorPassword = donorPassword;
    }

    String donorBloddGroup;
    String status;

    String img;
    String donorID;

    public String getDonorID() {
        return donorID;
    }

    public void setDonorID(String donorID) {
        this.donorID = donorID;
    }

    public ModelUser(String donorName, String donorCNIC, String donorDOB, String donorEmail, String donorAddress, String donorBloddGroup, String img, String donorID, String donorPassword) {
        this.donorName = donorName;
        this.donorCNIC = donorCNIC;
        this.donorDOB = donorDOB;
        this.donorEmail = donorEmail;
        this.donorAddress = donorAddress;
        this.donorBloddGroup = donorBloddGroup;
        this.img = img;
        this.donorID = donorID;
        this.donorPassword = donorPassword;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public ModelUser(String donorName, String donorCNIC, String donorDOB, String donorEmail, String donorAddress, String donorBloddGroup, String img, String donorPassword) {
        this.donorName = donorName;
        this.donorCNIC = donorCNIC;
        this.donorDOB = donorDOB;
        this.donorEmail = donorEmail;
        this.donorAddress = donorAddress;
        this.donorBloddGroup = donorBloddGroup;
        this.img = img;
        this.donorPassword = donorPassword;
    }

    public ModelUser() {
    }

    public ModelUser(String donorName, String donorCNIC, String donorDOB, String donorEmail, String donorAddress, String donorBloddGroup, String donorPassword) {
        this.donorName = donorName;
        this.donorCNIC = donorCNIC;
        this.donorDOB = donorDOB;
        this.donorEmail = donorEmail;
        this.donorAddress = donorAddress;
        this.donorBloddGroup = donorBloddGroup;
        this.donorPassword = donorPassword;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorCNIC() {
        return donorCNIC;
    }

    public void setDonorCNIC(String donorCNIC) {
        this.donorCNIC = donorCNIC;
    }

    public String getDonorDOB() {
        return donorDOB;
    }

    public void setDonorDOB(String donorDOB) {
        this.donorDOB = donorDOB;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String donorAddress) {
        this.donorAddress = donorAddress;
    }

    public String getDonorBloddGroup() {
        return donorBloddGroup;
    }

    public void setDonorBloddGroup(String donorBloddGroup) {
        this.donorBloddGroup = donorBloddGroup;
    }

    public String getDonorPassword() {
        return donorPassword;
    }

    public void setDonorPassword(String donorPassword) {
        this.donorPassword = donorPassword;
    }

    String donorPassword;
}