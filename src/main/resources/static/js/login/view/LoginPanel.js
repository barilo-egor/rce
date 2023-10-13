Ext.define('Login.view.LoginPanel', {
    xtype: 'loginpanel',
    extend: 'Ext.form.Panel',
    requires: ['Login.controller.LoginController'],
    controller: 'loginController',
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
    listeners: {
        afterrender: function (me) {
            ExtUtil.idQuery('loginInput').focus()
        }
    },
    items: [
        {
            xtype: 'form',
            id: 'loginForm',
            url: '/web/main',
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
                    id: 'loginInput',
                    name: 'username',
                    emptyText: 'Введите логин',
                    minLength: 4,
                    validator: ValidatorUtil.validateNotEmptyAndLettersAndNumber,
                    msgTarget: 'side',
                    listeners: {
                        specialkey: 'specialKeyPress'
                    }
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
                    listeners: {
                        specialkey: 'specialKeyPress'
                    }
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
                    iconCls: 'fas fa-sign-in-alt',
                    text: 'Вход',
                    width: 150,
                    handler: 'login'
                }
            ]
        }
    ]
})