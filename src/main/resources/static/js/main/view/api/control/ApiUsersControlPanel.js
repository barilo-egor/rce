Ext.define('Main.view.api.control.ApiUsersControlPanel', {
    xtype: 'apiuserscontrolpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'apiUsersControlController',
    title: {
        xtype: 'mainframetitle',
        text: 'Управление апи-пользователями'
    },
    scrollable: true,
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'container',
            padding: '20 20 20 20',
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'qwe'
                }
            ]
        }
    ]
})