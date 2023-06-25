Ext.define('Main.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Main.view.MainPanel',
        // 'Main.view.MainController'
    ],
    alias: 'widget.mainViewport',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'mainpanel'
        }
    ]
});