Ext.define('Login.view.LoginPanel', {
    xtype: 'loginpanel',
    extend: 'Ext.form.Panel',
    // controller: 'loginController',
    title: 'Вход',
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
            id: 'loginForm',
            padding: '20 20 20 20',
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
                        return true
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
                }
            ],
            buttonAlign: 'center',
            buttons: [
                {
                    iconCls: 'fa-solid fa-right-to-bracket',
                    text: 'Вход',
                    handler: 'registerUser'
                }
            ]
        }
    ]
})