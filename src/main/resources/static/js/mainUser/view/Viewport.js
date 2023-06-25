Ext.define('MainUser.view.Viewport', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.mainViewport',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'panel',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'container',
                    html: 'У пользователя пока что нет никаких прав.',
                    padding: '20 20 20 20'
                },
                {
                    xtype: 'button',
                    text: 'Выйти из аккаунта',
                    handler: function (btn) {
                        document.location.href = '/logout'
                    }
                }
            ]

        }
    ]
});