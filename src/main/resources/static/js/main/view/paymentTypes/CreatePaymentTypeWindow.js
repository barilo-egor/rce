Ext.define('Main.view.paymentTypes.CreatePaymentTypeWindow', {
    extend: 'Ext.window.Window',
    title: 'Создание типа оплаты',
    width: '95%',
    height: '95%',
    modal: true,
    autoShow: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'form',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            padding: '20 20 20 20',
            defaults: {
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Название',
                    emptyText: 'Краткое название'
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Реквизит',
                    emptyText: 'Текст для пользователя'
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: 'Включить'
                },
                {
                    xtype: 'combo',
                    fieldLabel: 'Тип сделки',
                    valueField: 'name',
                    displayField: 'nominative',
                    store: {
                        fields: ['name', 'nominative'],
                        autoLoad: true,
                        proxy: {
                            type: 'ajax',
                            url: '/web/enum/dealTypes',
                            reader: {
                                type: 'json',
                                rootProperty: 'body.data'
                            }
                        }
                    }
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Минимальная сумма'
                }
            ]
        }
    ]
})