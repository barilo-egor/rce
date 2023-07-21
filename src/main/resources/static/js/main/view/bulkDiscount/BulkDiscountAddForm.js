Ext.define('Main.view.bulkDiscount.BulkDiscountAddForm', {
    extend: 'Ext.window.Window',
    alias: 'widget.bulkdiscountaddform',
    controller: 'bulkDiscountController',
    width: 680,
    height: 350,
    layout: {
        type: 'fit'
    },
    viewModel: true,
    items: [
        {
            xtype: 'form',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            buttonAlign: 'center',
            items: [
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Сумма',
                    name: 'sum',
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Скидка',
                    name: 'percent',
                }
            ],
            buttons: [
                {
                    text: 'Сохранить',
                    iconCls: 'fas fa-save greenBtn',
                    cls: 'greenBtn',
                    handler: 'onSaveRecClick'
                }
            ]
        }
    ]
});