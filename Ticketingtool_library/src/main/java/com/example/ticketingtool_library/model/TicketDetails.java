package com.example.ticketingtool_library.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketDetails implements Serializable {
    public String getTIC_ID() {
        return TIC_ID;
    }

    public void setTIC_ID(String TIC_ID) {
        this.TIC_ID = TIC_ID;
    }

    public String getTIC_NARR() {
        return TIC_NARR;
    }

    public void setTIC_NARR(String TIC_NARR) {
        this.TIC_NARR = TIC_NARR;
    }

    public String getTIC_FILE() {
        return TIC_FILE;
    }

    public void setTIC_FILE(String TIC_FILE) {
        this.TIC_FILE = TIC_FILE;
    }

    public String getTIC_GENBY() {
        return TIC_GENBY;
    }

    public void setTIC_GENBY(String TIC_GENBY) {
        this.TIC_GENBY = TIC_GENBY;
    }

    public String getTIC_GENON() {
        return TIC_GENON;
    }

    public void setTIC_GENON(String TIC_GENON) {
        this.TIC_GENON = TIC_GENON;
    }

    public String getTIC_STATUS() {
        return TIC_STATUS;
    }

    public void setTIC_STATUS(String TIC_STATUS) {
        this.TIC_STATUS = TIC_STATUS;
    }

    public String getCLOSED_ON() {
        return CLOSED_ON;
    }

    public void setCLOSED_ON(String CLOSED_ON) {
        this.CLOSED_ON = CLOSED_ON;
    }

    public String getTIC_SUBCODE() {
        return TIC_SUBCODE;
    }

    public void setTIC_SUBCODE(String TIC_SUBCODE) {
        this.TIC_SUBCODE = TIC_SUBCODE;
    }

    public String getTIC_CLEARON() {
        return TIC_CLEARON;
    }

    public void setTIC_CLEARON(String TIC_CLEARON) {
        this.TIC_CLEARON = TIC_CLEARON;
    }

    public String getPRIORITY() {
        return PRIORITY;
    }

    public void setPRIORITY(String PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public String getASSIGN() {
        return ASSIGN;
    }

    public void setASSIGN(String ASSIGN) {
        this.ASSIGN = ASSIGN;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getSEVIRITY() {
        return SEVIRITY;
    }

    public void setSEVIRITY(String SEVIRITY) {
        this.SEVIRITY = SEVIRITY;
    }

    public String getASSIGNED_BY() {
        return ASSIGNED_BY;
    }

    public void setASSIGNED_BY(String ASSIGNED_BY) {
        this.ASSIGNED_BY = ASSIGNED_BY;
    }

    public String getHESCOM() {
        return HESCOM;
    }

    public void setHESCOM(String HESCOM) {
        this.HESCOM = HESCOM;
    }

    public String getCSD_HESCOM() {
        return CSD_HESCOM;
    }

    public void setCSD_HESCOM(String CSD_HESCOM) {
        this.CSD_HESCOM = CSD_HESCOM;
    }

    public String getMR_CODE() {
        return MR_CODE;
    }

    public void setMR_CODE(String MR_CODE) {
        this.MR_CODE = MR_CODE;
    }

    public String getCOMMENT() {
        return COMMENT;
    }

    public void setCOMMENT(String COMMENT) {
        this.COMMENT = COMMENT;
    }

    @SerializedName("TIC_ID")
    @Expose
    private String TIC_ID;
    @SerializedName("TIC_NARR")
    @Expose
    private String TIC_NARR;
    @SerializedName("TIC_FILE")
    @Expose
    private String TIC_FILE;
    @SerializedName("TIC_GENBY")
    @Expose
    private String TIC_GENBY;
    @SerializedName("TIC_GENON")
    @Expose
    private String TIC_GENON;
    @SerializedName("TIC_STATUS")
    @Expose
    private String TIC_STATUS;
    @SerializedName("CLOSED_ON")
    @Expose
    private String CLOSED_ON;
    @SerializedName("TIC_SUBCODE")
    @Expose
    private String TIC_SUBCODE;
    @SerializedName("TIC_CLEARON")
    @Expose
    private String TIC_CLEARON;
    @SerializedName("PRIORITY")
    @Expose
    private String PRIORITY;
    @SerializedName("ASSIGN")
    @Expose
    private String ASSIGN;
    @SerializedName("TITLE")
    @Expose
    private String TITLE;
    @SerializedName("DESCRIPTION")
    @Expose
    private String DESCRIPTION;
    @SerializedName("SEVIRITY")
    @Expose
    private String SEVIRITY;
    @SerializedName("ASSIGNED_BY")
    @Expose
    private String ASSIGNED_BY;
    @SerializedName("HESCOM")
    @Expose
    private String HESCOM;
    @SerializedName("CSD_HESCOM")
    @Expose
    private String CSD_HESCOM;
    @SerializedName("MR_CODE")
    @Expose
    private String MR_CODE;
    @SerializedName("COMMENT")
    @Expose
    private String COMMENT;

    public String getUSER_ROLE() {
        return USER_ROLE;
    }

    public void setUSER_ROLE(String USER_ROLE) {
        this.USER_ROLE = USER_ROLE;
    }

    private String USER_ROLE;


    public int getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(int COUNT) {
        this.COUNT = COUNT;
    }

    @SerializedName("COUNT")
    @Expose
    private int COUNT;

}
