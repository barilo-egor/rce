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
    modal: true,
    items: [
        {
            xtype: 'form',
            bodyPadding: 50,
            defaults: {
                anchor: '100%',
                labelWidth: 60
            },
            // layout: {
            //     type: 'vbox',
            //     align: 'stretch'
            // },
            buttonAlign: 'center',
            items: [
                {
                    xtype: 'numberfield',
                    name: 'sum',
                    hidden: true,
                    bind: {
                        value: '{sum}'
                    }
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Сумма',
                    name: 'newSum',
                    bind: {
                        value: '{sum}'
                    }
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Скидка',
                    name: 'percent',
                    bind: {
                        value: '{percent}'
                    }
                }
            ],
            buttons: [
                {
                    text: 'Сохранить',
                    handler: 'onSaveRecClick'
                }
            ]
        }
    ]
});