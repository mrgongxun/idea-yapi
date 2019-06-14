package com.qbb.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Description: YApiSetting
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @version 1.0 2019年06月14日 17:32 by 宫珣（gongxun@cloud-young.com）创建
 */
public class YApiSetting implements Configurable {


    private JPanel yapi;

    /**
     * 配置 json
     */
    private JTextArea config;

    /**
     * 持久化 配置
     */
    private PersistentState persistentState = PersistentState.getInstance();


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "YApiSetting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        yapi = new JPanel();
        yapi.setLayout(new GridLayoutManager(13, 3, JBUI.emptyInsets(), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("字段说明");
        yapi.add(label1, new GridConstraints(1, 0, 10, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("config");
        yapi.add(label2, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("。。。");
        yapi.add(label3, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("例子 ： ");
        yapi.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        // label5.setEnabled(false);
        label5.setText("{\"isSingle\":true,\"singleConfig\":{\"projectToken\":\"f10526011aa0231a6a7fd6b9ac09dea8896d2e0bc072524e5ba1ce61ef232503\",\"projectId\":\"82\",\"yapiUrl\":\"http://10.0.60.8:3300\",\"projectType\":\"api\"},\"multipleConfig\":{\"llb-api\":{\"projectToken\":\"\",\"projectId\":\"\",\"yapiUrl\":\"\",\"projectType\":\"\"},\"llb-admin-api\":{\"projectToken\":\"\",\"projectId\":\"\",\"yapiUrl\":\"\",\"projectType\":\"\"}}}");
        yapi.add(label5, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        config = new JTextArea();
        config.setText(StringUtils.isNotBlank(persistentState.getConfig()) ? persistentState.getConfig() : this.getDefaultConfig());
        yapi.add(config, new GridConstraints(11, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("isSingle : 是否单模块配置，true使用singleConfig，false则使用multipleConfig");
        yapi.add(label6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("项目id 获取方式 ：点击项目，查看url 中project 后面的数字为项目id http://127.0.0.1:3000/project/72/interface/api");
        yapi.add(label7, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("token 获取方式 ： 打开yapi ->具体项目->设置->token 配置");
        yapi.add(label8, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("multipleConfig ： 多模块配置， key = 项目名");
        yapi.add(label9, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("singleConfig ： 单模块配置");
        yapi.add(label10, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("projectType 填写方式： 根据你要上传的接口类型决定，如果为dubbo 接口就填dubbo ，如果是api 接口就填api ");
        yapi.add(label11, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("yapiUrl 获取方式：部署的yapi 地址");
        yapi.add(label12, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("");
        yapi.add(label13, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        return yapi;
    }

    @Override
    public boolean isModified() {
        if (StringUtils.isBlank(config.getText())) {
            return false;
        }
        if (StringUtils.isBlank(persistentState.getConfig())) {
            return true;
        }
        return persistentState.getConfig().hashCode() != config.getText().hashCode();
    }

    @Override
    public void apply() throws ConfigurationException {
        persistentState.setConfig(config.getText());
    }

    /**
     * Gets default config.
     *
     * @return the default config
     */
    private String getDefaultConfig() {
        return "{\n" +
                "  \"isSingle\": true,\n" +
                "  \"singleConfig\": {\n" +
                "    \"projectToken\": \"f10526011aa0231a6a7fd6b9ac09dea8896d2e0bc072524e5ba1ce61ef232503\",\n" +
                "    \"projectId\": \"82\",\n" +
                "    \"yapiUrl\": \"http://10.0.60.8:3300\",\n" +
                "    \"projectType\": \"api\"\n" +
                "  },\n" +
                "  \"multipleConfig\": {\n" +
                "    \"llb-api\": {\n" +
                "      \"projectToken\": \"\",\n" +
                "      \"projectId\": \"\",\n" +
                "      \"yapiUrl\": \"\",\n" +
                "      \"projectType\": \"\"\n" +
                "    },\n" +
                "    \"llb-admin-api\": {\n" +
                "      \"projectToken\": \"\",\n" +
                "      \"projectId\": \"\",\n" +
                "      \"yapiUrl\": \"\",\n" +
                "      \"projectType\": \"\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

}
