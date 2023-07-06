Ext.define('Main.view.api.registration.ApiRegistrationPanel', {
    xtype: 'apiregistrationpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'apiRegistrationController',
    requires: [
        'Main.view.components.button.RegisterButton'
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
                        render: function( component ) {
                            component.getEl().on('click', function( event, el ) {
                                Ext.ComponentQuery.query('[id=personalDiscountHintPanel]')[0].show()
                            });
                        },
                        focusleave: function () {
                            Ext.ComponentQuery.query('[id=personalDiscountHintPanel]')[0].hide()
                        }
                    },
                    validator: ValidatorUtil.validateDiscount
                },
                {
                    xtype: 'panel',
                    id: 'personalDiscountHintPanel',
                    hidden: true,
                    layout: 'fit',
                    padding: '0 0 15 0',
                    items: [
                        {
                            xtype: 'panel',
                            frame: true,
                            padding: '5 5 5 5',
                            style: {
                                borderColor: '#919191',
                                borderWidth: '1px',
                                textAlign: 'center'
                            },
                            html: HtmlConstants.personalDiscountInfo
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Реквизиты',
                    emptyText: 'Введите реквизиты',
                    name: 'requisites',
                    msgTarget: 'side',
                    padding: '0 0 5 0',
                    validator: ValidatorUtil.validateNotEmpty
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