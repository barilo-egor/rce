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
    requires: ['Main.view.paymentTypes.CreatePaymentTypeController'],
    controller: 'createPaymentTypeController',

    buttonAlign: 'center',
    buttons: [
        {
            xtype: 'savebutton',
            handler: 'save'
        },
        {
            xtype: 'cancelbutton',
            handler: ExtUtil.closeWindow
        }
    ],
    items: [
        {
            xtype: 'form',
            id: 'paymentTypeCreateForm',
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
                    emptyText: 'Введите название',
                    name: 'name',
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'checkbox',
                    id: 'isOnCheckbox',
                    fieldLabel: 'Включить',
                    name: 'isOn',
                    inputValue: true,
                    uncheckedValue: false
                },
                {
                    xtype: 'combo',
                    fieldLabel: 'Фиатная валюта',
                    emptyText: 'Выберите фиатную валюту',
                    valueField: 'name',
                    displayField: 'displayName',
                    store: 'fiatCurrenciesStore',
                    name: 'fiatCurrency',
                    editable: false,
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'combo',
                    fieldLabel: 'Тип сделки',
                    emptyText: 'Покупка или продажа',
                    valueField: 'name',
                    displayField: 'nominative',
                    name: 'dealType',
                    store: {
                        storeId: 'dealTypesStore',
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
                    },
                    editable: false,
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Минимальная сумма',
                    emptyText: 'Минимальная сумма покупки',
                    hideTrigger: true,
                    value: 0,
                    decimalSeparator: '.',
                    name: 'minSum',
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: 'Динамические реквизиты',
                    name: 'isDynamicOn',
                    inputValue: true,
                    uncheckedValue: false
                }
            ]
        }
    ]
})