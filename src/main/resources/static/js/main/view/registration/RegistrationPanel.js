Ext.define('Main.view.registration.RegistrationPanel', {
    xtype: 'registrationpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'registrationController',
    requires: [
        'Main.view.components.button.RegisterButton'
    ],
    title: {
        xtype: 'mainframetitle',
        text: 'Регистрация веб-пользователей'
    },
    region: 'center',
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            id: 'registrationForm',
            padding: '20 20 20 20',
            controller: 'registrationController',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                labelWidth: 130,
                labelAlign: 'right'
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Логин',
                    name: 'username',
                    emptyText: 'Введите логин',
                    minLength: 4,
                    validator: ValidatorUtil.validateLogin,
                    msgTarget: 'side'
                },
                {
                    xtype: 'textfield',
                    id: 'passwordInput',
                    fieldLabel: 'Пароль',
                    name: 'password',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validateNotEmptyAndLettersAndNumber,
                    msgTarget: 'side',
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Повторите пароль',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validatePasswordConfirm,
                    msgTarget: 'side',
                }
            ],
            buttonAlign: 'center',
            buttons: [
                {
                    xtype: 'registerbutton',
                    handler: 'registerUser'
                }
            ]
        }
    ]
})