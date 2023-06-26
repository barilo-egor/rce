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
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'panel',
            header: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            dockedItems: [
                {
                    xtype: 'maintoolbar'
                },
            ],
            items: [
                {
                    xtype: 'maintitle'
                },
                {
                    xtype: 'mainframepanel'
                }
            ]
        }
    ]
});
