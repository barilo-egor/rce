Ext.define('Main.view.MainPanel', {
    xtype: 'mainpanel',
    extend: 'Ext.tab.Panel',
    // controller: 'mainController',
    title: 'Главное меню',
    header: {
        titleAlign: 'center'
    },
    region: 'center',
    scrollable: true,
    layout: 'fit',

    tabPosition: 'left',
    tabRotation: 0,
    tabBar: {
        border: false
    },

    defaults: {
        textAlign: 'left',
        bodyPadding: 15
    },
    items: [
        {
            title: 'Пользователи',
            iconCls: 'fa-regular fa-user',
            layout: 'fit',
            items: [
                {
                    xtype: 'panel',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            title: 'Веб пользователи',
                            collapsible: false,
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    iconCls: 'fa-solid fa-user-plus',
                                    text: 'Регистрация нового пользователя',
                                    handler: function (btn) {
                                        document.location.href='/web/registration/init'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            title: 'Переменные',
            iconCls: 'fa-solid fa-square-root-variable',
            layout: 'fit',
            items: [
                {
                    xtype: 'panel',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            title: 'Расчет суммы',
                            collapsible: false,
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    iconCls: 'fa-solid fa-dollar-sign',
                                    text: 'Курс USD',
                                    handler: function (btn) {
                                        document.location.href='/web/settings/usdCourse'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
