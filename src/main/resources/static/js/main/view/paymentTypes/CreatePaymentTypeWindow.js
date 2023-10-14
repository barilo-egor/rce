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
    requires: ['Main.view.paymentTypes.CreatePaymentTypeController',
        'Main.view.paymentTypes.requisites.CreateRequisiteWindow',
        'Main.view.paymentTypes.requisites.RequisiteForm'],
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
        },
        {
            xtype: 'grid',
            padding: '20 20 20 20',
            title: 'Реквизиты',
            store: {
                storeId: 'createPaymentTypeRequisitesStore',
                fields: ['name', 'requisite', 'isOn']
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    items: [
                        {
                            iconCls: 'fas fa-plus',
                            tooltip: 'Добавить реквизит',
                            handler: function (me) {
                                Ext.create('Main.view.paymentTypes.requisites.CreateRequisiteWindow')
                            }
                        }
                    ]
                }
            ],
            columns: [
                {
                    text: 'Наименование',
                    flex: 1,
                    dataIndex: 'name'
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'isOn',
                    text: '<i class="fas fa-power-off"></i>',
                    width: 35,
                    tooltip: 'Включен ли реквизит'
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    items: [
                        {
                            iconCls: 'fas fa-edit',
                            tooltip: 'Редактировать',
                            handler: function (view, rowIndex, collIndex, item, e, record) {
                                Ext.create('Main.view.paymentTypes.requisites.EditRequisiteWindow', {
                                    viewModel: {
                                        data: {
                                            requisite: record.getData(),
                                            rowIndex: rowIndex
                                        }
                                    }
                                })
                            }
                        }
                    ]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    items: [
                        {
                            iconCls: 'fas fa-minus redColor',
                            padding: '0 5 0 2',
                            tooltip: 'Удалить'
                        }
                    ],
                    handler: function (view, rowIndex, collIndex, item, e, record) {
                        Ext.Msg.show({
                            title:'Удаление реквизита',
                            message: 'Вы уверены, что хотите удалить реквизит <b>' + record.get('name') + '</b>?',
                            buttons: Ext.Msg.YESNO,
                            icon: Ext.Msg.QUESTION,
                            fn: function(btn) {
                                if (btn === 'yes') {
                                    Ext.getStore('createPaymentTypeRequisitesStore').remove(record)
                                    Ext.toast('Резквизит <b>' + record.get('name') + '</b> удален.')
                                }
                            }
                        });
                    }
                }
            ]
        }
    ]
})