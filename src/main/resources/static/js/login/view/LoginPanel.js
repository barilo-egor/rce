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
            padding: '20 20 5 20',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                labelWidth: 70,
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
            ]
        },
        {
            xtype: 'container',
            id: 'errorLoginContainer',
            hidden: true,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            height: 30,
            items: [
                {
                    xtype: 'panel',
                    html: '<i class="fa-solid fa-circle-exclamation" style="color: #ff0000;"></i> Неверный логин либо пароль.',
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            height: 30,
            items: [
                {
                    xtype: 'button',
                    iconCls: 'fa-solid fa-right-to-bracket',
                    text: 'Вход',
                    width: 150,
                    handler: function () {
                        let form = Ext.ComponentQuery.query('[id=loginForm]')[0]
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/login',
                            async: false,
                            params: form.getValues(),
                            success: function (rs) {
                                let response = Ext.JSON.decode(rs.responseText)
                                let errorLoginContainer = Ext.ComponentQuery.query('[id=errorLoginContainer]')[0]
                                if (response.error) errorLoginContainer.show()
                                else if (response.loginSuccess) {
                                    errorLoginContainer.hide()
                                    document.location.href = response.loginUrl
                                }
                            }
                        })
                    }
                }
            ]
        }
    ]
})