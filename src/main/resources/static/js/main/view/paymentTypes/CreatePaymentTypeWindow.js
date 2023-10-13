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

    buttonAlign: 'center',
    buttons: [
        {
            xtype: 'savebutton',
        },
        {
            xtype: 'cancelbutton'
        }
    ],
    items: [
        {
            xtype: 'form',
            layout: {
                type: 'vbox',
                align: 'stretch',
            },
            defaults: {
                labelWidth: 170,
                labelAlign: 'right'
            },
            padding: '20 20 20 20',
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
                    fieldLabel: 'Фиатная валюта',
                    emptyText: 'Выберите фиатную валюту',
                    valueField: 'name',
                    displayField: 'displayName',
                    store: 'fiatCurrenciesStore'
                },
                {
                    xtype: 'combo',
                    fieldLabel: 'Тип сделки',
                    emptyText: 'Покупка или продажа',
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
                    fieldLabel: 'Минимальная сумма',
                    emptyText: 'Минимальная сумма покупки',
                    hideTrigger: true,
                    value: 0,
                    decimalSeparator: '.'
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: 'Динамические реквизиты'
                }
            ]
        }
    ]
})