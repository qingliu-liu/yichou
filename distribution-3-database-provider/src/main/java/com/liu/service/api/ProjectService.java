package com.liu.service.api;

import com.liu.entity.vo.ProjectVO;

public interface ProjectService {
    void saveProject(ProjectVO projectVO, String memberId);
}
