package com.psy.util;

/**
 * Created by ppssyyy on 2016-08-05.
 */
public class URL {
    public static final String IP_AND_PORT = "http://119.29.245.167:80/";
    //public static final String IP_AND_PORT = "http://115.159.124.204/";

    public static  final String LOGIN_URL =
            IP_AND_PORT+"PoseCamera/UserController/findUidByloginName.do";
    public static  final String FIND_U_BY_UID_URL =
            IP_AND_PORT+"PoseCamera/UserController/findUserById.do";
    public static  final String REG_URL =
            IP_AND_PORT+"PoseCamera/UserController/insertUser.do";
    public static  final String UPDATE_PW_BY_NAME_URL =
            IP_AND_PORT+ "PoseCamera/UserController/updateUserPwByUname.do";
    public static  final String UPDATE_PB_BY_UID_URL =
            IP_AND_PORT+"PoseCamera/UserController/updateUserPbById.do";
    public static  final String UPDATE_PIC_BY_NAME_URL =
            IP_AND_PORT+"PoseCamera/UserController/updateUserPicByUname.do";
    public static  final String FIND_ALL_USERS_URL =
            IP_AND_PORT+"PoseCamera/UserController/findAllUsers.do";


    public static  final String UPDATE_PB_BY_PID =
            IP_AND_PORT+"PoseCamera/PosLibController/updatePbByPid.do";
    public static  final String FIND_ALL_POS_URL =
            IP_AND_PORT+"PoseCamera/PosLibController/findAllPos.do";
    public static  final String FIND_POS_BY_ID_UID =
            IP_AND_PORT+"PoseCamera/PosLibController/findPosByUid.do";
    public static  final String FIND_POS_BY_ID_PID =
            IP_AND_PORT+"PoseCamera/PosLibController/findPoseByPid.do";
    public static  final String DEL_POS_BY_PID =
            IP_AND_PORT+"PoseCamera/PosLibController/delPosByPid.do";
    public static  final String FIND_POS_BY_ID_TID =
            IP_AND_PORT+"PoseCamera/PosLibController/findPosByTid.do";
    public static  final String INSERT_POS =
            IP_AND_PORT+"PoseCamera/PosLibController/insertPos.do";
    public static  final String INSERT_TAG =
            IP_AND_PORT+"PoseCamera/PosLibController/insertTag.do";
    public static  final String FIND_POS_BY_TAG =
            IP_AND_PORT+"PoseCamera/PosLibController/findPosByTag.do";
    public static  final String FIND_POS_BY_TYPE12 =
            IP_AND_PORT+"PoseCamera/PosLibController/findPosByType12.do";
    public static  final String FIND_POS_BY_TYPE_NOT12 =
            IP_AND_PORT+"PoseCamera/PosLibController/findPosByTypeNot12.do";
    public static  final String GET_RESULT =
            IP_AND_PORT+"PoseCamera/PosLibController/getResult.do";
    public static  final String FIND_ALL_TAGS =
            IP_AND_PORT+"PoseCamera/PosLibController/findAllTags.do";
}
