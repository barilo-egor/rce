Ext.define('Main.view.changePassword.ChangePasswordController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.changePasswordController',

    viewPasswordClick: function (btn) {
        let val = btn.up('container').items.items[0].inputEl.dom.type
        if (val === 'password') {
            btn.up('container').items.items[0].inputEl.dom.type = 'text'
            btn.setIconCls('fas fa-eye-slash noColor-noBorderBtn')
        } else {
            btn.up('container').items.items[0].inputEl.dom.type = 'password'
            btn.setIconCls('fas fa-eye noColor-noBorderBtn')
        }
    },

    saveButtonClick: function () {
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
})