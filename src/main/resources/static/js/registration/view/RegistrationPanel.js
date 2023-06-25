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
    tbar: [
        {
            xtype: 'mainpagebutton',
        }
    ],
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
                    validator: function (val) {
                        if (!val) return 'Введите значение'
                        if (!RegexUtil.onlyLettersAndNumbers(val)) return 'Только латинские буквы и цифры.'
                        let result = true
                        Ext.Ajax.request({
                            url: '/web/registration/isUsernameFree',
                            method: 'GET',
                            params: {
                                username: val
                            },
                            async: false,
                            success: function(rs) {
                                let response = Ext.JSON.decode(rs.responseText)
                                if (!response.result) result = 'Данный логин уже занят'
                            },
                            failure: function () {
                                Ext.Msg.alert('Ошибка', 'Ошибка при попытке проверки логина.')
                            }
                        })
                        return result
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
            buttonAlign: 'center',
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