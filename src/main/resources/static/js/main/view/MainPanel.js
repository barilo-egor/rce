Ext.define('Main.view.MainPanel', {
    xtype: 'mainpanel',
    extend: 'Ext.form.Panel',
    id: 'mainPanel',
    controller: 'mainController',
    requires: [
        'Main.view.components.MainTitle',
        'Main.view.components.MainToolBar',
        'Main.view.components.MainFramePanel'
    ],
    header: false,
    layout: {
        type: 'fit'
    },

    items: [
        {
            xtype: 'panel',
            header: false,
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'maintitle'
                },
                {
                    xtype: 'mainframepanel',
                    dockedItems: [
                        {
                            xtype: 'maintoolbar'
                        },
                    ]
                }
            ]
        }
    ]
});
