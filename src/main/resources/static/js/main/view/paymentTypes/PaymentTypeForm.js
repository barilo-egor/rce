Ext.define('Main.view.paymentTypes.PaymentTypeForm', {
    extend: 'Ext.form.Panel',
    xtype: 'paymenttypeform',
    id: 'paymentTypeForm',
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
            xtype: 'numberfield',
            name: 'pid',
            hidden: true,
            bind: {
                value: '{paymentType.pid}'
            }
        },
        {
            xtype: 'textfield',
            fieldLabel: 'Название',
            emptyText: 'Введите название',
            name: 'name',
            validator: ValidatorUtil.validateNotEmpty,
            bind: {
                value: '{paymentType.name}'
            }
        },
        {
            xtype: 'checkbox',
            id: 'isOnCheckbox',
            fieldLabel: 'Включить',
            name: 'isOn',
            inputValue: true,
            uncheckedValue: false,
            bind: {
                value: '{paymentType.isOn}'
            }
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
            validator: ValidatorUtil.validateNotEmpty,
            listeners: {
                afterrender: function (me) {
                    let viewModel = me.up('window').getViewModel().getData()
                    if (!viewModel.paymentType) return
                    me.setValue(me.getStore().findRecord('name', viewModel.paymentType.fiatCurrency))
                }
            }
        },
        {
            xtype: 'combo',
            fieldLabel: 'Тип сделки',
            emptyText: 'Покупка или продажа',
            valueField: 'name',
            displayField: 'nominative',
            name: 'dealType',
            store: 'dealTypesStore',
            editable: false,
            validator: ValidatorUtil.validateNotEmpty,
            listeners: {
                afterrender: function (me) {
                    let viewModel = me.up('window').getViewModel().getData()
                    if (!viewModel.paymentType) return
                    me.setValue(me.getStore().findRecord('name', viewModel.paymentType.dealType))
                }
            }
        },
        {
            xtype: 'numberfield',
            fieldLabel: 'Минимальная сумма',
            emptyText: 'Минимальная сумма покупки',
            hideTrigger: true,
            value: 0,
            decimalSeparator: '.',
            name: 'minSum',
            validator: ValidatorUtil.validateNotEmpty,
            bind: {
                value: '{paymentType.minSum}'
            }
        },
        {
            xtype: 'checkbox',
            id: 'isDynamicOnField',
            fieldLabel: 'Динамические реквизиты',
            name: 'isDynamicOn',
            inputValue: true,
            uncheckedValue: false,
            readOnly: true,
            bind: {
                value: '{paymentType.isDynamicOn}'
            }
        },
        {
            xtype: 'panel',
            frame: true,
            padding: '5 5 5 5',
            style: {
                borderColor: '#919191',
                borderWidth: '1px',
                textAlign: 'center'
            },
            html: '<i class="fas fa-info-circle" style="color: #005eff;"></i> ' +
                'Динамические реквизиты будут автоматически включены при включении нескольких реквизитов.'
        }
    ]
})