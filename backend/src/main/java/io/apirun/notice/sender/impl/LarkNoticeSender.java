package io.apirun.notice.sender.impl;

import io.apirun.notice.domain.MessageDetail;
import io.apirun.notice.sender.AbstractNoticeSender;
import io.apirun.notice.sender.NoticeModel;
import io.apirun.notice.util.LarkClient;
import org.springframework.stereotype.Component;

@Component
public class LarkNoticeSender extends AbstractNoticeSender {

    public void sendLark(MessageDetail messageDetail, String context) {
        //TextMessage message = new TextMessage(context);
        LarkClient.send(messageDetail.getWebhook(), context);
    }

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        sendLark(messageDetail, context);
    }
}
