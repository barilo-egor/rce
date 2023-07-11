Ext.define('Main.view.api.control.ApiUserEditWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Main.view.components.button.SaveButton',
        'Main.view.components.button.DeleteButton'
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
            disabled: true,
            handler: 'save'
        },
        {
            xtype: 'deletebutton',
            handler: 'delete'
        }
    ],
    items: [
        {
            xtype: 'form',
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
                    xtype: 'treepanel',
                    id: 'requisitesTree',
                    renderTo: Ext.getBody(),
                    height: 200,
                    width: 300,
                    rootVisible: false,
                    store: 'requisitestreestore',
                    padding: '0 0 15 0',
                    animate: true,
                    listeners: {
                        rowclick: function (view, rowBodyEl) {
                            view.getViewRange().forEach(row => row.removeCls('boldText'))
                            if (rowBodyEl.childNodes.length === 0) rowBodyEl.addCls('boldText')
                        },
                        afterrender: function (me) {
                            me.getView().getViewRange().forEach(row => row.removeCls('boldText'))
                        }
                    },
                    columns: [
                        {
                            xtype: 'treecolumn',
                            text: 'Реквизиты покупки',
                            dataIndex: 'text',
                            flex: 1
                        }
                    ]
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
                    fieldLabel: 'Курс USD',
                    name: 'usdCourse',
                    emptyText: 'Введите курс',
                    decimalSeparator: '.',
                    padding: '0 0 5 0',
                    hideTrigger: true,
                    msgTarget: 'side',
                    validator: ValidatorUtil.validatePositiveInt,
                    bind: {
                        value: '{apiUser.usdCourse}'
                    }
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Токен',
                    padding: '0 0 5 0',
                    bind: {
                        value: '{apiUser.token}'
                    }
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Дата регистрации',
                    padding: '0 0 5 0',
                    bind: {
                        value: '{apiUser.registrationDate}'
                    }
                }
            ]
        }
    ]
})