Ext.define('Main.view.apiregistration.ApiRegistrationPanel', {
    xtype: 'apiregistrationpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'usdCourseController',
    title: {
        xtype: 'mainframetitle',
        text: 'Регистрация апи-пользователя'
    },
    scrollable: true,
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'textfield',
                    labelField: 'field'
                }
            ]
        }
    ]
})