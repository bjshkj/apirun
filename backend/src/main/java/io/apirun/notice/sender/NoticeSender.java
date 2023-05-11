package io.apirun.notice.sender;

import io.apirun.notice.domain.MessageDetail;
import org.springframework.scheduling.annotation.Async;

public interface NoticeSender {
    @Async
    void send(MessageDetail messageDetail, NoticeModel noticeModel);
}
