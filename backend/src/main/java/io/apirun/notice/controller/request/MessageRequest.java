package io.apirun.notice.controller.request;

import io.apirun.notice.domain.MessageDetail;
import lombok.Data;

import java.util.List;

@Data
public class MessageRequest {
    private List<MessageDetail> messageDetail;
}
