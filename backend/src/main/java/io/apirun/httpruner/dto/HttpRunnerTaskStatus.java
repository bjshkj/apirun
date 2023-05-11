package io.apirun.httpruner.dto;

public enum HttpRunnerTaskStatus {
    CREATED, //0-创建完成
//    SENDING,//1-派发中
    SENDED,//2-已发送
    QUEUE,//3-排队中
    EXECUTING,//4-执行中、
    SUCCESS,//5-执行成功/
    FAILURE,//-执行失败/
    ERROR, //7-平台失败
    DELETE, //删除
    TIMEOUT, //超时
    DONE //执行完成
}
