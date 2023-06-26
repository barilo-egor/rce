Ext.define('Main.view.components.MainToolBar', {
    extend: 'Ext.toolbar.Toolbar',
    xtype: 'maintoolbar',
    dock: 'left',
    id: 'mainToolBar',
    padding: '0 0 0 0',
    items: [
        {
            xtype: 'button',
            iconCls: 'fas fa-square-root-alt',
            menu: [
                {
                    text: 'Курс доллара',
                    iconCls: 'fas fa-dollar-sign',
                    handler: 'usdCourseClick'
                }
            ]
        }
    ]
})