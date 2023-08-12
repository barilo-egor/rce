Ext.define('Registration.view.RegistrationPanel', {
    xtype: 'registrationpanel',
    extend: 'Ext.form.Panel',
    requires: [
        'Registration.view.RegistrationController'
    ],
    controller: 'registrationController',
    title: 'Регистрация',
    header: {
        titleAlign: 'center'
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
            padding: '20 20 5 20',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            defaults: {
                xtype: 'textfield',
                labelWidth: 70,
                labelAlign: 'right',
                minWidth: 350,
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
                },
            ],
            buttonAlign: 'center',
            buttons: [
                {
                    xtype: 'button',
                    iconCls: 'fas fa-save',
                    text: 'Регистрация',
                    handler: 'registerUser'
                }
            ]
        },
    ]
})