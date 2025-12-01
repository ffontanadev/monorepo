package uy.com.bbva.services.nonbusinesses.v1.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import uy.com.bbva.dtos.commons.v1.model.IdentityDocument;

import java.util.Date;

public class NonCustomerDatatype {

    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate = null;
    private String maritalStatus;
    private IdentityDocument identityDocument;
    private String birthCountry;
    private String dischargeType;
    private String processStatus;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {  return middleName; }

    public void setMiddleName(String middleName) {  this.middleName = middleName;  }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() { return secondLastName; }

    public void setSecondLastName(String secondLastName) { this.secondLastName = secondLastName; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getBirthDate() { return birthDate; }

    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public IdentityDocument getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(IdentityDocument identityDocument) {
        this.identityDocument = identityDocument;
    }

    public String getBirthCountry() {return birthCountry;}

    public void setBirthCountry(String birthCountry) {this.birthCountry = birthCountry;}

    public String getDischargeType() {return dischargeType;}

    public void setDischargeType(String dischargeType) {this.dischargeType = dischargeType;}

    public String getProcessStatus() { return processStatus; }

    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
}