Ext.define('Main.view.bulkDiscount.BulkDiscountAddForm', {
    extend: 'Ext.window.Window',
    alias: 'widget.bulkdiscountaddform',
    controller: 'bulkDiscountController',
    title: 'Добавление скидки',
    width: '50%',
    layout: {
        type: 'fit'
    },
    viewModel: true,
    modal: true,
    items: [
        {
            xtype: 'form',
            bodyPadding: 10,
            defaults: {
                anchor: '100%',
                labelWidth: 60
            },
            buttonAlign: 'center',
            items: [
                {
                    xtype: 'numberfield',
                    id: 'oldSum',
                    name: 'oldSum',
                    hidden: true,
                    bind: {
                        value: '{oldSum}'
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'fiatCurrency',
                    name: 'fiatCurrency',
                    hidden: true,
                    bind: {
                        value: '{fiatCurrency}'
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'dealType',
                    name: 'dealType',
                    hidden: true,
                    bind: {
                        value: '{dealType}'
                    }
                },
                {
                    xtype: 'numberfield',
                    id: 'sum',
                    fieldLabel: 'Сумма',
                    name: 'sum',
                    hideTrigger: true,
                    decimalSeparator: '.',
                    bind: {
                        value: '{sum}'
                    }
                },
                {
                    xtype: 'numberfield',
                    id: 'percent',
                    fieldLabel: 'Скидка',
                    name: 'percent',
                    hideTrigger: true,
                    decimalSeparator: '.',
                    bind: {
                        value: '{percent}'
                    }
                }
            ],
            buttons: [
                {
                    text: 'Сохранить',
                    handler: 'onSaveClick'
                }
            ]
        }
    ]
});