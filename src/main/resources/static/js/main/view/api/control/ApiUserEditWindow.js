Ext.define('Main.view.api.control.ApiUserEditWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Main.view.components.button.SaveButton',
        'Main.view.components.button.DeleteButton',
        'Main.view.components.button.CancelButton'
    ],
    padding: '20 20 20 20',
    layout: 'fit',
    width: '95%',
    height: '95%',
    modal: true,
    title: 'Редактирование апи-пользователя',
    buttonAlign: 'center',
    controller: 'apiUsersControlController',
    buttons: [
        {
            xtype: 'savebutton',
            handler: 'save'
        },
        {
            xtype: 'deletebutton',
            handler: 'delete'
        },
        {
            xtype: 'cancelbutton',
            handler: function (btn) {
                btn.up('window').close()
            }
        }
    ],
    items: [
        {
            xtype: 'form',
            id: 'editApiUserForm',
            padding: '20 20 20 20',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                labelWidth: 120
            },
            items: [
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'pid',
                    bind: {
                        value: '{apiUser.pid}'
                    }
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Идентификатор',
                    emptyText: 'Введите идентификатор',
                    name: 'id',
                    msgTarget: 'side',
                    padding: '0 0 5 0',
                    validator: ValidatorUtil.validateId,
                    bind: {
                        value: '{apiUser.id}'
                    }
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Реквизит для покупки',
                    emptyText: 'Введите текст для покупки',
                    padding: '0 0 5 0',
                    name: 'buyRequisite',
                    msgTarget: 'side',
                    bind: {
                        value: '{apiUser.buyRequisite}'
                    },
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Реквизит для продажи',
                    emptyText: 'Введите текст для продажи',
                    padding: '0 0 5 0',
                    name: 'sellRequisite',
                    msgTarget: 'side',
                    bind: {
                        value: '{apiUser.sellRequisite}'
                    },
                    validator: ValidatorUtil.validateNotEmpty
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Персональная скидка',
                    name: 'personalDiscount',
                    emptyText: 'Введите скидку от -99 до 99',
                    value: 0,
                    decimalSeparator: '.',
                    padding: '0 0 5 0',
                    hideTrigger: true,
                    msgTarget: 'side',
                    bind: {
                        value: '{apiUser.personalDiscount}'
                    },
                    listeners: {
                        render: function (component) {
                            component.getEl().on('click', function (event, el) {
                                ExtUtil.idQuery('discountHintOuterPanel').show()
                                ExtUtil.idQuery('discountHintPanel').show()
                            });
                        },
                        focusleave: function () {
                            ExtUtil.idQuery('discountHintOuterPanel').hide()
                            ExtUtil.idQuery('discountHintPanel').hide()
                        }
                    },
                    validator: ValidatorUtil.validateDiscount
                },
                {
                    xtype: 'panel',
                    id: 'discountHintOuterPanel',
                    hidden: true,
                    layout: 'fit',
                    padding: '0 0 15 0',
                    items: [
                        {
                            xtype: 'discounthintpanel'
                        }
                    ]
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Курс USD BYN',
                    name: 'usdCourseBYN',
                    emptyText: 'Введите курс',
                    decimalSeparator: '.',
                    padding: '0 0 5 0',
                    hideTrigger: true,
                    msgTarget: 'side',
                    validator: ValidatorUtil.validatePositiveInt,
                    bind: {
                        value: '{apiUser.usdCourseBYN}'
                    },
                    hidden: true,
                    listeners: {
                        beforerender: function (me) {
                            ExtUtil.request({
                                url: '/web/api/user/isOn',
                                params: {
                                    fiatCurrency: 'BYN'
                                },
                                method: 'GET',
                                success: function (response) {
                                    if (response.body.data.result) me.show()
                                }
                            })
                        }
                    }
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: 'Курс USD RUB',
                    name: 'usdCourseRUB',
                    emptyText: 'Введите курс',
                    decimalSeparator: '.',
                    padding: '0 0 5 0',
                    hideTrigger: true,
                    msgTarget: 'side',
                    validator: ValidatorUtil.validatePositiveInt,
                    hidden: true,
                    bind: {
                        value: '{apiUser.usdCourseRUB}'
                    },
                    listeners: {
                        beforerender: function (me) {
                            ExtUtil.request({
                                url: '/web/api/user/isOn',
                                params: {
                                    fiatCurrency: 'RUB'
                                },
                                method: 'GET',
                                success: function (response) {
                                    if (response.body.data.result) me.show()
                                }
                            })
                        }
                    }
                },
                {
                    xtype: 'datefield',
                    editable: false,
                    readOnly: true,
                    hideTrigger: true,
                    fieldLabel: 'Дата регистрации',
                    name: 'registrationDate',
                    format: 'd.m.Y',
                    padding: '0 0 5 0',
                    bind: {
                        value: '{apiUser.registrationDate}'
                    }
                },
                {
                    xtype: 'checkbox',
                    id: 'isBannedCheckBox',
                    boxLabel: 'Запретить доступ к API',
                    bind: {
                        value: '{apiUser.isBanned}'
                    }
                },
                {
                    xtype: 'combobox',
                    fieldLabel: 'Фиатная валюта по умолчанию',
                    displayField: 'displayName',
                    emptyText: 'Выберите фиатную валюту',
                    valueField: 'name',
                    editable: false,
                    name: 'fiatCurrency',
                    store: 'fiatCurrenciesStore',
                    validator: ValidatorUtil.validateNotEmpty,
                    listeners: {
                        beforerender: function (me) {
                            let fiatCurrency = me.up('window').getViewModel().data.apiUser.fiatCurrency
                            let record = me.getStore().getRange().filter(currency => currency.getData().name === fiatCurrency)[0]
                            me.setValue(record)
                        }
                    }
                },
            ]
        }
    ]
})