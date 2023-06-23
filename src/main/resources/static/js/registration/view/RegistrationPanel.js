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
                    emptyText: 'Введите логин',
                    minLength: 4,
                    validator: function (val) {
                        if (RegexUtil.onlyLettersAndNumbers(val)) return true;
                        else return 'Только латинские буквы и цифры.'
                    },
                    msgTarget: 'side'
                },
                {
                    xtype: 'textfield',
                    id: 'passwordInput',
                    fieldLabel: 'Пароль',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: function (val) {
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
                        if (passwordInput.value !== val) return 'Пароли не совпадают'
                        return true
                    },
                    msgTarget: 'side',
                },
                {
                    xtype: 'button',
                    iconCls: 'fa-regular fa-user',
                    text: 'Регистрация',
                    handler: 'ok'
                }
            ]
        }
    ]
})