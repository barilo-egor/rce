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
            iconCls: 'fas fa-square-root-alt menu-icon-color',
            menu: [
                {
                    text: 'Курс доллара',
                    iconCls: 'fas fa-dollar-sign menu-icon-color',
                    handler: 'usdCourseClick'
                },
                {
                    text: 'Оптовые скидки',
                    iconCls: 'fas fa-tag',
                    handler: 'bulkDiscountClick'
                },
            ],
        },
        {
            xtype: 'button',
            iconCls: 'fas fa-users menu-icon-color',
            menu: [
                {
                    text: 'Регистрация веб-пользователей',
                    iconCls: 'fas fa-user-plus menu-icon-color',
                    handler: 'newWebUserClick'
                },
                {
                    text: 'Регистрация апи-пользователей',
                    iconCls: 'fas fa-user-plus menu-icon-color',
                    handler: 'newWebUserClick'
                }
            ]
        }
    ]
})