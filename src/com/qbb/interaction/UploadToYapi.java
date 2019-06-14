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
import com.qbb.build.BuildJsonForYapi;
import com.qbb.config.Config;
import com.qbb.config.ConfigEntity;
import com.qbb.config.PersistentState;
import com.qbb.constant.ProjectTypeConstant;
import com.qbb.constant.YapiConstant;
import com.qbb.dto.YapiApiDTO;
import com.qbb.dto.YapiDubboDTO;
import com.qbb.dto.YapiResponse;
import com.qbb.dto.YapiSaveParam;
import com.qbb.upload.UploadYapi;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Description: UploadToYapi
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @version 1.0 2019年06月13日 15:27 by 宫珣（gongxun@cloud-young.com）创建
 */
public class UploadToYapi extends AnAction {


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
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) {
            return;
        }
        Project project = editor.getProject();
        String projectToken = null;
        String projectId = null;
        String yApiUrl = null;
        String projectType = null;
        String attachUpload = null;
        // 获取配置
        try {
            if (StringUtils.isBlank(persistentState.getConfig())) {
                Notification error = notificationGroup.createNotification("get config error: config is blank", NotificationType.ERROR);
                Notifications.Bus.notify(error, project);
            }
            Config config = gson.fromJson(persistentState.getConfig(), Config.class);
            if (config.isSingle()) {
                ConfigEntity singleConfig = config.getSingleConfig();
                projectToken = singleConfig.getProjectToken();
                projectId = singleConfig.getProjectId();
                yApiUrl = singleConfig.getYapiUrl();
                projectType = singleConfig.getProjectType();
            } else {
                Map<String, ConfigEntity> multipleConfig = config.getMultipleConfig();
                PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
                if (Objects.nonNull(psiFile)) {
                    String virtualFile = psiFile.getVirtualFile().getPath();
                    ConfigEntity configEntity = multipleConfig.entrySet().stream()
                            .filter(m -> virtualFile.contains(m.getKey()))
                            .map(Map.Entry::getValue)
                            .findFirst().orElse(null);
                    if (Objects.nonNull(configEntity)) {
                        projectToken = configEntity.getProjectToken();
                        projectId = configEntity.getProjectId();
                        yApiUrl = configEntity.getYapiUrl();
                        projectType = configEntity.getProjectType();
                    }
                }
            }
        } catch (Exception e2) {
            Notification error = notificationGroup.createNotification("get config error:" + e2.getMessage(), NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return;
        }
        // 配置校验
        if (Strings.isNullOrEmpty(projectToken) || Strings.isNullOrEmpty(projectId) || Strings.isNullOrEmpty(yApiUrl) || Strings.isNullOrEmpty(projectType)) {
            Notification error = notificationGroup.createNotification("please check config,[projectToken,projectId,yapiUrl,projectType]", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return;
        }
        // 判断项目类型
        if (ProjectTypeConstant.dubbo.equals(projectType)) {
            this.dubboApiUpload(e, project, projectToken, projectId, yApiUrl);
        } else if (ProjectTypeConstant.api.equals(projectType)) {
            this.webApiUpload(e, project, projectToken, projectId, yApiUrl, attachUpload);
        }
    }

    /**
     * Web api upload.
     *
     * @param e            the e
     * @param project      the project
     * @param projectToken the project token
     * @param projectId    the project id
     * @param yapiUrl      the yapi url
     * @param attachUpload the attach upload
     */
    private void webApiUpload(AnActionEvent e, Project project, String projectToken, String projectId, String yapiUrl, String attachUpload) {
        //获得api 需上传的接口列表 参数对象
        ArrayList<YapiApiDTO> yapiApiDTOS = new BuildJsonForYapi().actionPerformedList(e, attachUpload);
        if (Objects.nonNull(yapiApiDTOS)) {
            for (YapiApiDTO yapiApiDTO : yapiApiDTOS) {
                YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, yapiApiDTO.getTitle(), yapiApiDTO.getPath(), yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, true, yapiApiDTO.getMethod(), yapiApiDTO.getDesc(), yapiApiDTO.getHeader());
                yapiSaveParam.setReq_body_form(yapiApiDTO.getReq_body_form());
                yapiSaveParam.setReq_body_type(yapiApiDTO.getReq_body_type());
                yapiSaveParam.setReq_params(yapiApiDTO.getReq_params());
//                if (!Strings.isNullOrEmpty(yapiApiDTO.getMenu())) {
//                    yapiSaveParam.setMenu(yapiApiDTO.getMenu());
//                } else {
                    yapiSaveParam.setMenu(YapiConstant.menu);
//                }
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, attachUpload, project.getBasePath());
                    if (yapiResponse.getErrcode() != 0) {
                        Notification error = notificationGroup.createNotification("sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                        Notifications.Bus.notify(error, project);
                    } else {
                        String url = yapiUrl + "/project/" + projectId + "/interface/api/cat_" + UploadYapi.catMap.get(projectId).get(yapiSaveParam.getMenu());
                        Notification error = notificationGroup.createNotification("success ,url:  " + url, NotificationType.INFORMATION);
                        Notifications.Bus.notify(error, project);
                    }
                } catch (Exception e1) {
                    Notification error = notificationGroup.createNotification("sorry ,upload api error cause:" + e1, NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                }
            }
        }
    }

    /**
     * Dubbo api upload.
     *
     * @param e            the e
     * @param project      the project
     * @param projectToken the project token
     * @param projectId    the project id
     * @param yapiUrl      the yapi url
     */
    private void dubboApiUpload(AnActionEvent e, Project project, String projectToken, String projectId, String yapiUrl) {
        // 获得dubbo需上传的接口列表 参数对象
        ArrayList<YapiDubboDTO> yapiDubboDTOs = new BuildJsonForDubbo().actionPerformedList(e);
        if (yapiDubboDTOs != null) {
            for (YapiDubboDTO yapiDubboDTO : yapiDubboDTOs) {
                YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, yapiDubboDTO.getTitle(), yapiDubboDTO.getPath(), yapiDubboDTO.getParams(), yapiDubboDTO.getResponse(), Integer.valueOf(projectId), yapiUrl, yapiDubboDTO.getDesc());
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, null, project.getBasePath());
                    if (yapiResponse.getErrcode() != 0) {
                        Notification error = notificationGroup.createNotification("sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                        Notifications.Bus.notify(error, project);
                    } else {
                        String url = yapiUrl + "/project/" + projectId + "/interface/api/cat_" + UploadYapi.catMap.get(projectId);
                        Notification error = notificationGroup.createNotification("success ,url: " + url, NotificationType.INFORMATION);
                        Notifications.Bus.notify(error, project);
                    }
                } catch (Exception e1) {
                    Notification error = notificationGroup.createNotification("sorry ,upload api error cause:" + e1, NotificationType.ERROR);
                    Notifications.Bus.notify(error, project);
                }
            }
        }
    }
}
