Ext.define('Main.view.components.MainTitle', {
        extend: 'Ext.container.Container',
        xtype: 'maintitle',
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        height: 40,
        items: [
            {
                xtype: 'container',
                flex: 0.33,
                style: {
                    backgroundColor: '#5fa2dd',
                },
                items: [
                    {
                        xtype: 'button',
                        iconCls: 'fas fa-bars',
                        handler: 'collapse',
                        width: 40,
                        height: 40
                    },
                ]
            },
            {
                xtype: 'container',
                flex: 0.33,
                layout: {
                    type: 'vbox',
                    align: 'center',
                    pack: 'center'
                },
                cls: 'main-panel-header-title',
                items: [
                    {
                        xtype: 'container',
                        html: 'Админ панель',
                    }
                ]
            },
            {
                xtype: 'container',
                style: {
                    backgroundColor: '#5fa2dd',
                },
                html: '',
                flex: 0.33
            }
        ]
    }
)