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
                    name: 'clientId',
                    msgTarget: 'side',
                    padding: '0 0 5 0',
                    validator: function (val) {
                        if (!val) return 'Введите значение'
                        if (!RegexUtil.onlyLettersAndNumbers(val)) return 'Только латинские буквы и символы'
                        return true
                    }
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
                    validator: function (val) {
                        if (!val) return 'Введите значение.'
                        if (val < -99 || val > 99) {
                            return 'Значение должно быть от -99 до 99.'
                        } else return true
                    }
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
                            html: '<i class="fas fa-info-circle" style="color: #005eff;"></i> ' +
                                'Введите положительное значение для скидки, либо отрицательное для надбавки.'
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
                    validator: function (val) {
                        if (!val) return 'Введите значение'
                        return true
                    }
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
                    validator: function (val) {
                        if (!val) return 'Введите значение.'
                        if (val === '0' || val === 0 || val < 1) {
                            return 'Значение должно больше 0.'
                        } else return true
                    }
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