Ext.define('Main.view.api.registration.ApiRegistrationPanel', {
    xtype: 'apiregistrationpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'apiRegistrationController',
    requires: [
        'Main.view.components.button.RegisterButton',
        'Main.view.components.panel.DiscountHintPanel'
    ],
    title: {
        xtype: 'mainframetitle',
        text: 'Регистрация апи-пользователя'
    },
    scrollable: true,
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            id: 'apiuserregisteform',
            padding: '20 20 20 20',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                labelWidth: 150
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Идентификатор',
                    emptyText: 'Введите идентификатор',
                    name: 'id',
                    msgTarget: 'side',
                    padding: '0 0 5 0',
                    validator: ValidatorUtil.validateId
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
                    xtype: 'numberfield',
                    fieldLabel: 'Персональная скидка',
                    name: 'personalDiscount',
                    emptyText: 'Введите скидку от -99 до 99',
                    value: 0,
                    decimalSeparator: '.',
                    padding: '0 0 5 0',
                    hideTrigger: true,
                    msgTarget: 'side',
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
                    validator: ValidatorUtil.validatePositiveInt
                }
            ]
        },
        {
            xtype: 'panel',
            buttonAlign: 'center',
            buttons: [
                {
                    xtype: 'registerbutton',
                    handler: 'register'
                }
            ]
        }
    ]
})