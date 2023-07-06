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
                xtype: 'textfield',
                labelWidth: 130,
                labelAlign: 'right',
                msgTarget: 'side'
            },
            items: [
                {
                    fieldLabel: 'Логин',
                    name: 'username',
                    emptyText: 'Введите логин',
                    minLength: 4,
                    validator: ValidatorUtil.validateLogin
                },
                {
                    id: 'passwordInput',
                    fieldLabel: 'Пароль',
                    name: 'password',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validateNotEmptyAndLettersAndNumber
                },
                {
                    fieldLabel: 'Повторите пароль',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validatePasswordConfirm
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