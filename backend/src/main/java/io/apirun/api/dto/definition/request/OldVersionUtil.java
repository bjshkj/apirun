package io.apirun.api.dto.definition.request;


import io.apirun.commons.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OldVersionUtil {

    public static void transferHashTree(List<MsTestElement> hashTree) {
        for (int i = 0; i < hashTree.size(); i++) {
            MsTestElement element = hashTree.get(i);
            if (StringUtils.isBlank(element.getProjectId())) {
                element.setProjectId(SessionUtils.getCurrentProjectId());
            }
            if (element.getHashTree() != null && element.getHashTree().size() > 0) {
                transferHashTree(element.getHashTree());
            }
        }
    }

}
