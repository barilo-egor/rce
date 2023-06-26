Ext.define('Main.view.components.MainToolBar', {
    extend: 'Ext.toolbar.Toolbar',
    xtype: 'maintoolbar',
    dock: 'left',
    id: 'mainToolBar',
    padding: '0 0 0 1',
    cls: 'main-toolbar',
    items: [
        {
            xtype: 'button',
            iconCls: 'fas fa-square-root-alt variables-icon-color',
            menu: [
                {
                    text: 'Курс доллара',
                    iconCls: 'fas fa-dollar-sign usd-course-icon-color',
                    handler: 'usdCourseClick'
                },
            ],
        },
        {
            xtype: 'button',
            iconCls: 'fas fa-users users-icon-color',
            menu: [
                {
                    text: 'Регистрация веб-пользователей',
                    iconCls: 'fas fa-user-plus new-web-user-icon-color',
                    handler: 'newWebUserClick'
                }
            ]
        }
    ]
})