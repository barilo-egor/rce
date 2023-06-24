Ext.define('Login.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Login.view.LoginPanel'
    ],
    alias: 'widget.loginViewport',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'loginpanel'
        }
    ]
});