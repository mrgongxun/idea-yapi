package com.qbb.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


/**
 * Description: 持久化
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @version 1.0 2019年06月14日 15:02 by 宫珣（gongxun@cloud-young.com）创建
 */
@State(name = "YApiConfig", storages = {@com.intellij.openapi.components.Storage(file = "$APP_CONFIG$/YApiConfig.xml")})
public class PersistentState implements PersistentStateComponent<Element> {

    private String config;

    /**
     * 服务管理器获取实例
     *
     * @return PersistentState instance
     */
    public static PersistentState getInstance() {
        return ServiceManager.getService(PersistentState.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("YApiConfig");
        element.setAttribute("config", this.getConfig());
        return element;
    }

    @Override
    public void loadState(Element element) {
        this.config = element.getAttributeValue("config");
    }


    /**
     * Gets config.
     *
     * @return the config
     */
    public String getConfig() {
        return Objects.isNull(config) ? "" : config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public void setConfig(String config) {
        this.config = config;
    }
}
