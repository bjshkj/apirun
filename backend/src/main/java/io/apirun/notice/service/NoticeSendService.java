package io.apirun.notice.service;

import com.alibaba.nacos.client.utils.StringUtils;
import io.apirun.commons.constants.NoticeConstants;
import io.apirun.commons.utils.LogUtil;
import io.apirun.notice.domain.MessageDetail;
import io.apirun.notice.sender.NoticeModel;
import io.apirun.notice.sender.NoticeSender;
import io.apirun.notice.sender.impl.DingNoticeSender;
import io.apirun.notice.sender.impl.LarkNoticeSender;
import io.apirun.notice.sender.impl.MailNoticeSender;
import io.apirun.notice.sender.impl.WeComNoticeSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class NoticeSendService {
    @Resource
    private MailNoticeSender mailNoticeSender;
    @Resource
    private WeComNoticeSender weComNoticeSender;
    @Resource
    private DingNoticeSender dingNoticeSender;
    @Resource
    private LarkNoticeSender larkNoticeSender;
    @Resource
    private NoticeService noticeService;

    private NoticeSender getNoticeSender(MessageDetail messageDetail) {
        NoticeSender noticeSender = null;
        switch (messageDetail.getType()) {
            case NoticeConstants.Type.EMAIL:
                noticeSender = mailNoticeSender;
                break;
            case NoticeConstants.Type.WECHAT_ROBOT:
                noticeSender = weComNoticeSender;
                break;
            case NoticeConstants.Type.NAIL_ROBOT:
                noticeSender = dingNoticeSender;
                break;
            case NoticeConstants.Type.LARK:
                noticeSender = larkNoticeSender;
            default:
                break;
        }

        return noticeSender;
    }

    public void send(String taskType, NoticeModel noticeModel) {
        String loadReportId = (String) noticeModel.getParamMap().get("id");
        try {
            List<MessageDetail> messageDetails;
            switch (taskType) {
                case NoticeConstants.Mode.API:
                    messageDetails = noticeService.searchMessageByTypeBySend(NoticeConstants.TaskType.JENKINS_TASK, loadReportId);
                    break;
                case NoticeConstants.Mode.SCHEDULE:
                    messageDetails = noticeService.searchMessageByTestId(noticeModel.getTestId());
                    break;
                default:
                    messageDetails = noticeService.searchMessageByType(taskType);
                    break;
            }
            messageDetails.forEach(messageDetail -> {
                if (StringUtils.equals(messageDetail.getEvent(), noticeModel.getEvent())) {
                    this.getNoticeSender(messageDetail).send(messageDetail, noticeModel);
                }
            });
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
    }
}
