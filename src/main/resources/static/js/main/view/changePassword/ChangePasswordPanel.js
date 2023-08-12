Ext.define('Main.view.changePassword.ChangePasswordPanel', {
    xtype: 'changepasswordpanel',
    extend: 'Main.view.components.FramePanel',
    requires: [
        'Main.view.components.button.SaveButton',
        'Main.view.components.button.EyeButton',
        'Main.view.changePassword.ChangePasswordController'
    ],
    controller: 'changePasswordController',

    layout: {
        type: 'vbox',
        align : 'stretch'
    },
    title: {
        xtype: 'mainframetitle',
        text: 'Смена пароля'
    },

    items: [
        {
            xtype: 'panel',
            layout: {
                type: 'vbox',
                align : 'center'
            },
            defaults: {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'center'
                },
                padding: '0 0 10 0',
                defaults: {
                    xtype: 'textfield',
                    labelWidth: 130,
                    labelAlign: 'right',
                    msgTarget: 'side',
                    minLength: 8,
                    inputType: 'password',
                    emptyText: 'Введите пароль'
                }
            },
            buttonAlign: 'center',
            buttons: [
                {
                    xtype: 'savebutton',
                    handler: 'saveButtonClick'
                }
            ],
            items: [
                {
                    items: [
                        {
                            id: 'passwordInput',
                            fieldLabel: 'Новый пароль',
                            name: 'password',
                            validator: ValidatorUtil.validateNotEmptyAndLettersAndNumber
                        },
                        {
                            xtype: 'eyebutton',
                            handler: 'viewPasswordClick'
                        }
                    ]
                },
                {
                    items: [
                        {
                            fieldLabel: 'Повторите пароль',
                            validator: ValidatorUtil.validatePasswordConfirm
                        },
                        {
                            xtype: 'eyebutton',
                            handler: 'viewPasswordClick'
                        }
                    ]
                }
            ]
        }
    ]
})