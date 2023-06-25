Ext.define('Rce.components.MainPageButton', {
    extend: 'Ext.button.Button',
    xtype: 'mainpagebutton',
    text: 'На главную',
    iconCls: 'fa-solid fa-house',
    handler: function (btn) {
        document.location.href = '/web/main'
    }
})