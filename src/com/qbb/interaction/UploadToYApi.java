package com.qbb.interaction;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.qbb.build.BuildJsonForDubbo;
import com.qbb.build.BuildJsonForYApi;
import com.qbb.config.Config;
import com.qbb.config.ConfigEntity;
import com.qbb.config.PersistentState;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiDubboDTO;
import com.qbb.dto.YapiResponse;
import com.qbb.dto.YApiSaveParam;
import com.qbb.upload.UploadYapi;
import com.qbb.util.NotifyUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Description: UploadToYApi
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @version 1.0 2019年06月13日 15:27
 */
public class UploadToYApi extends AnAction {


    private static NotificationGroup notificationGroup;

    /**
     * 持久化的setting
     */
    private PersistentState persistentState = PersistentState.getInstance();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) {
            return;
        }
        Project project = editor.getProject();
        // 获取配置
        ConfigEntity configEntity = this.getConfigEntity(anActionEvent, project);
        // 配置校验
        if (Objects.isNull(configEntity)
                || Strings.isNullOrEmpty(configEntity.getProjectToken())
                || Strings.isNullOrEmpty(configEntity.getProjectId())
                || Strings.isNullOrEmpty(configEntity.getyApiUrl())
                || Strings.isNullOrEmpty(configEntity.getProjectType())) {
            NotifyUtil.log(notificationGroup, project, "please check config,[projectToken,projectId,yApiUrl,projectType]", NotificationType.ERROR);
            return;
        }
        // 判断项目类型
        if (ProjectTypeConstant.dubbo.equals(configEntity.getProjectType())) {
            this.dubboApiUpload(anActionEvent, project, configEntity.getProjectToken(), configEntity.getProjectId(), configEntity.getyApiUrl(), configEntity.getMenu());
        } else if (ProjectTypeConstant.api.equals(configEntity.getProjectType())) {
            this.webApiUpload(anActionEvent, project, configEntity.getProjectToken(), configEntity.getProjectId(), configEntity.getyApiUrl(), null, configEntity.getMenu());
        }
    }



    /**
     * Web api upload.
     * @param anActionEvent            the e
     * @param project      the project
     * @param projectToken the project token
     * @param projectId    the project id
     * @param yApiUrl      the yapi url
     * @param attachUpload the attach upload
     * @param menu          menu
     */
    private void webApiUpload(AnActionEvent anActionEvent, Project project, String projectToken, String projectId, String yApiUrl, String attachUpload, String menu) {
        //获得api 需上传的接口列表 参数对象
        ArrayList<YapiApiDTO> yApiApiDTOS = new BuildJsonForYApi().actionPerformedList(anActionEvent, attachUpload);
        if (Objects.nonNull(yApiApiDTOS)) {
            for (YapiApiDTO yapiApiDTO : yApiApiDTOS) {
                YApiSaveParam yapiSaveParam = new YApiSaveParam(projectToken, yapiApiDTO.getTitle(), yapiApiDTO.getPath(), yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(), Integer.valueOf(projectId), yApiUrl, true, yapiApiDTO.getMethod(), yapiApiDTO.getDesc(), yapiApiDTO.getHeader());
                yapiSaveParam.setReq_body_form(yapiApiDTO.getReq_body_form());
                yapiSaveParam.setReq_body_type(yapiApiDTO.getReq_body_type());
                yapiSaveParam.setReq_params(yapiApiDTO.getReq_params());
                if (!Strings.isNullOrEmpty(menu)) {
                    yapiSaveParam.setMenu(menu);
                } else {
                    yapiSaveParam.setMenu(YapiConstant.menu);
                }
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, attachUpload, project.getBasePath());
                    if (yapiResponse.getErrcode() != 0) {
                        NotifyUtil.log(notificationGroup, project, "sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                    } else {
                        String url = yApiUrl + "/project/" + projectId + "/interface/api/cat_" + UploadYapi.catMap.get(projectId).get(yapiSaveParam.getMenu());
                        NotifyUtil.log(notificationGroup, project, "success ,url:  " + url, NotificationType.INFORMATION);
                    }
                } catch (Exception e) {
                    NotifyUtil.log(notificationGroup, project, "sorry ,upload api error cause:" + e, NotificationType.ERROR);
                }
            }
        }
    }

    /**
     * Dubbo api upload.
     * @param anActionEvent            the e
     * @param project      the project
     * @param projectToken the project token
     * @param projectId    the project id
     * @param yapiUrl      the yapi url
     * @param menu          menu
     */
    private void dubboApiUpload(AnActionEvent anActionEvent, Project project, String projectToken, String projectId, String yapiUrl, String menu) {
        // 获得dubbo需上传的接口列表 参数对象
        ArrayList<YapiDubboDTO> yapiDubboDTOs = new BuildJsonForDubbo().actionPerformedList(anActionEvent);
        if (yapiDubboDTOs != null) {
            for (YapiDubboDTO yapiDubboDTO : yapiDubboDTOs) {
                YApiSaveParam yapiSaveParam = new YApiSaveParam(projectToken, yapiDubboDTO.getTitle(), yapiDubboDTO.getPath(), yapiDubboDTO.getParams(), yapiDubboDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, yapiDubboDTO.getDesc());
                if (!Strings.isNullOrEmpty(menu)) {
                    yapiSaveParam.setMenu(menu);
                } else {
                    yapiSaveParam.setMenu(YapiConstant.menu);
                }
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, null, project.getBasePath());
                    if (yapiResponse.getErrcode() != 0) {
                        NotifyUtil.log(notificationGroup, project, "sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                    } else {
                        String url = yapiUrl + "/project/" + projectId + "/interface/api/cat_" + UploadYapi.catMap.get(projectId);
                        NotifyUtil.log(notificationGroup, project, "success ,url: " + url, NotificationType.INFORMATION);
                    }
                } catch (Exception e) {
                    NotifyUtil.log(notificationGroup, project,  "sorry ,upload api error cause:" + e, NotificationType.ERROR);
                }
            }
        }
    }

    /**
     * 持久化的配置参数转化为object
     *
     * @param anActionEvent the an action event
     * @param project       the project
     * @return the config entity
     */
    private ConfigEntity getConfigEntity(AnActionEvent anActionEvent, Project project) {
        ConfigEntity configEntity = null;
        // 获取配置
        try {
            if (StringUtils.isBlank(persistentState.getConfig())) {
                NotifyUtil.log(notificationGroup, project,  "get config error: config is blank", NotificationType.ERROR);
            }
            Config config = gson.fromJson(persistentState.getConfig(), Config.class);
            if (config.isSingle()) {
                configEntity = config.getSingleConfig();
            } else {
                Map<String, ConfigEntity> multipleConfig = config.getMultipleConfig();
                PsiFile psiFile = anActionEvent.getDataContext().getData(CommonDataKeys.PSI_FILE);
                if (Objects.nonNull(psiFile) && StringUtils.isNotBlank(psiFile.getVirtualFile().getPath())) {
                    configEntity = multipleConfig.entrySet().stream().filter(m -> psiFile.getVirtualFile().getPath().contains(m.getKey())).map(Map.Entry::getValue).findFirst().orElse(null);
                }
            }
            return configEntity;
        } catch (Exception e) {
            NotifyUtil.log(notificationGroup, project,  "get config error:" + e.getMessage(), NotificationType.ERROR);
        }
        return null;
    }

}
