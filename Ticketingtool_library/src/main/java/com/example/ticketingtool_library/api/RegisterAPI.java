package com.example.ticketingtool_library.api;

import com.example.ticketingtool_library.model.LoginDetails;
import com.example.ticketingtool_library.model.TicketDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPI {
    @POST("Retrofit_loginDetails")//1
    @FormUrlEncoded
    Call<List<LoginDetails>> getData(@Field("username") String USERNAME,
                                     @Field("password") String PASSWORD);

    @POST("Retrofit_TicketDetails")//2
    @FormUrlEncoded
    Call<List<TicketDetails>> getTicketDetails(@Field("subdivcode") String SUBDIV,@Field("comp_id") String COMPANY);

    @POST("Retrofit_TicketUpdateDetails")//3
    @FormUrlEncoded
    Call<List<TicketDetails>> getUpdateDetails(@Field("ticket_id") String TIC_ID);

    @POST("Retrofit_TicketDataInsert")//4
    @FormUrlEncoded
    Call<List<TicketDetails>> insertTicket(@Field("NARRATION") String NARRATION, @Field("TIC_FILE") String TIC_FILE, @Field("TIC_GENBY") String TIC_GENBY,
                                           @Field("TIC_SUBCODE") String TIC_SUBCODE, @Field("TIC_STATUS") String TIC_STATUS, @Field("PRIORITY") String PRIORITY,
                                           @Field("TITLE") String TITLE, @Field("DESCRIPTION") String DESCRIPTION, @Field("SEVIRITY") String SEVIRITY,
                                           @Field("ASSIGN") String ASSIGN, @Field("HESCOM") String HESCOM, @Field("MR_CODE") String MR_CODE,
                                           @Field("CSD_HESCOM") String CSD_HESCOM, @Field("Encodefile") String Encodefile);

    @POST("Retrofit_TicketDataUpdate")//5
    @FormUrlEncoded
    Call<List<TicketDetails>> updateTicket(@Field("TIC_ID") String TIC_ID, @Field("NARRATION") String NARRATION, @Field("TIC_FILE") String TIC_FILE,
                                           @Field("TIC_GENBY") String TIC_GENBY, @Field("TIC_SUBCODE") String TIC_SUBCODE, @Field("TIC_STATUS") String TIC_STATUS,
                                           @Field("PRIORITY") String PRIORITY, @Field("SEVIRITY") String SEVIRITY, @Field("TITLE") String TITLE,
                                           @Field("DESCRIPTION") String DESCRIPTION, @Field("ASSIGN") String ASSIGN, @Field("HESCOM") String HESCOM,
                                           @Field("MR_CODE") String MR_CODE, @Field("COMMENT") String COMMENT, @Field("Encodefile") String Encodefile);
}