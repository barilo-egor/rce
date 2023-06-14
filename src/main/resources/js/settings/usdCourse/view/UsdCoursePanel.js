Ext.define('UsdCourse.view.BcdpConfiguratorPanel', {
    xtype: 'usdcoursepanel',
    extend: 'Ext.form.Panel',
    alias: 'widget.UsdCourse-panel',
    title: 'Замена курса USD',
    header: {
        titleAlign: 'center'
    },
    bodyStyle: 'padding:5px 5px',
    region: 'center',
    layout: {
        type: 'vbox',
        align: 'center'
    },
    listeners: {
        afterrender: 'onPanelRendered'
    },
    items: [
        {
            xtype: 'form',
            title: 'Форма авторизации',
            width: 300,
            height: 200,
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Логин',
                    name: 'login',
                    labelAlign: 'top',
                    cls: 'field-margin',
                    flex: 1
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Пароль',
                    name: 'password',
                    labelAlign: 'top',
                    cls: 'field-margin',
                    flex: 1
                }
            ],
            buttons: [
                {
                    text: 'Оправить',
                    handler: function () {
                        // действие отправки
                    }
                },
                {
                    text: 'Отмена',
                    handler: function () {
                        // действие отмены
                    }
                }],
        }
    ]
});
