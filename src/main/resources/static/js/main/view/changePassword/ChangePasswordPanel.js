Ext.define('Main.view.changePassword.ChangePasswordPanel', {
    xtype: 'changepasswordpanel',
    extend: 'Main.view.components.FramePanel',
    requires: [
          'Main.view.components.button.SaveButton'
    ],

    title: {
        xtype: 'mainframetitle',
        text: 'Смена пароля'
    },
    layout: {
        type: 'vbox',
        align : 'center'
    },
    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'center'
            },
            padding: '0 0 10 0',
            items: [
                {
                    xtype: 'textfield',
                    labelWidth: 130,
                    labelAlign: 'right',
                    msgTarget: 'side',
                    id: 'passwordInput',
                    fieldLabel: 'Новый пароль',
                    name: 'password',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validateNotEmptyAndLettersAndNumber
                },
                {
                    xtype: 'button',
                    iconCls: 'fas fa-eye noColor-noBorderBtn',
                    cls: 'noColor-noBorderBtn',
                    handler: function (btn) {
                        let val = btn.up('container').items.items[0].inputEl.dom.type
                        if (val === 'password') {
                            btn.up('container').items.items[0].inputEl.dom.type = 'text'
                            this.setIconCls('fas fa-eye-slash noColor-noBorderBtn')
                        } else {
                            btn.up('container').items.items[0].inputEl.dom.type = 'password'
                            this.setIconCls('fas fa-eye noColor-noBorderBtn')
                        }
                    }
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'center'
            },
            padding: '0 0 10 0',
            items: [
                {
                    xtype: 'textfield',
                    labelWidth: 130,
                    labelAlign: 'right',
                    msgTarget: 'side',
                    fieldLabel: 'Повторите пароль',
                    emptyText: 'Введите пароль',
                    inputType: 'password',
                    minLength: 8,
                    validator: ValidatorUtil.validatePasswordConfirm
                },
                {
                    xtype: 'button',
                    iconCls: 'fas fa-eye noColor-noBorderBtn',
                    cls: 'noColor-noBorderBtn',
                    handler: function (btn) {
                        let val = btn.up('container').items.items[0].inputEl.dom.type
                        if (val === 'password') {
                            btn.up('container').items.items[0].inputEl.dom.type = 'text'
                            this.setIconCls('fas fa-eye-slash noColor-noBorderBtn')
                        } else {
                            btn.up('container').items.items[0].inputEl.dom.type = 'password'
                            this.setIconCls('fas fa-eye noColor-noBorderBtn')
                        }
                    }
                }
            ]
        },
        {
            xtype: 'savebutton',
            handler: function (btn) {
                let input = ExtUtil.idQuery('passwordInput')
                if (!input.isValid()) {
                    Ext.Msg.alert('Внимание', 'Неверно заполнена форма.')
                    return
                }
                Ext.Ajax.request({
                    url: '/web/registration/changePassword',
                    params: {
                        password: input.getValue()
                    },
                    success: function (rs) {
                        let response = Ext.JSON.decode(rs.responseText)
                        if (response.success) {
                            Ext.Msg.alert('Сохранено', 'Пароль успешно обновлен.')
                        } else {
                            Ext.Msg.alert('Ошибка', 'Ошибки при обновлении пароля')
                        }
                    }
                })
            }
        }
    ]
})