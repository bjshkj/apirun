package io.apirun.track.request.testcase;

import io.apirun.base.domain.FileMetadata;
import io.apirun.base.domain.TestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditTestCaseRequest extends TestCaseWithBLOBs {
    private List<FileMetadata> updatedFileList;
    /**
     * 复制测试用例后，要进行复制的文件Id list
     */
    private List<String> fileIds = new ArrayList<>();
    private List<List<String>> selected = new ArrayList<>();
}
