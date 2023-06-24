Ext.define('Registration.view.RegistrationPanel', {
    xtype: 'registrationpanel',
    extend: 'Ext.form.Panel',
    controller: 'registrationController',
    title: 'Регистрация нового пользователя',
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
            padding: '20 20 20 20',
            controller: 'registrationController',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Логин',
                    name: 'username',
                    emptyText: 'Введите логин',
                    minLength: 4,
                    validator: function (val) {
                        if (!val) return 'Введите значение'
                        if (RegexUtil.onlyLettersAndNumbers(val)) return true;
                        else return 'Только латинские буквы и цифры.'
                    },
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
                    validator: function (val) {
                        if (!val) return 'Введите значение'
                        if (RegexUtil.onlyLettersAndNumbers(val)) return true;
                        else return 'Только латинские буквы и цифры.'
                    },
                    msgTarget: 'side',
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Повторите пароль',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: function (val) {
                        let passwordInput = Ext.ComponentQuery.query('[id=passwordInput]')[0]
                        if (!val) return 'Введите значение'
                        if (passwordInput.value !== val) return 'Пароли не совпадают'
                        return true
                    },
                    msgTarget: 'side',
                }
            ],
            buttons: [
                {
                    iconCls: 'fa-regular fa-user',
                    text: 'Регистрация',
                    handler: 'registerUser'
                }
            ]
        }
    ]
})