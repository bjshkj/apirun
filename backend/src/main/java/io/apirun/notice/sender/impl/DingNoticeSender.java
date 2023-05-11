package io.apirun.notice.sender.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.notice.domain.MessageDetail;
import io.apirun.notice.sender.AbstractNoticeSender;
import io.apirun.notice.sender.NoticeModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DingNoticeSender extends AbstractNoticeSender {

    public void sendNailRobot(MessageDetail messageDetail, String context) {
        List<String> userIds = messageDetail.getUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        DingTalkClient client = new DefaultDingTalkClient(messageDetail.getWebhook());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(context);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        List<String> phoneList = super.getUserPhones(userIds);
        LogUtil.info("收件人地址: " + phoneList);
        at.setAtMobiles(phoneList);
        request.setAt(at);
        try {
            client.execute(request);
        } catch (ApiException e) {
            LogUtil.error(e.getMessage(), e);
        }
    }

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        sendNailRobot(messageDetail, context);
    }
}
