Ext.define('Ext.form.RcePanel', {
    xtype: 'rcepanel',
    extend: 'Ext.form.Panel',
    tbar: [
        {
            xtype: 'button',
            text: 'На главную',
            iconCls: 'fa-solid fa-house',
            handler: function (btn) {
                document.location.href = '/web/main'
            }
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'button',
            text: 'Выйти из аккаунта',
            iconCls: 'fa-solid fa-right-from-bracket',
            handler: function (btn) {
                document.location.href = '/logout'
            }
        },
    ],
})